# RabbitMQ-Alex

``` lua
├─simple 简单队列
│
├─work 工作队列
│   ├─fair 公平模式（能者多劳）
│   │  
│   └─rr 轮询模式 
│  
├─exchange_fanout 发布/订阅队列（通过交换机，把消息广播到各个绑定的队列）
│  
├─exchange_direct 路由队列 （通过交换机，结合发送消息时的路由key，把消息广播到路由key对应的队列）
│  
├─exchange_topic 主题队列 （引入通配符模糊匹配，解决路由队列的路由key过多的问题）
│
└─rpc模式队列
```

