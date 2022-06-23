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

