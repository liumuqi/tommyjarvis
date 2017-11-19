package sys

import (
	"fmt"
	"strconv"
	"strings"
	"syscall"
	"time"

	sigar "github.com/cloudfoundry/gosigar"
	"mogujie.com/remosis/config"
)

var sig = sigar.ConcreteSigar{}
var cpu sigar.Cpu
var preStat *SysStat

func init() {
	go func() {
		cpuchan, _ := sig.CollectCpuStats(time.Second * 1)
		for {
			select {
			case cpu = <-cpuchan:
			case <-time.After(2 * time.Second):
			}
		}
	}()
}

type SysStat struct {
	//node config
	Ip          string  `json:"ip"`
	HostName    string  `json:"hostName"`
	CollectTime int64   `json:"collectTime"`
	Load1       float64 `json:"load1"`
	Load5       float64 `json:"load5"`
	Load15      float64 `json:"load15"`
	Total       uint64  `json:"total"`
	Available   uint64  `json:"available"`
	Used        uint64  `json:"used"`
	UsedPercent float64 `json:"usedPercent"`
	Free        uint64  `json:"free"`
	Active      uint64  `json:"active"`
	Inactive    uint64  `json:"inactive"`
	Buffers     uint64  `json:"buffers"`
	Cached      uint64  `json:"cached"`
	Wired       uint64  `json:"wired"`
	Shared      uint64  `json:"shared"`
	// sum of all interfaces
	Name        string  `json:"name"`
	BytesSent   uint64  `json:"bytesSent"`
	BytesRecv   uint64  `json:"bytesRecv"`
	PacketsSent uint64  `json:"packetsSent"`
	PacketsRecv uint64  `json:"packetsRecv"`
	CPU         string  `json:"cpu"`
	User        float64 `json:"user"`
	System      float64 `json:"system"`
	Idle        float64 `json:"idle"`
	Nice        float64 `json:"nice"`
	Iowait      float64 `json:"iowait"`
	Irq         float64 `json:"irq"`
	Softirq     float64 `json:"softirq"`
	Steal       float64 `json:"steal"`
	Guest       float64 `json:"guest"`
	GuestNice   float64 `json:"guestNice"`
	Stolen      float64 `json:"stolen"`
	// disk usage
	DiskUsages []*DiskUsageStat
}
type DiskUsageStat struct {
	Path              string
	Total             uint64
	Free              uint64
	Used              uint64
	UsedPercent       float64
	InodesTotal       uint64
	InodesUsed        uint64
	InodesFree        uint64
	InodesUsedPercent float64
}

func (stat *SysStat) subPrev(prev *SysStat) {
	stat.BytesSent -= prev.BytesSent
	stat.BytesRecv -= prev.BytesRecv
	stat.PacketsSent -= prev.PacketsSent
	stat.PacketsRecv -= prev.PacketsRecv
}
func CopyOne(one *SysStat) *SysStat {
	stat := new(SysStat)
	stat.HostName = one.HostName
	stat.Ip = one.Ip
	stat.CollectTime = one.CollectTime
	stat.Load1 = one.Load1
	stat.Load5 = one.Load5
	stat.Load15 = one.Load15
	stat.Total = one.Total
	stat.Available = one.Available
	stat.Used = one.Used
	stat.UsedPercent = one.UsedPercent
	stat.Free = one.Free
	stat.Active = one.Active
	stat.Inactive = one.Inactive
	stat.Buffers = one.Buffers
	stat.Cached = one.Cached
	stat.Wired = one.Wired
	stat.Shared = one.Shared
	stat.Name = one.Name
	stat.BytesSent = one.BytesSent
	stat.BytesRecv = one.BytesRecv
	stat.PacketsSent = one.PacketsSent
	stat.PacketsRecv = one.PacketsRecv
	stat.CPU = one.CPU
	stat.User = one.User
	stat.System = one.System
	stat.Idle = one.Idle
	stat.Nice = one.Nice
	stat.Iowait = one.Iowait
	stat.Irq = one.Irq
	stat.Softirq = one.Softirq
	stat.Steal = one.Steal
	stat.Guest = one.Guest
	stat.GuestNice = one.GuestNice
	return stat
}
func GetSysStat(server *config.Server) (*SysStat, error) {
	stat := new(SysStat)
	stat.HostName = server.Name
	stat.Ip = server.Ip
	stat.CollectTime = time.Now().Unix()
	err := stat.cPUTimes()
	if err != nil {
		return nil, err
	}
	err = stat.netIOCounters()
	if err != nil {
		return nil, err
	}
	err = stat.virtualMemory()
	if err != nil {
		return nil, err
	}
	err = stat.loadAvg()
	//if err != nil {
	//return nil, err
	//}
	/*
	 *err = stat.disk()
	 *if err != nil {
	 *    return nil, err
	 *}
	 */
	return stat, nil
}
func (stat *SysStat) cPUTimes() error {
	tof := float64(cpu.Total())
	stat.User = float64(cpu.User) / tof * 100
	stat.Nice = float64(cpu.Nice) / tof * 100
	stat.System = float64(cpu.Sys) / tof * 100
	stat.Idle = float64(cpu.Idle) / tof * 100
	stat.Iowait = float64(cpu.Wait) / tof * 100
	stat.Irq = float64(cpu.Irq) / tof * 100
	stat.Softirq = float64(cpu.SoftIrq) / tof * 100
	stat.Stolen = float64(cpu.Stolen) / tof * 100
	stat.CPU = fmt.Sprintf("%.3f", (tof-float64(cpu.Idle))/tof*100)
	//stat.Steal = float64(steal)
	//stat.Guest = float64(guest)
	//countTicks += guestNice
	return nil
}

type netIOCountersStat struct {
	Name        string
	BytesSent   uint64
	BytesRecv   uint64
	PacketsSent uint64
	PacketsRecv uint64
}

func (stat *SysStat) netIOCounters() error {
	filename := "/proc/net/dev"
	lines, err := readLines(filename)
	if err != nil {
		return err
	}
	statlen := len(lines) - 1
	all := make([]netIOCountersStat, 0, statlen)
	for _, line := range lines[2:] {
		parts := strings.SplitN(line, ":", 2)
		if len(parts) != 2 {
			continue
		}
		interfaceName := strings.TrimSpace(parts[0])
		if interfaceName == "" {
			continue
		}
		fields := strings.Fields(strings.TrimSpace(parts[1]))
		bytesRecv, err := strconv.ParseUint(fields[0], 10, 64)
		if err != nil {
			return err
		}
		packetsRecv, err := strconv.ParseUint(fields[1], 10, 64)
		if err != nil {
			return err
		}
		bytesSent, err := strconv.ParseUint(fields[8], 10, 64)
		if err != nil {
			return err
		}
		packetsSent, err := strconv.ParseUint(fields[9], 10, 64)
		if err != nil {
			return err
		}
		nic := netIOCountersStat{
			Name:        interfaceName,
			BytesRecv:   bytesRecv,
			PacketsRecv: packetsRecv,
			BytesSent:   bytesSent,
			PacketsSent: packetsSent}
		all = append(all, nic)
	}
	return stat.getNetIOCountersAll(all)
}
func (stat *SysStat) getNetIOCountersAll(n []netIOCountersStat) error {
	stat.Name = "all-interfaces"
	for _, nic := range n {
		stat.BytesRecv += nic.BytesRecv
		stat.PacketsRecv += nic.PacketsRecv
		stat.BytesSent += nic.BytesSent
		stat.PacketsSent += nic.PacketsSent
	}
	if preStat != nil {
		stat.subPrev(preStat)
	}
	preStat = stat
	return nil
}
func (stat *SysStat) virtualMemory() error {
	/*
	 *filename := "/proc/meminfo"
	 *lines, _ := readLines(filename)
	 *for _, line := range lines {
	 *    fields := strings.Split(line, ":")
	 *    if len(fields) != 2 {
	 *        continue
	 *    }
	 *    key := strings.TrimSpace(fields[0])
	 *    value := strings.TrimSpace(fields[1])
	 *    value = strings.Replace(value, " kB", "", -1)
	 *    t, err := strconv.ParseUint(value, 10, 64)
	 *    if err != nil {
	 *        return err
	 *    }
	 *    switch key {
	 *    case "MemTotal":
	 *        stat.Total = t / 1000
	 *    case "MemFree":
	 *        stat.Free = t / 1000
	 *    case "Buffers":
	 *        stat.Buffers = t / 1000
	 *    case "Cached":
	 *        stat.Cached = t / 1000
	 *    case "Active":
	 *        stat.Active = t / 1000
	 *    case "Inactive":
	 *        stat.Inactive = t / 1000
	 *    }
	 *}
	 */
	me, _ := sig.GetMem()
	stat.Total = me.Total
	stat.Free = me.Free
	stat.Available = me.ActualFree
	stat.Used = me.ActualUsed
	stat.UsedPercent = float64(stat.Used) / float64(me.Total) * 100
	//stat.Available = stat.Free + stat.Buffers + stat.Cached
	//stat.Used = stat.Total - stat.Free
	//stat.UsedPercent = float64(stat.Total-stat.Available) / float64(stat.Total) * 100.0
	return nil
}
func (stat *SysStat) loadAvg() error {
	la, _ := sig.GetLoadAverage()
	la.Get()
	stat.Load1 = la.One
	stat.Load5 = la.Five
	stat.Load15 = la.Fifteen
	return nil
}
func (stat *SysStat) disk() error {
	stat.DiskUsages = make([]*DiskUsageStat, 0)
	filename := "/proc/mounts"
	lines, _ := readLines(filename)
	for _, line := range lines {
		devs := strings.Fields(line)
		if strings.Contains(devs[0], "/dev/") {
			devStat, err := diskUsage(devs[1])
			if err != nil {
				return err
			}
			stat.DiskUsages = append(stat.DiskUsages, devStat)
		}
	}
	return nil
}
func diskUsage(path string) (*DiskUsageStat, error) {
	stat := syscall.Statfs_t{}
	err := syscall.Statfs(path, &stat)
	if err != nil {
		return nil, err
	}
	bsize := stat.Bsize
	ret := &DiskUsageStat{
		Path:        path,
		Total:       (uint64(stat.Blocks) * uint64(bsize)),
		Free:        (uint64(stat.Bavail) * uint64(bsize)),
		InodesTotal: (uint64(stat.Files)),
		InodesFree:  (uint64(stat.Ffree)),
	}
	ret.InodesUsed = (ret.InodesTotal - ret.InodesFree)
	ret.InodesUsedPercent = (float64(ret.InodesUsed) / float64(ret.InodesTotal)) * 100.0
	ret.Used = (uint64(stat.Blocks) - uint64(stat.Bfree)) * uint64(bsize)
	ret.UsedPercent = (float64(ret.Used) / float64(ret.Total)) * 100.0
	return ret, nil
}
