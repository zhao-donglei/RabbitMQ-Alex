package com.alex.work.fair.recv;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * 工作队列队列-消息消费者
 * 公平
 */
public class Recv01 {

    //定义队列
    private final static String QUEUE_NAME = "work_fair";

    public static void main(String[] argv) throws Exception {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.116.128.174");
        factory.setUsername("zdl");
        factory.setVirtualHost("/demo");
        factory.setPassword("2021");
        factory.setPort(5672);
        //连接工厂创建连接
        Connection connection = factory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();
        //声明队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        int prefetchCount = 1;
        //公平模式---限制消费者每次只能接收一条消息，处理完才能接收下一条消息
        channel.basicQos(prefetchCount);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            /**
             * 手动确认
             *第二个参数：是否确认多条
             */
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };
        //监听队列消费消息
        channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
    }
}