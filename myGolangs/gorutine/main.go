package main

func main() {
	ch := make(chan int)
	go routineA(ch)
	go routineB(ch)
	println("goroutines scheduled!")
	<-ch
	<-ch
}
func routineA(ch chan int) {
	println("A executing!")
	ch <- 1
}
func routineB(ch chan int) {
	println("B executing!")
	ch <- 2
}
