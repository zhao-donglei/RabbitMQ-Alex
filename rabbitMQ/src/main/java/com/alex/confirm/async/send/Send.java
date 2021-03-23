package com.alex.confirm.async.send;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

/**
 * 确认模式-异步-消息生产者
 */
public class Send {

    //队列名称
    private final static String QUEUE_NAME = "confirm_async";

    public static void main(String[] argv) throws Exception {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.116.128.174");
        factory.setUsername("zdl");
        factory.setVirtualHost("/demo");
        factory.setPassword("2021");
        factory.setPort(5672);
        Connection connection=null;
        Channel channel=null;
        try {
                //维护信息发送回执 deliveryTag
                final SortedSet<Long> confirmSet= Collections.synchronizedSortedSet(new TreeSet<Long>());
                //创建连接
                connection = factory.newConnection();
                //创建信道
                channel = connection.createChannel();
                //开启确认模式
                channel.confirmSelect();
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

                    //添加channel监听
                    channel.addConfirmListener(new ConfirmListener() {
                        //已确认
                        @Override
                        public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                            //multiple=true 确认多条，false 确认单条
                            if(multiple){
                                System.out.println("handleAck--success-->multiple"+deliveryTag);
                                //清除前deliveryTag项标识id
                                confirmSet.headSet(deliveryTag+1L).clear();
                            }else{
                                System.out.println("handleAck--success-->single"+deliveryTag);
                                confirmSet.remove(deliveryTag);
                            }
                        }
                        //未确认
                        @Override
                        public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                            if(multiple){
                                System.out.println("handleAck--failed-->multiple"+deliveryTag);
                                //清除前deliveryTag项标识id
                                confirmSet.headSet(deliveryTag+1L).clear();
                            }else{
                                System.out.println("handleAck--failed-->single"+deliveryTag);
                                confirmSet.remove(deliveryTag);
                            }
                        }
                    });

                    while (true){
                        String message = "Hello World!";
                        //获取unconfirm的消息序号的deliveryTag
                        Long seqNo=channel.getNextPublishSeqNo();
                        channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
                        confirmSet.add(seqNo);
                    }

                }catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        //关闭通道
                        if(null!=channel && channel.isOpen()){
                            channel.close();
                        }
                        //关闭连接
                        if(null !=connection && connection.isOpen()){
                            connection.close();
                        }
                    }catch (TimeoutException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

        }
    }
}