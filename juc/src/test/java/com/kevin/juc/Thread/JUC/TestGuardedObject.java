package com.kevin.juc.Thread.JUC;

import com.kevin.juc.Thread.bean.GuardedObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;;

import java.util.Objects;


@Slf4j
public class TestGuardedObject {


    @Test
    public void test1(){
        String id="1";//唯一索引值
        Message msg1 = new Message(id,"{...}");
        //创建GuardedObject实例
        GuardedObject<Message> go= GuardedObject.create(id);

        //发送消息
        send(msg1);
        //等待MQ消息
        Message r = go.get(Objects::nonNull);
    }


    class Message{
        String id;
        String content;

        public Message(String id, String content) {
            this.id = id;
            this.content = content;
        }
    }

    /**
     * 该方法可以发送消息
     */
    void send(Message msg){
        //省略相关代码
    }

    /**
     *   MQ消息返回后会调用该方法,该方法的执行线程不同于发送消息的线程
     */
    void onMessage(Message msg){
        //唤醒等待的线程
        GuardedObject.fireEvent(
                msg.id, msg);
    }


}
