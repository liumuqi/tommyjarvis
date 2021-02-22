package main

import (
	"fmt"
	_ "runtime"
	"strings"
	"testing"

	_ "net"
	_ "unsafe"
)

//go:linkname lookupStaticHost net.lookupStaticHost
func lookupStaticHost(host string) []string

func f() {
	lookupStaticHost("localhost")
}
func ft() {
	var bs = []byte{1: 2, 2: 3, 5: 100, 10: 'a'}
	for i, v := range bs {
		fmt.Println("{}=>{}", i, v)
	}
}

//使用slice方式拼接 减少内存分配
func sliceTest() {
	var bx = []byte{1: 'a', 1024: 'x'}
	var by = []byte{1024: 'y'}
	//var s = (string(bx) + string(by))[1:]  这样就是4次
	var s = ("-" + string(bx) + string(by))[1:] //这样就是2次了
	fmt.Println("sss:{}", s)
}
func sliceTest2() {
	var bx = []byte{1024: 'x'}
	var by = []byte{1024: 'y'}
	var s = string(bx) + string(by)
	fmt.Println("sss:{}", s)
}
func runeTest() {
	var gogogo = strings.Repeat("go我", 100)
	fmt.Println(len([]rune(gogogo)))
}
func runeTest2() {
	var gogogo = strings.Repeat("go我", 100)
	fmt.Println(len([]byte(gogogo)))
}

func rb() bool {
	return false
}

//使用go:linkname调用 其他包里面的私有方法
func main() {
	fmt.Println(lookupStaticHost("localhost"))
	ft()
	fmt.Println(testing.AllocsPerRun(1, f))
	fmt.Println("------------------")
	//sliceTest()
	fmt.Println(testing.AllocsPerRun(1, sliceTest))
	fmt.Println(testing.AllocsPerRun(1, sliceTest2))
	fmt.Println("------------------")
	fmt.Println(testing.AllocsPerRun(1, runeTest))
	fmt.Println(testing.AllocsPerRun(1, runeTest2))

	//如果{ 换行 。打印的是==== 真有意思
	switch rb() {
	case true:
		fmt.Println("========")
	case false:
		fmt.Println("------")
	}

}
