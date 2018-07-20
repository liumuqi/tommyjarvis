package main

import (
	"fmt"
	"io"
	"log"
	"net/http"
)

type MyServer struct {
	i int
}

//接收者的类型才是真正实现接口的类型
func (myServer *MyServer) ServeHTTP(w http.ResponseWriter, request *http.Request) {
	io.WriteString(w, "Hello")
	fmt.Println(request.UserAgent())
}

func main() {
	myServer := MyServer{1}
	err := http.ListenAndServe(":8080", &myServer) //接收者的类型才是真正实现接口的类型
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}
