package main

import (
	"time"
	"fmt"
)

//+--------+
//| timers |
//+----+---++----+----+----+----+----+-----------------------+----+
//|    |    |    |    |    |    |    |                       |    |
//|  0 |  1 |  2 |  3 |  4 |  5 |  6 |            ...        | 63 |
//+----+----+----+----+----+----+----+-----------------------+----+
//|                                                         |
//|                                                         |
//|                                                         |
//|                                                         |
//|                                                         |
//| |                                          |            |    |                                          |
//| |          +--------------------+          |            |    |          +--------------------+          |
//| |--------> | cacheline size * N | <--------+            |    |--------> | cacheline size * N | <--------+
//| |          +--------------------+          |            |    |          +--------------------+          |
//| +-------------+-----------------+----------+            |    +-------------+-----------------+----------+
//+>|timersBucket |                 |          |            +--->|timersBucket |                 |          |
//+-------------+-----------------+   pad    |                 +-------------+-----------------+   pad    |
//|          lock mutex           |          |                 |          lock mutex           |          |
//+-------------------------------+          |                 +-------------------------------+          |
//|             gp *g             |          |                 |             gp *g             |          |
//+-------------------------------+          |                 +-------------------------------+          |
//|         created bool          |          |                 |         created bool          |          |
//+-------------------------------+          |    ........     +-------------------------------+          |
//|         sleeping bool         |          |                 |         sleeping bool         |          |
//+-------------------------------+          |                 +-------------------------------+          |
//|       rescheduling bool       |          |                 |       rescheduling bool       |          |
//+-------------------------------+          |                 +-------------------------------+          |
//|       sleepUntil int64        |          |                 |       sleepUntil int64        |          |
//+-------------------------------+          |                 +-------------------------------+          |
//|         waitnote note         |          |                 |         waitnote note         |          |
//+-------------------------------+          |                 +-------------------------------+          |
//|          t []*timer           |          |                 |          t []*timer           |          |
//+-------------------------------+----------+                 +-------------------------------+----------+
//|
//|
//|
//v
//+---+
//| 0 |
//+---+
//|
//|
//|
//|
//v
//+---+---+---+---+
//| 1 | 2 | 3 | 4 |
//+-+-+-+-+-+-+-+-+
//+---------------------------------+   |   |   +------------------------------------------+
//|                              +------+   +--------+                                     |
//v                              |                   |                                     v
//+---+---+---+---+                      v                   v                             +---+---+---+---+
//|   |   |   |   |              +---+---+---+---+   +---+---+---+---+                     |   |   |   |   |
//+-+-+-+-+---+---+              |   |   |   |   |   |   |   |   |   |                     +---+---+-+-+-+-+
//|   |                        +---+---+---+---+   +---+---+---+---+                               |   |
//+---------+   +---------+                                                                        +---------+   +-----+
//|                       |                                                                        |                   |
//|                       |                                                                        |                   |
//v                       v                                                                        v                   v
//+---+---+---+---+       +---+---+---+---+                                                        +---+---+---+---+   +---+---+---+---+
//|   |   |   |   |       |   |   |   |   |                  .................                     |   |   |   |   |   |   |   |   |   |
//+---+---+---+---+       +---+---+---+---+                                                        +---+---+---+---+   +---+---+---+---+
func main() {
	tick()
	fmt.Println("===================================================")
	ticker()
}

func ticker() {
	tt := time.Now().Add(time.Minute * 1)
	ticker := time.NewTicker(time.Second * 10)
	for {
		select {
		case t := <-ticker.C:
			{
				fmt.Println(t, "hello world")
				if t.After(tt) {
					fmt.Println("##################################################")
					ticker.Stop()
				}
			}
		}
		break
	}
}
func tick() {
	tt := time.Now().Add(time.Minute * 1)
	for t := range time.Tick(time.Second * 10) {
		fmt.Println(t, "hello world")
		after := t.After(tt)
		if after {
			break
		}
	}
}

//下面的代码，<-ch 这个 case 每次执行的时间都很短，但每次进入 select，time.After 都会分配一个新的 timer。
//因此会在短时间内创建大量的无用 timer，虽然没用的 timer 在触发后会消失，但这种写法会造成无意义的 cpu 资源浪费。正确的写法应该对 timer 进行重用，如下:
func timeout() {
	var ch chan int
	select {
	case <-time.After(time.Second):
		println("time out, and end")
	case <-ch:
	}
}
func reuseTimeout() {
	var ch = make(chan int)
	go func() {
		for {
			ch <- 1
		}
	}()

	timer := time.NewTimer(time.Second)
	for {
		timer.Reset(time.Second)
		select {
		case <-timer.C:
			println("time out, and end")
		case <-ch:
		}
	}
}
