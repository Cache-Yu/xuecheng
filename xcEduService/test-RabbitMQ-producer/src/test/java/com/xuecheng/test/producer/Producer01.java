package com.xuecheng.test.producer;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Producer01 {

    public static final String QUEUE="TEST1";


    public static void main(String[] args){
        //创建连接工厂
        ConnectionFactory connectionFactory=new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setVirtualHost("/");


        Connection connection=null;
        try {

            connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            channel.queueDeclare(QUEUE,true,false,false,null);
            String message="四月的文山，有杨娇燕，很美！这一次邂逅，可谓华美";

            channel.basicPublish("",QUEUE,null,message.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
