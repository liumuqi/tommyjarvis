package sys

import (
	"fmt"
	"log"
	"strconv"
	"strings"
	"sync"
	"time"

	"github.com/garyburd/redigo/redis"
	"mogujie.com/remosis/actions/email"
	"mogujie.com/remosis/config"
)

var nodeInstances map[string]*redis.Pool

//CommandChan 控制指令string
var CommandChan chan string

func init() {
	nodeInstances = make(map[string]*redis.Pool)
	CommandChan = make(chan string, 1)
}

const (
	m string = "->-" // migrating slot
	i string = "-<-" // importing slot
)

//RedisSloting 正在迁移的slot
type RedisSloting struct {
	Slot   string
	NodeID string
	Status string
}

//RedisNode cluster nodes 信息
type RedisNode struct {
	Ipport  string          `json:"ipport"`
	NodeID  string          `json:"nodeId"`
	MsType  string          `json:"msType"`
	SlaveOf string          `json:"slaveOf"`
	Ping    string          `json:"ping"`
	Pong    string          `json:"pong"`
	Epoch   string          `json:"epoch"`
	Status  string          `json:"status"`
	Slots   []string        `json:"slots"`
	Sloting []*RedisSloting `json:"redisSloting"`
}

//Redises 控制类
type Redises struct {
	sync.RWMutex
	emailContent string
}

//RedisAliveCheck redis 实例存货监控
func (r *Redises) RedisAliveCheck(conf *config.Config, errChan chan string) {
	defer func() {
		if err := recover(); err != nil {
			log.Println("[sys/redis.go:RedisAliveCheck] recover error::", err)
		}
	}()
	var result string
	for _, v := range conf.Redises {
		ipport := v.Ip + ":" + strconv.Itoa(v.Port)
		rs := r.ExecRedisCmd(ipport, "PING")
		if len(rs) != 1 || rs[0] != "PONG" || rs[0] == "" {
			result = result + "," + ipport
		}
	}
	if result != "" {
		email.MSendMail("redis 不存活报警", []byte(result))
		errChan <- string(result)
	}
}

//GetReidsInfo 获取reids基本信息
func (r *Redises) GetReidsInfo(conf *config.Config) map[string]*map[string]*map[string]string {
	var rv = make(map[string]*map[string]*map[string]string)
	redises := conf.Redises
	for _, v := range redises {
		ipport := v.Ip + ":" + strconv.Itoa(v.Port)
		info := r.ExecRedisCmd(ipport, "info")
		clusterinfo := r.ExecRedisCmd(ipport, "cluster", "info")
		var redisinfosClusterinfo = make(map[string]string)
		clusterinfofields := strings.Split(clusterinfo[0], "\r\n")
		for i := 1; i < len(clusterinfofields); i++ {
			v := clusterinfofields[i]
			if strings.Contains(v, ":") && !strings.Contains(v, "CLUSTER ") {
				r := strings.Split(v, ":")
				redisinfosClusterinfo[r[0]] = r[1]
			}
		}
		var redisinfosServer = make(map[string]string)
		var redisinfosClients = make(map[string]string)
		var redisinfosMem = make(map[string]string)
		var redisinfosPersis = make(map[string]string)
		var redisinfosStats = make(map[string]string)
		var redisinfosRepli = make(map[string]string)
		var redisinfosCPU = make(map[string]string)
		var redisinfosCluster = make(map[string]string)
		var redisinfosKeyspace = make(map[string]string)
		var redisinfoReturn = make(map[string]*map[string]string)
		fields := strings.Split(info[0], "\r\n")
		var redisinfos *map[string]string
		upgrade := -1
		for i := 0; i < len(fields); i++ {
			v := fields[i]
			v = strings.TrimSpace(v)
			if strings.Index(v, "Server") >= 0 && len(v) > 1 && i > upgrade {
				upgrade = i
				redisinfosServer["ip"] = conf.Server.Ip
				redisinfosServer["hostName"] = conf.Server.Name
				redisinfosServer["collectTime"] = strconv.FormatInt(time.Now().Unix(), 10)
				redisinfos = &redisinfosServer
			} else if strings.Index(v, "Clients") >= 0 && len(v) > 1 && i > upgrade {
				upgrade = i
				redisinfos = &redisinfosClients
			} else if strings.Index(v, "Memory") >= 0 && len(v) > 1 && i > upgrade {
				upgrade = i
				redisinfos = &redisinfosMem
			} else if strings.Index(v, "Persistence") >= 0 && len(v) > 1 && i > upgrade {
				upgrade = i
				redisinfos = &redisinfosPersis
			} else if strings.Index(v, "Stats") >= 0 && len(v) > 1 && i > upgrade {
				upgrade = i
				redisinfos = &redisinfosStats

			} else if strings.Index(v, "Replication") >= 0 && len(v) > 1 && i > upgrade {
				upgrade = i
				redisinfos = &redisinfosRepli
			} else if strings.Index(v, "CPU") >= 0 && len(v) > 1 && i > upgrade {
				upgrade = i
				redisinfos = &redisinfosCPU
			} else if strings.Index(v, "Cluster") >= 0 && len(v) > 1 && i > upgrade {
				upgrade = i
				redisinfos = &redisinfosCluster
			} else if strings.Index(v, "Keyspace") >= 0 && len(v) > 1 && i > upgrade {
				upgrade = i
				redisinfos = &redisinfosKeyspace
			}
			if strings.Contains(v, ":") && !strings.Contains(v, "Server") && redisinfos != nil {
				r := strings.SplitN(v, ":", 2)
				(*redisinfos)[r[0]] = r[1]
			}
		}
		redisinfoReturn["server"] = &redisinfosServer
		redisinfoReturn["clients"] = &redisinfosClients
		redisinfoReturn["memory"] = &redisinfosMem
		redisinfoReturn["persistence"] = &redisinfosPersis
		redisinfoReturn["stats"] = &redisinfosStats
		redisinfoReturn["replication"] = &redisinfosRepli
		redisinfoReturn["cpu"] = &redisinfosCPU
		redisinfoReturn["cluster"] = &redisinfosCluster
		redisinfoReturn["clusterInfo"] = &redisinfosClusterinfo
		redisinfoReturn["keyspace"] = &redisinfosKeyspace
		rv[ipport] = &redisinfoReturn
	}
	return rv
}

//ExecRedisCmd 执行redis command
func (r *Redises) ExecRedisCmd(ipport, cmd string, arg ...interface{}) []string {
	defer func() {
		if err := recover(); err != nil {
			log.Println("[sys/redis.go:ExecRedisCmd] error", err)
			es := fmt.Sprint(err)
			email.MSendMail("[sys/redis.go:ExecRedisCmd] error", []byte(es))
		}
	}()
	rc := r.getPooledRedisClient(ipport)
	defer rc.Close()
	reply, err := rc.Do(cmd, arg...)
	if err != nil {
		es := fmt.Sprint(cmd, arg, "  run error!:>>>", err)
		return []string{es}
	}
	var result []string
	switch t := reply.(type) {
	default:
		log.Printf("%s  命令 %s,  redis 返回输出类型是:%T", cmd, arg, t)
		result = []string{}
	case []byte:
		result = []string{string(reply.([]byte))}
	case string:
		result = []string{reply.(string)}
	case []interface{}:
		is := reply.([]interface{})
		for _, v := range is {
			result = append(result, string(v.([]byte)))
		}
	}
	return result
}

//newPool create redis server pool connections
func (r *Redises) newPool(server, password string) *redis.Pool {
	return &redis.Pool{
		MaxIdle:     3,
		IdleTimeout: 240 * time.Second,
		Dial: func() (redis.Conn, error) {
			c, err := redis.Dial("tcp", server)
			if err != nil {
				return nil, err
			}
			if len(password) > 0 {
				if _, err := c.Do("AUTH", password); err != nil {
					c.Close()
					return nil, err
				}
			}
			return c, err
		},
		TestOnBorrow: func(c redis.Conn, t time.Time) error {
			_, err := c.Do("PING")
			return err
		},
	}
}

//get redis info from config
func (r *Redises) getPooledRedisClient(ipport string) redis.Conn {
	r.RLock()
	_, ok := nodeInstances[ipport]
	r.RUnlock()
	if !ok {
		pool := r.newPool(ipport, "")
		r.Lock()
		nodeInstances[ipport] = pool
		r.Unlock()
	}
	return nodeInstances[ipport].Get()
}

//返回值：sourceipport,desipport,slots,是否有效
//T@10.13.128.221:6666|10232-11123->10.13.128.221:6667
func checkCommandValid(c string) (sipport string, des string, slots []string, flag bool) {
	cdetail := strings.Split(c, "@")
	sd := strings.Split(cdetail[1], "->")
	if len(sd) != 2 {
		email.MSendMail("[sys/redis.go:checkCommandValid] reveive command string is not valid", []byte(c))
		return "", "", nil, false
	}
	source := sd[0]
	des = sd[1]
	if strings.Count(source, "|") < 1 {
		email.MSendMail("[sys/redis.go:checkCommandValid] reveive command string is not valid", []byte(c))
	}
	temp := strings.Split(source, "|")
	sipport = temp[0]
	slots = temp[1:]
	for _, v := range slots {
		ss := strings.Split(v, "-")
		if len(ss) > 2 {
			return "", "", nil, false
		} else if len(ss) == 2 {
			i, e := strconv.Atoi(ss[0])
			if e != nil {
				return "", "", nil, false
			}
			ii, e := strconv.Atoi(ss[1])
			if e != nil {
				return "", "", nil, false
			}
			if i >= ii {
				return "", "", nil, false
			}
		}
	}
	return sipport, des, slots, true
}

//DealCommandChan 处理socket 获取的 转移slot指令
func (r *Redises) DealCommandChan() {
	go func() {
		for v := range CommandChan {
			fmt.Printf("receive command chan string --->%s\n", v)
			switch v[0] {
			//v : T@10.13.128.221:6666|10232-11123->10.13.128.221:6667
			case 'T':
				sipport, desipport, slots, flag := checkCommandValid(v)
				if flag {
					r.DealTransSlot(sipport, desipport, slots)
				} else {
					email.MSendMail("[sys/redis.go:checkCommandValid] reveive command string is not valid", []byte(v))
				}
			}
		}
	}()
}

//DealTransSlot 迁移指令处理函数
func (r *Redises) DealTransSlot(sipport string, dip string, slots []string) {
	defer func() {
		if err := recover(); err != nil {
			log.Println("[sys/redis.go:transSlot] error", err)
			es := fmt.Sprint(err)
			email.MSendMail("[sys/redis.go:transSlot] error", []byte(es))
		}
	}()
	r.emailContent = time.Now().String() + "\n"
	rs := r.ExecRedisCmd(sipport, "cluster", "nodes")
	wm := wrapRedisNode(rs[0])
	//v : T@10.13.128.221:6666|10232-11123->10.13.128.221:6667
	r.emailContent = fmt.Sprint(r.emailContent, sipport, " to destination ip:", dip, "   slots:", slots, " \nGet cluster nodes info:\n", rs, "\n\n>>>>>>>>>>>>>>>>>>begin process>>>>>>>>>>>>>>>\n\n")
	r.processNodesInfo(sipport, dip, wm, slots...)
	r.emailContent = fmt.Sprint(r.emailContent, " \n\n done!!!!!!!!!!!!!!!!!!!!!!!!!!")
	email.MSendMail("[sys/redis.go:DealTransSlot] transinfo", []byte(r.emailContent))
	//fmt.Printf("%s\n", r.emailContent)
}

func (r *Redises) checkSlotIn(pslots []string, sourceNode *RedisNode, desNode *RedisNode) bool {
	slots := sourceNode.Slots
	sloting := sourceNode.Sloting
	if !strings.Contains(sourceNode.MsType, "master") {
		r.emailContent = fmt.Sprint(r.emailContent, " \n\n does not master \n\n", sourceNode)
		return false
	}
	if !strings.Contains(desNode.MsType, "master") {
		r.emailContent = fmt.Sprint(r.emailContent, " \n\n does not master \n\n", desNode)
		return false
	}
	if sourceNode.NodeID == desNode.NodeID {
		r.emailContent = fmt.Sprint(r.emailContent, " \n\n source == des  \n\n", desNode)
		return false
	}
	if len(slots) == 0 && len(sloting) == 0 {
		r.emailContent = fmt.Sprint(r.emailContent, " \n\n slots or sloting is none  \n\n")
		return false
	}
	for _, slot := range pslots {
		sc := strings.Split(slot, "-")
		for _, v := range slots {
			sf := strings.Split(v, "-")
			if len(sf) == 2 && len(sc) == 2 {
				sc0, _ := strconv.Atoi(sc[0])
				sc1, _ := strconv.Atoi(sc[1])
				sf0, _ := strconv.Atoi(sf[0])
				sf1, _ := strconv.Atoi(sf[1])
				if sc0 >= sf0 && sc1 <= sf1 {
					return true
				}
			} else if len(sf) == 2 && len(sc) == 1 {
				sf0, _ := strconv.Atoi(sf[0])
				sf1, _ := strconv.Atoi(sf[1])
				sc0, _ := strconv.Atoi(sc[0])
				if sc0 >= sf0 && sc0 < sf1 {
					return true
				}
			} else if len(sf) == 1 && len(sc) == 1 {
				if sf[0] == sc[0] {
					return true
				}
			}
		}
		if len(sc) == 1 {
			for _, sing := range sloting {
				if sing.Slot == sc[0] {
					return true
				}
			}
		}
	}
	r.emailContent = fmt.Sprint(r.emailContent, " \n\n checkslotin all checked  but not found \n\n")
	return false
}

func (r *Redises) processNodesInfo(key string, dip string, nodes *map[string]*RedisNode, slots ...string) {
	sourcem, ok1 := (*nodes)[key]
	destm, ok2 := (*nodes)[dip]
	if ok1 && ok2 {
		cb := r.checkSlotIn(slots, sourcem, destm)
		if !cb {
			return
		}
		for _, sid := range slots {
			mima := strings.Split(sid, "-")
			if len(mima) == 2 {
				mi, _ := strconv.Atoi(mima[0])
				ma, _ := strconv.Atoi(mima[1])
				for i := mi; i <= ma; i++ {
					r.transfering(sourcem, destm, strconv.Itoa(i), nodes)
				}
			} else if len(mima) == 1 {
				r.transfering(sourcem, destm, mima[0], nodes)
			}
		}
	}
}

func (r *Redises) transfering(source *RedisNode, des *RedisNode, slot string, notifyNodes *map[string]*RedisNode) {
	sip := source.Ipport
	suuid := source.NodeID
	dip := des.Ipport
	duuid := des.NodeID
	step1 := r.ExecRedisCmd(dip, "cluster", "setslot", slot, "importing", suuid)
	r.emailContent = fmt.Sprint(r.emailContent, slot, "--step1+++++++++++++++++++++++++>>>>>", step1, "\n")
	step2 := r.ExecRedisCmd(sip, "cluster", "setslot", slot, "migrating", duuid)
	r.emailContent = fmt.Sprint(r.emailContent, slot, "--step2+++++++++++++++++++++++++>>>>>", step2, "\n")
	dp := strings.Split(dip, ":")
	var keyCountInslot int
	for {
		step3 := r.ExecRedisCmd(sip, "cluster", "getkeysinslot", slot, "8000")
		if len(step3) < 1 || step3[0] == "" {
			break
		}
		for _, k := range step3 {
			keyCountInslot++
			step4 := r.ExecRedisCmd(sip, "migrate", dp[0], dp[1], k, 0, 15000)
			if strings.Contains(step4[0], "BUSYKEY") {
				r.ExecRedisCmd(sip, "migrate", dp[0], dp[1], k, 0, 15000, "replace")
			}
		}
	}
	r.emailContent = fmt.Sprint(r.emailContent, slot, "--step3+++++++++++++++++++++++++migrating keys count:>>>>>", keyCountInslot, "\n")
	for _, v := range *notifyNodes {
		noti := r.ExecRedisCmd(v.Ipport, "cluster", "setslot", slot, "node", duuid)
		r.emailContent = fmt.Sprint(r.emailContent, slot, "-->", v.Ipport, "--notify+++++++++++++++++++++++++>>>>>", noti, "\n")
	}
}

func wrapRedisNode(nodestring string) *map[string]*RedisNode {
	nodesInfoMap := make(map[string]*RedisNode)
	for _, v := range strings.Split(nodestring, "\n") {
		vs := strings.Split(v, " ")
		if len(vs) > 7 {
			node := &RedisNode{}
			node.NodeID = vs[0]
			node.Ipport = vs[1]
			node.MsType = vs[2]
			node.SlaveOf = vs[3]
			node.Ping = vs[4]
			node.Pong = vs[5]
			node.Epoch = vs[6]
			node.Status = vs[7]
			if len(vs) >= 8 {
				ts := vs[8:]
				for ti, sl := range ts {
					if strings.Contains(sl, m) {
						sl = strings.Trim(sl, "[")
						sl = strings.Trim(sl, "]")
						slt := strings.Split(sl, m)
						rsing := &RedisSloting{Status: m, Slot: slt[0], NodeID: slt[1]}
						node.Sloting = append(node.Sloting, rsing)
					} else if strings.Contains(sl, i) {
						sl = strings.Trim(sl, "[")
						sl = strings.Trim(sl, "]")
						slt := strings.Split(sl, m)
						rsing := &RedisSloting{Status: i, Slot: slt[0], NodeID: slt[1]}
						node.Sloting = append(node.Sloting, rsing)
					} else {
						node.Slots = append(node.Slots, ts[ti])
					}
				}
			}
			nodesInfoMap[node.Ipport] = node
		}
	}
	return &nodesInfoMap
}

//GetClusterNodes 获取cluster nodes信息
func (r *Redises) GetClusterNodes(ipport string) *map[string]*RedisNode {
	rs := r.ExecRedisCmd(ipport, "cluster", "nodes")
	rm := wrapRedisNode(rs[0])
	return rm
}
