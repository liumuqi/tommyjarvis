package server

import (
	"encoding/json"
	"fmt"
	"log"
	"net"
	"regexp"
	"strconv"
	"strings"
	"unicode/utf8"

	"mogujie.com/remosis/sys"
)

var findip, _ = regexp.Compile(`\d+\.\d+\.\d+\.\d+:\d+`)

//SocketServer socketServer 服务类
type SocketServer struct {
	Port    int
	Message chan string
}

func (s *SocketServer) MSocketServer(r *sys.Redises) {
L1:
	p := strconv.Itoa(s.Port)
	if p == "0" || p == "" {
		p = "33002"
	}
	log.Printf("监听端口 %s >>>>>>>>>>>>>>>>>\n", p)
	tcpAddr, err := net.ResolveTCPAddr("tcp4", ":"+p)
	if err != nil {
		log.Printf("本地初始化端口 %s 失败\n", err)
		return
	}
	tcplistener, err := net.ListenTCP("tcp", tcpAddr)
	defer tcplistener.Close()
	if err != nil {
		log.Printf("监听端口 %s 失败\n", p, err)
		return
	}
	for {
		if s.Port != tcpAddr.Port {
			goto L1
		}
		conn, err := tcplistener.Accept()
		if err != nil {
			log.Printf("%s,建立连接失败\n", conn.RemoteAddr().String(), err)
			continue
		}
		go s.handlerConn(conn, r)
	}
}
func (s *SocketServer) handlerConn(conn net.Conn, r *sys.Redises) {
	defer func() {
		if err := recover(); err != nil {
			log.Println("[server/msocket.go:handlerConn] error", err)
			fmt.Println("[msocket.go:handlerConn] recover error: :", err)
		}
	}()
	//conn.SetDeadline(time.Now().Add(5 * time.Second))
	//conn.SetReadDeadline(time.Now().Add(2 * time.Second))
	//conn.SetWriteDeadline(time.Now().Add(2 * time.Second))
	defer conn.Close()
	cmdbytes := make([]byte, 100)
	cmdstr := ""
RL:
	for {
		readLen, err := conn.Read(cmdbytes)
		if err != nil {
			fmt.Println("[msocket.go:handlerConn]receive redis cmd error: :", string(cmdbytes), " error:", err)
			break
		}
		if readLen == 0 {
			break // connection already closed by client
		}
		//####################需要特殊处理字符串，找到每一个字符，然后累加。####################
		for i := 0; i < len(cmdbytes); i++ {
			if cmdbytes[i] == 0 {
				break
			} else {
				cmdstr += string(cmdbytes[i])
			}
		}
		//判断结尾字符串
		var cstemp []string
		if strings.Contains(cmdstr, "\r\n") {
			cstemp = strings.Split(cmdstr, "\r\n")
		}
		if len(cstemp) == 0 {
			break
		} else if len(cstemp[0]) < 1 {
			break
		}
		switch cstemp[0][0] {
		case 'T':
			//发送给goroutine 后台执行迁移操作，如有错发错误邮件
			sys.CommandChan <- cstemp[0]
			conn.Write([]byte(`{"success":"done"}`))
			conn.Close()
		case 'Q':
			//查询redis nodes 信息
			cdetail := strings.Split(cstemp[0], "@")
			if len(cdetail) < 2 || strings.Count(cdetail[1], ":") != 1 {
				break RL
			}
			nodesInfoMap := r.GetClusterNodes(cdetail[1])
			rd, er := json.Marshal(nodesInfoMap)
			if er != nil {
				log.Println("转化json 错误---->", nodesInfoMap)
			} else {
				log.Println("====================>", string(rd))
				conn.Write(rd)
			}
			conn.Close()
		case 'G':
			cdetail := strings.Split(cstemp[0], "@")
			if len(cdetail) < 2 || utf8.RuneCountInString(cdetail[1]) < 1 {
				break RL
			}
			ipp := strings.Split(cdetail[1], "|")
			if len(ipp) != 2 {
				break RL
			}
			rs := r.ExecRedisCmd(ipp[0], "get", ipp[1])
			if strings.Contains(rs[0], "MOVED") {
				ipport := findip.FindString(rs[0])
				rs = r.ExecRedisCmd(ipport, "get", ipp[1])
			}
			result := make(map[string]string)
			result[ipp[1]] = rs[0]
			rd, er := json.Marshal(result)
			if er != nil {
				log.Println("====================>", string(rd))
			} else {
				conn.Write(rd)
				conn.Close()
			}
		default:
			conn.Close()
		}
	}
}
