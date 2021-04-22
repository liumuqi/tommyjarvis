package main

import (
	"context"
	"fmt"
	"math/rand"
	"time"
)

func main() {
	//	ch := make(chan int)
	//	go routineA(ch)
	//	go routineB(ch)
	//	println("goroutines scheduled!")
	//	<-ch
	//	<-ch
	//	//contextTest()
	//	//ticker := time.NewTicker(time.Second * 2)
	//	////循环执行的step是批次中的一台,每次循环开头检查 s.ProcessingTasksByIdMap 中是否有task,没有则说明暂停了需要关闭此goroutine, 对账成功的goroutine返回结果后继续后续操作
	//	//var i int32 = 0
	//	//onlineCityNumMap := make(map[string]int32)
	//	//for range ticker.C {
	//	//	i++
	//	//	if i < 3 {
	//	//		fmt.Println("=================" + fmt.Sprint(i))
	//	//	} else if i == 4 {
	//	//		fmt.Println("---break---" + fmt.Sprint(i))
	//	//		continue
	//	//	} else {
	//	//		onlineCityNumMap["a"]++
	//	//		fmt.Printf("%v............%v\n", onlineCityNumMap, fmt.Sprint(i))
	//	//	}
	//	//}
	//	testSyncMap()
	//}
	//
	//type Test struct {
	//	ProcessingTaskIds sync.Map
	//}
	//
	//func testSyncMap() {
	//	test := &Test{}
	//	test.ProcessingTaskIds.Store("a", true)
	//	test.ProcessingTaskIds.Store("b", true)
	//	test.ProcessingTaskIds.Store("c", true)
	//	test.ProcessingTaskIds.Store("e", true)
	//	test.ProcessingTaskIds.Range(func(key, value interface{}) bool {
	//		if key == "b" {
	//			test.ProcessingTaskIds.Delete("b")
	//			fmt.Printf("delete k:%v,v:%v \n", key, value)
	//		} else {
	//			fmt.Printf("k:%v,v:%v  \n", key, value)
	//		}
	//		return true
	//	})
	//	test.ProcessingTaskIds.Range(func(key, value interface{}) bool {
	//		fmt.Printf("k:%v,v:%v \n", key, value)
	//		return true
	//	})
	//	test.ProcessingTaskIds.Store("e", false)
	//	test.ProcessingTaskIds.Range(func(key, value interface{}) bool {
	//		fmt.Printf("k:%v,v:%v \n", key, value)
	//		return true
	//	})
	//	fmt.Println("================")
	//	if v, ok := test.ProcessingTaskIds.Load("a"); ok {
	//		fmt.Printf("k:%v,v:%v \n", v, ok)
	//	} else {
	//		fmt.Printf("no key =========== %v,%v \n", v, ok)
	//	}
	//	data := make(map[string]string)
	//	data["a"] = "1"
	//	data["b"] = "2"
	//	data["c"] = "3"
	//ticker := time.NewTicker(time.Second * 1)
	//var c int32 = 0
	//for range ticker.C {
	//	c++
	//	if c == 4 {
	//		time.Sleep(time.Second * 5)
	//	}
	//	if c == 5 {
	//		time.Sleep(time.Second * 2)
	//	}
	//	if  c==10 {
	//		break
	//	}
	//	fmt.Println(fmt.Sprint(time.Now().Unix()) + " ticker.C================" + fmt.Sprint(c))
	//}
	var dd string
	fmt.Println("...."+dd)
	c := make(chan int, 20)
	fmt.Println(len(c))
	c <- 1
	c <- 2
	c <- 3
	c <- 4
	c <- 5
	if test(c) {
		return
	}
	//fmt.Println("vvv")
	//sprintf := fmt.Sprintf("%v_%v_%v", 1, "", 3)
	//split := strings.Split("_2", "_")
	//for _, v := range split {
	//	fmt.Println("v:" + v + " len:" + fmt.Sprint(len(v)))
	//}
	//var x string
	//fmt.Println(len(x))
}

func test(c chan int) bool {
	var stop chan bool = make(chan bool, 0)
	//go func(c chan int, stop chan bool) {
	xxx := 1
	for {
		select {
		case <-time.After(time.Second * 3):
			xxx++
			if xxx > 3 {
				fmt.Println("xxx > 3")
				break
				//return true
			}
			i := rand.Int()
			c <- i
			if i%10 == 2 {
				stop = make(chan bool, 1)
				stop <- true
				fmt.Println("stop <- true")
			} else if i%7 == 5 {
				fmt.Println("stop close!!! i%7 == 5 !!!!!!!!!!!")
			}
		case _, ok := <-stop:
			//case _, ok := <-stop:
			fmt.Printf("************stop close!!!!!!!!!!!!!! ok: %v :%v  \n", ok, stop == nil)
			if !ok {
				stop = nil
				return true
			}
			break
		}
		if stop == nil {
			fmt.Printf("break...........................!!!!!!!!:%v  \n", stop == nil)
			break
		}
		fmt.Printf("........................ %v \n", stop == nil)
	}
	fmt.Printf("dddddddddddddddddddddddddddddd %v \n", stop == nil)
	//}(c, stop)
	ticker := time.NewTicker(time.Second * 1)
	x := 1
	canclosed := true
	for {
		select {
		case <-ticker.C:
			x++
			if x > 5 && canclosed {
				close(stop)
				fmt.Printf("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ %v\n", stop == nil)
				canclosed = false
				break
			}
			if x > 20 {
				return true
			}
		case d := <-c:
			fmt.Println("custom c" + fmt.Sprint(d))
			//case ss := <-stop:
			//	fmt.Println("custom stop " + fmt.Sprint(ss))
			//close(stop)
		}
	}
}

func contextTest() {
	gen := func(ctx context.Context) <-chan int {
		dst := make(chan int)
		n := 1
		go func() {
			for {
				select {
				case <-ctx.Done():
					time.Sleep(time.Second * 5)
					fmt.Println("vvvvvvvvvvvvvvvv")
					return
				case dst <- n:
					n++
				}
			}
		}()
		return dst
	}
	ctx, cancel := context.WithCancel(context.Background())
	for n := range gen(ctx) {
		fmt.Println(n)
		if n == 5 {
			break
		}
	}
	cancel()
	time.Sleep(time.Second * 7)
}
func routineA(ch chan int) {
	println("A executing!")
	ch <- 1
}
func routineB(ch chan int) {
	println("B executing!")
	ch <- 2
}
