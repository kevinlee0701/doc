### 安装maven

#### 1.下载maven包

```shell
wget http://mirrors.tuna.tsinghua.edu.cn/apache/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz
```

如果提示 `wget: 未找到命令`，请尝试如下指令安装 `wget`

```shell
yum -y install wget
```

#### 2.解压下载的maven压缩包

```shell
tar -xzvf apache-maven-3.3.9-bin.tar.gz
```

- -x：从备份文件中还原文件
- -z：处理备份文件
- -v：显示指令执行过程
- -f：指定备份文件

#### 3、配置系统maven环境

编辑系统环境文件`profile`

```shell
vi /etc/profile
```

注意，配置的变量，请指向自己解压的maven路径：

```xml
export MAVEN_HOME=/home/maven/apache-maven-3.3.9
export PATH=$MAVEN_HOME/bin:$PATH
```

重新加载一下配置：

```shel
source /etc/profile
```

查看maven版本，测试配置生效：

```shell
mvn -v
```

#### 4、配置镜像加速+指定仓库地址

打开maven安装包 setting文件

配置jar包下载路径，路径指向自己的。

```xml
<localRepository>/home/maven/repo</localRepository>
```

配置阿里镜像加速，默认是从中央仓库拉取。

```xml
<mirrors>
	<mirror>
	  <id>alimaven</id>
	  <name>aliyun maven</name>
	  <url>http://maven.aliyun.com/nexus/content/groups/public/</url>
	  <mirrorOf>central</mirrorOf>        
	</mirror>
</mirrors>
```

### rocketmq安装

#### 2.1 下载RocketMQ

```http
https://rocketmq.apache.org/release_notes/release-notes-4.9.2/
```

![image-20211229103320599](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211229103320599.png)



#### 2.2 解压文件

```shell
unzip rocketmq-all-4.9.2-bin-release.zip
```

#### 2.4 进入启动目录

```shell
cd /home/kevinlee/software/rocketmq-4.9.2
```

#### 2.5 启动Nameserver，

```shell
#进入编译后的目录
cd /home/kevinlee/software/rocketmq-4.9.2
#执行命令
nohup sh bin/mqnamesrv &
tail -f ~/logs/rocketmqlogs/namesrv.log
#The Name Server boot success...

```

#### 2.6 启动Broker

```shell
nohup sh bin/mqbroker -n localhost:9876 autoCreateTopicEnable=true &
tail -f ~/logs/rocketmqlogs/broker.log 
#The broker[%s, 172.30.30.233:10911] boot success...

```

#### 关闭服务

```shell
rocketmq服务关闭
关闭namesrv服务：sh bin/mqshutdown namesrv
关闭broker服务 ：sh bin/mqshutdown broker
```







#### 3.安装问题

>  在本人安装过程中只遇到了一个问题，就是内存分配不够，修改runserver.sh和runbroker.sh两个文件的如下位置：

![image-20211228144325039](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211228144325039.png)

> 报错：No compiler is provided in this environment. Perhaps you are running

```shell
#运行命令
yum install java-devel
```



### rocketmq可视化客户端

运行如下命令启动客户端

```sh
cd /home/kevinlee/software
java -jar rocketmq-console-ng-1.0.0.jar
```

