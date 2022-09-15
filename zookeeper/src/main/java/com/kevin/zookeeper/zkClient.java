package com.kevin.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * 类 描 述：zk客户端
 * 创建时间：2022/9/15 14:57
 * 创 建 人：lifeng
 */
public class zkClient {

    private String connectString = ZookeeperUtil.hostname;
    private int sessionTimeout = 2000;
    private ZooKeeper zkClient;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        com.kevin.zookeeper.zkClient zkClient1 = new zkClient();
        zkClient1.init();
        zkClient1.create();
    }

    public void init() throws IOException {

        zkClient = new ZooKeeper(connectString, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

//                System.out.println("-------------------------------");
//                List<String> children = null;
//                try {
//                    children = zkClient.getChildren("/", true);
//
//                    for (String child : children) {
//                        System.out.println(child);
//                    }
//
//                    System.out.println("-------------------------------");
//                } catch (KeeperException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });
    }


    public void create() throws KeeperException, InterruptedException {
        String nodeCreated = zkClient.create("/atguigu/test", "ss.avi".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }


    public void getChildren() throws KeeperException, InterruptedException {
        List<String> children = zkClient.getChildren("/", true);
        for (String child : children) {
            System.out.println(child);
        }
        // 延时
        Thread.sleep(Long.MAX_VALUE);
    }


    public void exist() throws KeeperException, InterruptedException {

        Stat stat = zkClient.exists("/atguigu", false);

        System.out.println(stat==null? "not exist " : "exist");
    }
}
