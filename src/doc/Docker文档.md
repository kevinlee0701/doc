

### Docker

> 出自https://blog.csdn.net/qq_21197507/article/details/115071715

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200811092952284.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70)

#### 名词解释

##### 镜像（image）

Docker镜像就好比是一个模板，可以通过这个模板来创建容器服务，tomcat镜像 ===> run ===> tomcat01容器， 通过这个镜像可以创建多个容器（最终服务运行或者项目运行就是在容器中的）

##### 容器（container）

Docker利用容器技术，独立运行一个或者一组应用， 通过镜像来创建的
启动，停止，删除，基本命令！
就目前可以把这个容器理解为一个建议的linux系统

##### 仓库（repository）

存放镜像的地方
Docker Hub（默认是国外的）
阿里云,,,都有容器服务（配置镜像加速！）

#### 安装docker

##### 卸载旧版本

较旧的 Docker 版本称为 docker 或 docker-engine 。如果已安装这些程序，请卸载它们以及相关的依赖项

```shell
$ sudo yum remove docker 
                  docker-client 
                  docker-client-latest 
                  docker-common 
                  docker-latest 
                  docker-latest-logrotate 
                  docker-logrotate 
                  docker-engine
```



##### 安装 Docker Engine-Community

###### 使用 Docker 仓库进行安装

在新主机上首次安装 Docker Engine-Community 之前，需要设置 Docker 仓库。之后，您可以从仓库安装和更新 Docker。

- 设置仓库

  安装所需的软件包。yum-utils 提供了 yum-config-manager ，并且 device mapper 存储驱动程序需要 device-mapper-persistent-data 和 lvm2。

```shell
$ sudo yum install -y yum-utils device-mapper-persistent-data lvm2
```



- 使用官方源地址（比较慢）

```shell
$ sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
```

- 阿里云

```shell
$ sudo yum-config-manager --add-repo https://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo
```

- 清华大学源

```shell
$ sudo yum-config-manager 
    --add-repo 
    https://mirrors.tuna.tsinghua.edu.cn/docker-ce/linux/centos/docker-ce.repo
```

###### 开始安装

安装最新版本的 Docker Engine-Community 和 containerd，或者转到下一步安装特定版本：

```shell
sudo yum install docker-ce docker-ce-cli containerd.io
```

Docker 安装完默认未启动。并且已经创建好 docker 用户组，但该用户组下没有用户。

**要安装特定版本的 Docker Engine-Community，请在存储库中列出可用版本，然后选择并安装：**

- 列出并排序您存储库中可用的版本。此示例按版本号（从高到低）对结果进行排序。

```shell
yum list docker-ce --showduplicates | sort -r
 
docker-ce.x86_64  3:18.09.1-3.el7                     docker-ce-stable
docker-ce.x86_64  3:18.09.0-3.el7                     docker-ce-stable
docker-ce.x86_64  18.06.1.ce-3.el7                    docker-ce-stable
docker-ce.x86_64  18.06.0.ce-3.el7                    docker-ce-stable

```

- 通过其完整的软件包名称安装特定版本，该软件包名称是软件包名称（docker-ce）加上版本字符串（第二列），从第一个冒号（:）一直到第一个连字符，并用连字符（-）分隔。例如：docker-ce-18.09.1。

```
sudo yum install docker-ce-<VERSION_STRING> docker-ce-cli-<VERSION_STRING> containerd.io
```

- 启动 Docker

```shell
sudo systemctl start docker
```

- 通过运行 hello-world 映像来验证是否正确安装了 Docker Engine-Community 。

  ```shell
  d
  ```

#### Docker基本命令

##### 	docker的常用命令

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200812093539341.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2ZhbmppYW5oYWk=,size_16,color_FFFFFF,t_70)



![image-20211201161930009](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211201161930009.png)

​		                      ![image-20211201161953317](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211201161953317.png)

##### 帮助命令

```shell
docker version  # docker版本信息
docker info     # 系统级别的信息，包括镜像和容器的数量
docker 命令 --help
```

##### 镜像命令

- docker images 查看所有本地主机上的镜像

```r
docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
hello-world         latest              bf756fb1ae65        7 months ago        13.3kB
 
# 解释
REPOSITORY      # 镜像的仓库
TAG             # 镜像的标签
IMAGE ID        # 镜像的ID
CREATED         # 镜像的创建时间
SIZE            # 镜像的大小
 
# 可选项
--all , -a      # 列出所有镜像
--quiet , -q    # 只显示镜像的id

```

- docker search 查找镜像

![image-20211201152531144](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211201152531144.png)

- Docker pull 拉取镜像

  ```shell
  
  # 下载镜像，docker pull 镜像名[:tag]
  [root@iZ2zeg4ytp0whqtmxbsqiiZ ~]# docker pull mysql
  Using default tag: latest           # 如果不写tag，默认就是latest
  latest: Pulling from library/mysql
  bf5952930446: Pull complete         # 分层下载，dockerimages的核心，联合文件系统
  8254623a9871: Pull complete 
  938e3e06dac4: Pull complete 
  ea28ebf28884: Pull complete 
  f3cef38785c2: Pull complete 
  894f9792565a: Pull complete 
  1d8a57523420: Pull complete 
  6c676912929f: Pull complete 
  ff39fdb566b4: Pull complete 
  fff872988aba: Pull complete 
  4d34e365ae68: Pull complete 
  7886ee20621e: Pull complete 
  Digest: sha256:c358e72e100ab493a0304bda35e6f239db2ec8c9bb836d8a427ac34307d074ed     # 签名
  Status: Downloaded newer image for mysql:latest
  docker.io/library/mysql:latest      # 真实地址
   
  # 等价于
  docker pull mysql
  docker pull docker.io/library/mysql:latest
   
  # 指定版本下载
  [root@iZ2zeg4ytp0whqtmxbsqiiZ ~]# docker pull mysql:5.7
  5.7: Pulling from library/mysql
  bf5952930446: Already exists 
  8254623a9871: Already exists 
  938e3e06dac4: Already exists 
  ea28ebf28884: Already exists 
  f3cef38785c2: Already exists 
  894f9792565a: Already exists 
  1d8a57523420: Already exists 
  5f09bf1d31c1: Pull complete 
  1b6ff254abe7: Pull complete 
  74310a0bf42d: Pull complete 
  d398726627fd: Pull complete 
  Digest: sha256:da58f943b94721d46e87d5de208dc07302a8b13e638cd1d24285d222376d6d84
  Status: Downloaded newer image for mysql:5.7
  docker.io/library/mysql:5.7
   
  # 查看本地镜像
  [root@iZ2zeg4ytp0whqtmxbsqiiZ ~]# docker images
  REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
  mysql               5.7                 718a6da099d8        6 days ago          448MB
  mysql               latest              0d64f46acfd1        6 days ago          544MB
  hello-world         latest              bf756fb1ae65        7 months ago        13.3kB
  
  ```

  - docker rmi 删除镜像

  ```shell
  [root@iZ2zeg4ytp0whqtmxbsqiiZ ~]# docker rmi -f IMAGE ID                        # 删除指定镜像
  [root@iZ2zeg4ytp0whqtmxbsqiiZ ~]# docker rmi -f IMAGE ID1 IMAGE ID2 IMAGE ID3   # 删除多个镜像
  [root@iZ2zeg4ytp0whqtmxbsqiiZ ~]#  docker rmi -f $(docker images -aq)           # 删除所有镜像
  ```



#### 容器命令

##### **新建容器并启动**

```shell
docker run [可选参数] image
# 参数说明
--name=“Name”   容器名字    tomcat01    tomcat02    用来区分容器
-d      后台方式运行
-it     使用交互方式运行，进入容器查看内容
-p      指定容器的端口     -p 8080:8080
    -p  ip:主机端口：容器端口
    -p  主机端口：容器端口（常用）
    -p  容器端口
    容器端口
-p      随机指定端口

```

##### 列出所有的运行的容器

```shell
# docker ps 命令
        # 列出当前正在运行的容器
-a      # 列出正在运行的容器包括历史容器
-n=?    # 显示最近创建的容器
-q      # 只显示当前容器的编号
 
[root@iZ2zeg4ytp0whqtmxbsqiiZ /]# docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
[root@iZ2zeg4ytp0whqtmxbsqiiZ /]# docker ps -a
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS                     PORTS               NAMES
77969f5dcbf9        centos              "/bin/bash"         5 minutes ago       Exited (0) 5 minutes ago                       xenodochial_bose
74e82b7980e7        centos              "/bin/bash"         16 minutes ago      Exited (0) 6 minutes ago                       silly_cori
a57250395804        bf756fb1ae65        "/hello"            7 hours ago         Exited (0) 7 hours ago                         elated_nash
392d674f4f18        bf756fb1ae65        "/hello"            8 hours ago         Exited (0) 8 hours ago                         distracted_mcnulty
571d1bc0e8e8        bf756fb1ae65        "/hello"            23 hours ago        Exited (0) 23 hours ago                        magical_burnell
 
[root@iZ2zeg4ytp0whqtmxbsqiiZ /]# docker ps -qa
77969f5dcbf9
74e82b7980e7
a57250395804
392d674f4f18
571d1bc0e8e8
```

##### **退出容器**

```shell
exit            # 直接退出容器并关闭
Ctrl + P + Q    # 容器不关闭退出
```

##### **删除容器**

```shell
docker rm -f 容器id                  # 删除指定容器
docker rm -f $(docker ps -aq)       # 删除所有容器
docker ps -a -q|xargs docker rm -f  # 删除所有的容器
```

##### 启动和停止容器

```shell
docker start 容器id           # 启动容器
docker restart 容器id         # 重启容器
docker stop 容器id            # 停止当前正在运行的容器
docker kill 容器id            # 强制停止当前的容器
```

##### 后台启动容器

```shell
# 命令 docker run -d 镜像名
[root@iZ2zeg4ytp0whqtmxbsqiiZ /]# docker run -d centos
 
# 问题 docker ps， 发现centos停止了
 
# 常见的坑， docker 容器使用后台运行， 就必须要有一个前台进程，docker发现没有应用，就会自动停止
# nginx， 容器启动后，发现自己没有提供服务，就会立即停止，就是没有程序了
```

##### 查看日志

```shell
docker logs -tf --tail number 容器id
 
[root@iZ2zeg4ytp0whqtmxbsqiiZ /]# docker logs -tf --tail 1 8d1621e09bff
2020-08-11T10:53:15.987702897Z [root@8d1621e09bff /]# exit      # 日志输出
 
# 自己编写一段shell脚本
[root@iZ2zeg4ytp0whqtmxbsqiiZ /]# docker run -d centos /bin/sh -c "while true;do echo xiaofan;sleep 1;done"
a0d580a21251da97bc050763cf2d5692a455c228fa2a711c3609872008e654c2
 
[root@iZ2zeg4ytp0whqtmxbsqiiZ /]# docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS               NAMES
a0d580a21251        centos              "/bin/sh -c 'while t…"   3 seconds ago       Up 1 second                             lucid_black
 
# 显示日志
-tf                 # 显示日志
--tail number       # 显示日志条数
[root@iZ2zeg4ytp0whqtmxbsqiiZ /]# docker logs -tf --tail 10 a0d580a21251
```

##### 查看容器中进程信息

```shell
# 命令 docker top 容器id
[root@iZ2zeg4ytp0whqtmxbsqiiZ /]# docker top df358bc06b17
UID                 PID                 PPID                C                   STIME               TTY     
root                28498               28482               0                   19:38               ?  
```

##### **查看镜像的元数据**

```shell
# 命令
docker inspect 容器id
```

##### **进入当前正在运行的容器**

```shell
# 命令
docker exec -it 容器id /bin/bash

# 方式二
docker attach 容器id
 
# docker exec       # 进入容器后开启一个新的终端，可以在里面操作
# docker attach     # 进入容器正在执行的终端，不会启动新的进程
```

##### **从容器中拷贝文件到主机**

```shell
docker cp 容器id：容器内路径    目的地主机路径
 
[root@iZ2zeg4ytp0whqtmxbsqiiZ /]# docker cp 7af535f807e0:/home/Test.java /home
```



#### docker部署软件实战

##### Docker安装Nginx

```shell
# 1. 搜索镜像 search 建议去docker hub搜索，可以看到帮助文档
# 2. 下载镜像 pull
# 3. 运行测试
[root@iZ2zeg4ytp0whqtmxbsqiiZ home]# docker images
REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
centos              latest              0d120b6ccaa8        32 hours ago        215MB
nginx               latest              08393e824c32        7 days ago          132MB
 
# -d 后台运行
# -name 给容器命名
# -p 宿主机端口：容器内部端口
[root@iZ2zeg4ytp0whqtmxbsqiiZ home]# docker run -d --name nginx01 -p 3344:80 nginx  # 后台方式启动启动镜像
fe9dc33a83294b1b240b1ebb0db9cb16bda880737db2c8a5c0a512fc819850e0
[root@iZ2zeg4ytp0whqtmxbsqiiZ home]# docker ps
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS              PORTS                  NAMES
fe9dc33a8329        nginx               "/docker-entrypoint.…"   4 seconds ago       Up 4 seconds        0.0.0.0:3344->80/tcp   nginx01
[root@iZ2zeg4ytp0whqtmxbsqiiZ home]# curl localhost:3344    # 本地访问测试
 
# 进入容器
[root@iZ2zeg4ytp0whqtmxbsqiiZ home]# docker exec -it nginx01 /bin/bash
root@fe9dc33a8329:/# whereis nginx
nginx: /usr/sbin/nginx /usr/lib/nginx /etc/nginx /usr/share/nginx
root@fe9dc33a8329:/# cd /etc/nginx/
root@fe9dc33a8329:/etc/nginx# ls
conf.d      koi-utf  mime.types  nginx.conf   uwsgi_params
fastcgi_params  koi-win  modules     scgi_params  win-utf

```

##### Docker安装Tomcat

```bash
# 官方的使用
docker run -it --rm tomcat:9.0
 
# 我们之前的启动都是后台的，停止了容器之后， 容器还是可以查到，docker run -it --rm 一般用来测试，用完就删
 
# 下载再启动
docker pull tomcat
 
# 启动运行
docker run -d -p 3344:8080 --name tomcat01 tomcat
 
# 测试访问没有问题
 
# 进入容器
docker exec -it tomcat01 /bin/bash
 
# 发现问题：1.linux命令少了， 2. webapps下内容为空，阿里云净吸纳过默认是最小的镜像，所有不必要的都剔除了，保证最小可运行环境即可
```



##### Docker部署es + kibana

```shell
# es 暴露的端口很多
# es 十分的耗内存
# es 的数据一般需要放置到安全目录！ 挂载
# --net somenetwork 网络配置
 
# 启动elasticsearch
docker run -d --name elasticsearch --net somenetwork -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.6.2
 
[root@iZ2zeg4ytp0whqtmxbsqiiZ home]# docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" elasticsearch:7.6.2
a920894a940b354d3c867079efada13d96cf9138712c76c8dea58fabd9c7e96f
 
# 启动了linux就卡主了，docker stats 查看cpu状态
 
# 测试一下es成功了
[root@iZ2zeg4ytp0whqtmxbsqiiZ home]# curl localhost:9200
{
  "name" : "a920894a940b",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "bxE1TJMEThKgwmk7Aa3fHQ",
  "version" : {
    "number" : "7.6.2",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "ef48eb35cf30adf4db14086e8aabd07ef6fb113f",
    "build_date" : "2020-03-26T06:34:37.794943Z",
    "build_snapshot" : false,
    "lucene_version" : "8.4.0",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
 
 
# 增加内存限制，修改配置文件 -e 环境配置修改
docker run -d --name elasticsearch -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e ES_JAVA_OPTS="-Xms64m -Xmx512m" elasticsearch:7.6.2
```

##### 可视化

- portainer

  ```shell
  docker run -d -p 8088:9000 --restart=always -v /var/run/docker.sock:/var/run/docker.sock --privileged=true portainer/portainer
   
  # 测试
  [root@iZ2zeg4ytp0whqtmxbsqiiZ home]# curl localhost:8088
  <!DOCTYPE html
  ><html lang="en" ng-app="portainer">
   
  # 外网访问 http://ip:8088
  
  ```

#### Docker原理

Docker镜像都是只读的，当容器启动时， 一个新的可写层被加载到镜像的顶部！，这一层就是我们通常说的容器层， 容器之下的都叫做镜像层

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020081215123458.png)

- commit镜像

```shell
docker commit 提交容器成为一个新的版本
 
# 命令和git 原理类似
docker commit -m="提交的描述信息" -a="作者" 容器id 目标镜像名：[TAG]
 
docker commit -a="xiaofan" -m="add webapps app" d798a5946c1f tomcat007:1.0

```

#### 容器数据卷

##### docker的理解回顾

将应用和环境打包成一个镜像！

数据？如果数据都在容器中，那么我们容器删除，数据就会丢失！`需求：数据可以持久化`

MySQL，容器删了，删库跑路！`需求：MySQL数据可以存储在本地！`

容器之间可以有一个数据共享技术！Docker容器中产生的数据，同步到本地！

这就是卷技术，目录的挂载，将我们容器内的目录挂载到linux目录上面！

**总结： **容器的持久化和同步操作！容器间数据也是可以共享的！

##### 使用数据卷

方式一：直接使用命令来挂载 -v

``` shell

docker run -it -v 主机目录：容器目录
 
[root@iZ2zeg4ytp0whqtmxbsqiiZ home]# docker run -it -v /home/ceshi:/home centos /bin/bash
```

