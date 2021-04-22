package main

import (
	"bufio"
	"debug/elf"
	"fmt"
	"log"
	"sync"
	"time"
)

//type data struct {
//	lock *sync.Mutex
//}
//var d data = data{lock:&sync.Mutex{}}
//这样可以解决,或者接受者变成这样 (d *data)

//var local sync.Mutex
type data struct {
	sync.Mutex
}

func (d data) test(s ...string) {
	d.Lock()
	defer func() {
		d.Unlock()
		println("success")
	}()
	for i := 0; i < 5; i++ {
		fmt.Printf("%s, %d, object addr: %p \n", s, i, &d)
		time.Sleep(time.Second)
	}
}

/*
值传递每次调用的地址都会不一样
*/
func main() {
	//var wg sync.WaitGroup
	//wg.Add(2)
	var d data
	//go func() {
	//	defer wg.Done()
	d.test("read")
	//}()
	//go func() {
	//	defer wg.Done()
	d.test("write")
	//}()
	//wg.Wait()
	buf := make([]byte, 2)
	advance, token, err := bufio.ScanWords(buf, false)
	fmt.Println(advance, token, err)
	//========================
	f, err := elf.Open("mainexpvarHandler")

	if err != nil {
		log.Fatal(err)
	}

	for _, section := range f.Sections {
		log.Println(section)
	}
}
