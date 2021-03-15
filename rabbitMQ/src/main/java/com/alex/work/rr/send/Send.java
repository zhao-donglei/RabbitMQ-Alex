package com.alex.work.rr.send;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.nio.charset.StandardCharsets;

/**
 * 工作队列-消息生产者
 * 轮询
 */
public class Send {

    //队列名称
    private final static String QUEUE_NAME = "work_rr";

    public static void main(String[] argv) throws Exception {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.116.128.174");
        factory.setUsername("zdl");
        factory.setVirtualHost("/demo");
        factory.setPassword("2021");
        factory.setPort(5672);
        try (
                //Connection继承Closeable继承AutoCloseable,所以try不需要catch
                //创建工厂创建连接，
                Connection connection = factory.newConnection();
                //创建信道
                Channel channel = connection.createChannel()) {
                    /**
                     * 声明队列
                     * 第一个参数queue：队列名称
                     * 第二个参数durable：是否持久化
                     * 第三个参数Exclusive：排他队列。如果一个队列被声明为排他队列，该队列仅对首次声明为它的连接可见，并在连接关闭时自动删除
                         * 注意三点Tips：
                         * 1、排他队列是基于连接可见的，同一个连接的不同信道是可以同时访问同一个连接创建的排他队列的。
                         * 2、“首次”，如果一个连接已经声明了一个排他队列，其它连接是不允许建立同名的排他队列的，这个与普通队列不同
                         * 3、即使该队列是持久化的，一旦连接关闭或者客户端退出，该排他队列都会被自动删除。
                         *    这种队列只限于一个客户端发送读取消息的应用场景
                     * 第四个参数Auto-delete：自动删除，如果该队列没有任何订阅的消费者，该队列会被自动删除。
                     *      这种队列适用于临时队列
                     */
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    for(int i=0;i<20;i++) {
                        String message = "Hello World!"+i;
                        //发送消息
                        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                        System.out.println(" [x] Sent '" + message + "'"+i);
                    }
                }
    }
}