package config

import "encoding/json"

type Config struct {
	Server    *Server    `json:"server"`
	Collector *Collector `json:"collector"`
	Redises   []Redis    `json:"redises"`
	//Valid     bool       `json:"valid"`
}
type Server struct {
	//server 的Ip
	Ip string `json:"ip"`
	//server 主机名
	Name string `json:"name"`
	//监控类型 目前为redis
	Type string `json:"ctype"`
	//socket监控端口
	Socket int `json:"socket"`
}
type Redis struct {
	//redis 本机ip
	Ip string `json:"ip"`
	//redis 端口
	Port int `json:"port"`
	//redis slots 节点
	//Slots string `json:"slots"`
	//集群分类
	Cluster int `json:"cluster"`
}

//webcontrol 信息
type Collector struct {
	//webcontrol收集url
	CollectUrl string `json:"collectUrl"`
	//alarmUrl 报警url
	AlarmUrl string `json:"alarmUrl"`
	//url 连接超时  second
	ConnTimeout int `json:"connTimeout"`
	//接受类型
	//such as application/json
	AcceptType string `json:"acceptType"`
	//认证信息
	Authen string `json:"authen"`
}

func ParseConf(content []byte) (*Config, error) {
	config := &Config{}
	config.Server = &Server{}
	config.Collector = &Collector{}
	config.Redises = []Redis{}
	err := json.Unmarshal(content, config)
	if err != nil {
		return nil, err
	}
	return config, nil
}
