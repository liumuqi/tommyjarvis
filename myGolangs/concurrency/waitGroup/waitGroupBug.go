package main

import (
	"log"

	"sync"
)

func main() {
	runRight()
	runWrong()
}

/**
原因是 传参数的时候 值传递
在 Golang 中函数之间传递变量时总是以值的方式传递的，无论是 int,string,bool,array 这样的内置类型（或者说原始的类型），还是 slice,channel,map 这样的引用类型，在函数间传递变量时，都是以值的方式传递，也就是说传递的都是值的副本
*/
func runWrong() {
	wg := sync.WaitGroup{}
	for i := 0; i < 5; i++ {
		wg.Add(1)
		go func(wg sync.WaitGroup, i int) {
			log.Printf("@@@:%d", i)
			wg.Done()
		}(wg, i)
	}
	wg.Wait()
	log.Println("exit")
}
func runRight() {
	wg := &sync.WaitGroup{}
	for i := 0; i < 5; i++ {
		wg.Add(1)
		go func(wg *sync.WaitGroup, i int) {
			log.Printf("i:%d", i)
			wg.Done()
		}(wg, i)
	}
	wg.Wait()
	log.Println("exit")
}
