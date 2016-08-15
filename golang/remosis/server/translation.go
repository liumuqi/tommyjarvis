package server

import (
	"bytes"
	"io/ioutil"
	"log"
	"net/http"
	"time"
)

func GetInfo(urlstring string) ([]byte, error) {
	defer func() {
		if err := recover(); err != nil {
			log.Println("[server/translation.go:GetInfo] error", err)
		}
	}()
	log.Println("get config url--->:", string(urlstring))
	req, err := http.NewRequest("GET", urlstring, nil)
	client := &http.Client{}
	client.Timeout = time.Second * 5
	if err != nil {
		log.Println("new request err(%s)", err.Error())
		return nil, err
	}
	resp, err := client.Do(req)
	defer resp.Body.Close()
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Println("read body err(%s)", err.Error())
		return nil, err
	}
	log.Println("服务端返回的信息:", string(body))
	return body, nil
}
func SendInfo(urlstring string, httptype string, connTimeOut int, data string) (int, error) {
	req, err := http.NewRequest("POST", urlstring, bytes.NewBuffer([]byte(data)))
	req.Header.Set("Content-Type", httptype)
	if err != nil {
		log.Println("init request error----->:", urlstring, " error---->:", err)
		return 400, err
	}
	client := &http.Client{}
	client.Timeout = time.Second * time.Duration(connTimeOut)
	resp, err := client.Do(req)
	if err != nil {
		log.Println("request send error----->:", urlstring, " error---->:", err)
		return 500, err
	}
	defer resp.Body.Close()
	return resp.StatusCode, nil
}
