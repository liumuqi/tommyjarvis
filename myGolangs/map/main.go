package main

import (
	"fmt"
	"reflect"
)

type Bar struct {
	Id string
}

type Foo struct {
	Bar *Bar
}

func (f *Foo) Equal(f2 *Foo) bool {
	return f.Bar.Id == f2.Bar.Id
}

func (f *Foo) Hash() uint32 {
	var h uint32
	for i := 0; i < len(f.Bar.Id); i++ {
		h ^= uint32(f.Bar.Id[i])
		h *= 16777619
	}
	return h
}

func main() {
	m := make(map[Foo]string)
	bar := &Bar{"one"}
	foo := Foo{bar}
	m[foo] = "foo"
	fmt.Printf("foo: %s bar one:%s\n", foo, m[foo])
	bar.Id = "two"
	fmt.Printf("bar id change two:%s\n", m)

	bar2 := &Bar{"two"}
	foo2 := Foo{bar2}

	fmt.Printf("foo2:%s  foo:%s  foo2==foo:%s\n", foo2, foo, foo2 == foo)
	fmt.Printf("reflect.DeepEqual(a, b): %s \n", reflect.DeepEqual(foo2, foo))

	fmt.Printf("foo2:%s  :%s\n", foo2, m[foo2])
	// At this point, your map may be irreversibly broken.
	// It contains an element that is probably in the wrong bucket.
	fmt.Println("================================================")
	query := map[string]string{}
	// 需要按照字典排序
	query["test0"] = "0"
	query["test1"] = "1"
	query["test2"] = "2"

	for i := 0; i < 100; i++ {
		for _, v := range query {
			fmt.Print(v)
		}
		fmt.Println()
	}
}
