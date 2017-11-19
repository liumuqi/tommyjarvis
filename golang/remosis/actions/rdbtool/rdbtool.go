package rdbtool

import (
	"os"

	"mogujie.com/remosis/sys"

	"github.com/cupcake/rdb"
	"github.com/cupcake/rdb/nopdecoder"
)

//SlotInfo rdb中每个slot下的key的数量和大小
type SlotInfo struct {
	SlotSize int
	KeyCount int
}
type decoder struct {
	db   int
	i    int
	port int
	nopdecoder.NopDecoder
	totalInfo map[int]*SlotInfo
}

func (p *decoder) StartDatabase(n int) {
	p.db = n
}
func (p *decoder) calc(key, value []byte) {
	i := int(sys.Crc16(string(key)))
	tsm := p.totalInfo
	si := tsm[i]
	if si == nil {
		tsm[i] = &SlotInfo{}
		si = tsm[i]
	}
	si.KeyCount++
	si.SlotSize += len(key)
	si.SlotSize += len(value)
}

func (p *decoder) Set(key, value []byte, expiry int64) {
	p.calc(key, value)
}

func (p *decoder) Hset(key, field, value []byte) {
	p.calc(key, value)
}

func (p *decoder) Sadd(key, member []byte) {
	p.calc(key, member)
}

func (p *decoder) StartList(key []byte, length, expiry int64) {
	p.i = 0
}

func (p *decoder) Rpush(key, value []byte) {
	p.calc(key, value)
	p.i++
}

func (p *decoder) StartZSet(key []byte, cardinality, expiry int64) {
	p.i = 0
}

func (p *decoder) Zadd(key []byte, score float64, member []byte) {
	p.calc(key, member)
	p.i++
}

//ParseRdb 解析rdb文件获取相关信息
func ParseRdb(path string) map[int]*SlotInfo {
	file, e := os.Open(path)
	if e == nil {
		si := make(map[int]*SlotInfo)
		rdb.Decode(file, &decoder{totalInfo: si})
		return si
	}
	return nil
}
