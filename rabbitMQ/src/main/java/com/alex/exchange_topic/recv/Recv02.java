package com.alex.exchange_topic.recv;

import com.rabbitmq.client.*;

/**
 * 主题队列-消息消费者
 */
public class Recv02 {

    //定义交换机
    private final static String EXCHANGE_NAME = "exchange_topic";

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
        //绑定交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        //获取队列（排他队列）
        String queueName = channel.queueDeclare().getQueue();
        //将队列和交换机进行绑定
        String infoRoutingKey="info.*.*";
        String errorRoutingKey="*.rabbit.*"; //*号匹配1个，#匹配0个或多个
        String warningRoutingKey="FBI.#";

        channel.queueBind(queueName, EXCHANGE_NAME, infoRoutingKey);
        channel.queueBind(queueName, EXCHANGE_NAME, errorRoutingKey);
        channel.queueBind(queueName, EXCHANGE_NAME, warningRoutingKey);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        /**
         * 监听队列消费消息
         * 第二个参数：收到消息后是否自动回值
         */
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}