package main

import "fmt"

func main() {
	s := " a b c  d e f "
	bytes := trim([]byte(s))
	fmt.Printf(">>%s<<", string(bytes))
	//s := []byte("")
	//s1 := append(s, 'a')
	//s2 := append(s, 'b')
	////fmt.Println(s1, "==========", s2)
	//fmt.Println(string(s1), "==========", string(s2))
}
func trim(s []byte) []byte {
	const space = ' '
	n := len(s)
	low, high := 0, n
	for low < n && s[low] == space {
		low++
	}
	for high > low && s[high-1] == space {
		high--
	}
	return s[low:high:high]
}
