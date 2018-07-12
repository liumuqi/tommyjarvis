package main

import "fmt"

func main() {
	var a = map[int]int{}
	a[0] = 1
	fmt.Printf("%#v", a)
	defer func() {
		if err := recover(); err != nil {
			println("oh no")
		}
	}()
	go func() {
		defer func() {
			if err := recover(); err != nil {
				println("oh no")
			}
		}()
		for {
			a[0] = 1
		}
	}()
	for {
		a[1] = 1
	}
}
