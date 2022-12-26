### **安装**

#### 下载

在官网下载tar.gz的安装包，或者通过wget的方式下载

```shell
wget http://download.redis.io/releases/redis-4.0.1.tar.gz
```

#### 解压　

```shell
#安装目录
cd /home/kevinlee/software
tar -zxvf redis-4.0.1.tar.gz
```

#### 编译

通过make来编译，make是自动编译，会根据Makefile中描述的内容来进行编译。

```shell
cd redis-4.0
make
```

如果没有make命令，安装make命令

```shell
CentOS 中无法使用make,make install 命令
 
提示错误：make: command not found
 
make是gcc的编译器，一定要安装
 
     1、安装：
 
yum -y install gcc automake autoconf libtool make
 
2、安装g++:
 
yum install gcc gcc-c++
　　
```



可以看到在src目录下生成了几个新的文件。

#### 安装

```text
make install
```

实际上，就是将这个几个文件加到/usr/local/bin目录下去。这个目录在Path下面的话，就可以直接执行这几个命令了。

可以看到，这几个文件就已经被加载到bin目录下了



#### 修改配置文件

打开配置文件

```shell
$ cd redis-4.0.1
$ vim redis.conf
```

属性:bind

```shell
# bind 127.0.0.1 #限制只有本机可以连接redis服务连接,需要注释掉
```

属性:protected-mode

```shell
protected-mode yes #保护模式，需配置bind ip或者设置访问密码
protected-mode no  #外部网络可以直接访问
daemonize yes #开启守护进程
```



#### 启动服务器

运行命令

```shell
#启动命令
cd /home/kevinlee/software/redis/redis-4.0.1
redis-server redis.conf
#停止命令
cd /home/kevinlee/software/redis/redis-4.0.1/src
redis-cli shutdown
```

**卸载redis**

卸载redis服务，只需把/usr/local/bin/目录下的redis删除即可

```shell
rm -rf /usr/local/bin/redis*
```

甚至可以把解压包也删除掉

```shell
rm -rf /root/redis-stable
```

#### 基础命令

```shell
#连接
redis-cli -h 172.16.95.138 -p 6379

ps aux | grep redis #查看redis是否启动成功

netstat -tlun  #查看主机的6379端口是否在使用（监听）

./redis-cli #打开redis的客户端    

quit #退出redis的客户端

pkill redis-server #关闭redis服务器

./redis-cli shutdown   #也可以通过这条命令关闭redis服务器
```

### redis 报错 Redis protected-mode 配置文件没有真正启动

1.完成修改配置步骤

2.没反应应该是你启动服务端的时候没有带上配置文件。你可以./redis-server redis.conf
你配置好了，但要重新启动redis,如果还是报一样的错误，很可能是没有启动到配置文件，所以需要真正的和配置文件启动需要：
在redis.conf文件的当前目录下：

```shell
redis-server redis.conf
```

### 设置密码

修改配置文件redis.conf

```xml
requirepass redis
```

