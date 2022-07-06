### 拉取zookeeper镜像

```shell
#拉取镜像
docker pull zookeeper:3.6
#运行镜像
docker run -d --name zookeeper -p 2181:2181 -v /etc/localtime:/etc/localtime zookeeper:3.6
```

### 拉取kafka镜像

```shell
#拉取镜像
docker pull wurstmeister/kafka:2.12-2.5.0
#运行镜像
docker run -d --name kafka --publish 9092:9092 --link zookeeper --env KAFKA_ZOOKEEPER_CONNECT=zookeeper:2181 --env KAFKA_ADVERTISED_HOST_NAME=localhost --env KAFKA_ADVERTISED_PORT=9092 wurstmeister/kafka:2.12-2.5.0
```

### 创建一个topic

```shell
#进入容器
docker exec -it ${CONTAINER ID} /bin/bash
cd opt/bin
#单机方式：创建一个主题
bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic first
#运行一个生产者
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic first
#运行一个消费者
bin/kafka-console-consumer.sh   --bootstrap-server hadoop102:9092 --topic first
```

### kafka设置分区数量

```shell
#分区数量的作用：有多少分区就能负载多少个消费者，生产者会自动分配给分区数据，每个消费者只消费自己分区的数据，每个分区有自己独立的offset
#进入kafka容器
vi opt/kafka/config/server.properties
修改run.partitions=2
#退出容器
ctrl+p+q
#重启容器
docker restart kafka
#修改指定topic
./kafka-topics.sh --zookeeper localhost:2181 --alter --partitions 3 --topic topicname
```

### kafka设置brokerid问题

一开始我以为config/server.properties 中 broker.id=-1 有问题；但是实际上，这是合法的。因为真正的 broker.id 会保存在 log.dir 目录下的 meta.properties 中。这个文件在kafka启动时创建。

首先，我们进入 zookeeper 容器，并打开 zookeeper 客户端：

```shell
D:\>docker exec -it kafka_zookeeper_1 bash
root@f4d9db43d301:/opt/zookeeper-3.4.13# bin/zkCli.sh
```

接着查看结点信息：

```bash
[zk: localhost:2181(CONNECTED) 0] ls /brokers/ids
[1001]
```

***我们看到存在一个id为1001的节点。\***

但是，我们进入 kafka 容器，并查看 server.properties 中的 `broker.id` 属性：

```shell
F:\Docker\kafka>docker exec -it kafka_kafka_1 bash
bash-5.1# cd $KAFKA_HOME
bash-5.1# cat config/server.properties | grep broker.id
broker.id=-1
```

***那真的表示不对应吗？答案是否定的。\***

还是在 kafka 容器中，查看 server.properties 中的 `log.dirs` 属性：

```lua
bash-5.1# cat config/server.properties | grep log.dirs
log.dirs=/kafka/kafka-logs-8ea8aa68e5a5
```

顺藤摸瓜，查看日志文件夹下的 `meta.properties` 中的 `broker.id` 属性：

```shell
bash-5.1# cat /kafka/kafka-logs-8ea8aa68e5a5/meta.properties
#
#Thu Aug 12 08:43:58 GMT 2021
cluster.id=vAGFzDVHRLu_06_1DM03Og
version=0
broker.id=1001
```

**所以，kafka节点的id 以 log文件夹下的 `meta.properties` 中的 `broker.id`为准。**
