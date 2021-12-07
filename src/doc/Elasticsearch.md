#### jdk8安装

##### 先查看centos中自带的jdk并卸载

```shell
[root@root ~]# rpm -qa | grep jkd    //查看
[root@root ~]rpm -e | grep java     //删除
# 卸载 -e --nodeps 强制删除
[root@kuangshen ~]# rpm -e --nodeps jdk1.8.0_121-1.8.0_121-fcs.x86_64
```

##### jdk8安装

```shell
[root@root ~]# yum install java-1.8.0-openjdk.x86_64
一直y确定

# 检验安装
[root@root ~]# java -version

#设置jdk环境变量
[root@root alternatives]# vi /etc/profile

#在文件最后加入如下配置：
#set java environment
JAVA_HOME= /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.262.b10-0.el7_8.x86_64/jre
PATH=$PATH:$JAVA_HOME/bin
CLASSPATH=.:$JAVA_HOME/lib
export JAVA_HOME CLASSPATH PATH

#使profile文件立马生效
[root@root alternatives]#. /etc/profile   
```

#### 安装es7.6.1

> 引用 https://www.jianshu.com/p/b670648be8bb

> **新建用户并创建data跟logs目录（elasticsearch的策略会阻止root用户运行）**

```shell
useradd es
su es
cd ~ 
mkdir -p software/es
cd software/es
mkdir data logs
```

> **下载elasticsearch7.6.1安装包、并解压**

```shell
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-7.6.1-linux-x86_64.tar.gz

tar -xzvf elasticsearch-7.6.1-linux-x86_64.tar.gz
```

> **修改elasticsearch配置文件 (config/elasticsearch.yml) **

```shell
cluster.name: my-es
node.name: master
path.data: /home/es/software/es/data
path.logs: /home/es/software/es/log
network.host: 0.0.0.0 #如果需要外网访问则需要设置成0.0.0.0
http.port: 9200
discovery.seed_hosts: ["127.0.0.1"]
cluster.initial_master_nodes: ["master"]
node.master: true
node.data: true
```

#### 基础命令

```shell
#启动命令
./bin/elasticsearch
#停止启动
#查找ES进程
ps -ef | grep elastic
#杀掉ES进程
kill -9 2382（进程号）
```

#### 查询命令

```shell
match_phrase #会将检索关键词分词。match_phrase的分词结果必须在被检索字段的分词中都包含，而且顺序必须相同，而且默认必须都是连续的
match #会对输入进行分词处理后再去查询，部分命中的结果也会按照评分由高到低显示出来。
term #是将传入的文本原封不动地（不分词）拿去查询。

#bool查询 https://www.cnblogs.com/qdhxhz/p/11529107.html
must：    必须匹配。贡献算分
must_not：过滤子句，必须不能匹配，但不贡献算分 
should：  选择性匹配，至少满足一条。贡献算分
filter：  过滤子句，必须匹配，但不贡献算分

#boosting query 
#有一种场景就是我并不需要完全剔除，而是把需要剔除的那部分文档的,这个时候就可以使用boosting query
#boosting需要搭配三个关键字 positive , negative , negative_boost
#只有匹配了 positive查询 的文档才会被包含到结果集中，但是同时匹配了negative查询 的文档会被降低其相关度，通过将文档原本的_score和negative_boost参数进行相乘来得到新的_score。因此，negative_boost参数一般小于1.0。在下面的例子中，任何包含了指定负面词条的文档的_score都会是其原本_score的一半。

POST news/_search
{
  "query": {
    "boosting": {
      "positive": {
        "match": {
          "content": "apple"
        }
      },
      "negative": {
        "match": {
          "content": "pie"
        }
      },
      "negative_boost": 0.5
    }
  }
}

#constant_score(固定分数查询) 
#定义 常量分值查询，目的就是返回指定的score，一般都结合filter使用，因为filter context忽略score
POST news/_search
{
  "query": {
    "constant_score": {
      "filter": {
        "match": {
         "content":"apple"
        }
      },
      "boost": 2.5
    }
  }
}
#运行结果如下,可以看出分数都是2.5。
```

![](https://img2018.cnblogs.com/blog/1090617/201909/1090617-20190916191204939-725686846.jpg)

```shell
#dis_max(最佳匹配查询）
#只是取分数最高的那个query的分数而已。
GET /_search
{
    "query": {
        "dis_max" : {
            "queries" : [
                { "term" : { "title" : "Quick pets" }},
                { "term" : { "body" : "Quick pets" }}
            ],
            "tie_breaker" : 0.7
        }
    }
}
#解释：假设一条文档的'title'查询得分是 1，'body'查询得分是1.6。那么总得分为：1.6+1*0.7 = 2.3。如果我们去掉"tie_breaker" : 0.7 ，那么tie_breaker默认为0，那么这条文档的得分就是 1.6 + 1*0 = 1.6


```

#### 授权用户文件夹权限

``` shell
#授权文件夹权限 my文件夹和里面的文件权限都是不同的。现在递归修改，都改为统一的权限777
 chmod -R 777 my/
```

#### 问题解决

> **max virtual memory areas vm.max_map_count [65530] likely too low, increase to at least [262144]** 

```shell
#切换到root用户修改配置sysctl.conf

vi /etc/sysctl.conf 

#添加下面配置：

vm.max_map_count=655360

#并执行命令：

sysctl -p
```

> **max file descriptors [4096] for elasticsearch process is too low, increase to at least [65535]**

```shell
#打开/etc/security/limits.conf，在里面添加如下内容,其中*表示所有用户 nofile表示最大文件句柄数,表示能够打开的最大文件数目

* soft nofile 65536
* hard nofile 65536

#编辑/etc/pam.d/common-session，添加如下内容
session required pam_limits.so 

#编辑/etc/profile，添加如下内容
ulimit -SHn 65536

#然后重新启动机器,再利用ulimit -n查看文件句柄数,发现文件句柄数变为65536
```

#### IK分词器的安装

- **下载ik分词器**

```shell
https://github.com//medcl/elasticsearch-analysis-ik/releases/download/v7.6.1/elasticsearch-analysis-ik-7.6.1.zip
```

- **在es目录下的plugins文件下创建ik文件夹**
- **将下载的elasticsearch-analysis-ik-7.6.1.zip解压到ik文件夹中，重启es**
- **验证是否安装成功语句如下**

```shell
POST http://172.16.95.136:9200/_analyze
{
  "analyzer": "ik_max_word",
  "text":     "中华人民共和国国歌"
}
```



#### ES核心概念

##### 动态更新索引

> **如何在保留不变性的前提下实现倒排索引的更新**

```shell
用更多的索引。通过增加新的补充索引来反映新近的修改，而不是直接重写整个倒排索引。每一个倒排索引都会被轮流查询到，从最早的开始查询完后再对结果进行合并。Elasticsearch 基于 Lucene, 这个 java 库引入了按段搜索的概念。 每一段本身都是一个倒排索引，但索引在 Lucene中除表示所有段的集合外，还增加了提交点的概念一个列出了所有已知段的文件
```

![image-20211207152635750](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211207152635750.png)

​	   按段搜索会以如下流程执行：

- 新文档被收集到内存索引缓存。

  ![image-20211207152844144](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211207152844144.png)

- 不时地, 缓存被 提交。

  - 一个新的段—一个追加的倒排索引—被写入磁盘。

  - 一个新的包含新段名字的 提交点 被写入磁盘

  - 磁盘进行 同步—所有在文件系统缓存中等待的写入都刷新到磁盘，以确保它们

    被写入物理文件。

- 新的段被开启，让它包含的文档可见以被搜索
- 内存缓存被清空，等待接收新的文档

![image-20211207153054242](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211207153054242.png)

```shell
当一个查询被触发，所有已知的段按顺序被查询。词项统计会对所有段的结果进行聚合，以保证每个词和每个文档的关联都被准确计算。 这种方式可以用相对较低的成本将新文档添加到索引。段是不可改变的，所以既不能从把文档从旧的段中移除，也不能修改旧的段来进行反映文档的更新。 取而代之的是，每个提交点会包含一个 .del 文件，文件中会列出这些被删除文档的段信息。当一个文档被 “删除” 时，它实际上只是在 .del 文件中被 标记 删除。一个被标记删除的文档仍然可以被查询匹配到， 但它会在最终结果被返回前从结果集中移除。文档更新也是类似的操作方式：当一个文档被更新时，旧版本文档被标记删除，文档的新版本被索引到一个新的段中。 可能两个版本的文档都会被一个查询匹配到，但被删除的那个旧版本文档在结果集返回前就已经被移除。
```

##### 近实时搜索

```shel
随着按段（per-segment）搜索的发展，一个新的文档从索引到可被搜索的延迟显著降低了。新文档在几分钟之内即可被检索，但这样还是不够快。磁盘在这里成为了瓶颈。提交(Commiting）一个新的段到磁盘需要一个 fsync 来确保段被物理性地写入磁盘，这样在断电的时候就不会丢失数据。 但是 fsync 操作代价很大; 如果每次索引一个文档都去执行一次的话会造成很大的性能问题。我们需要的是一个更轻量的方式来使一个文档可被搜索这意味着 fsync 要从整个过程中被移除。在 Elasticsearch 和磁盘之间是文件系统缓存。 像之前描述的一样， 在内存索引缓冲区中的文档会被写入到一个新的段中。 但是这里新段会被先写入到文件系统缓存—这一步代价会比较低，稍后再被刷新到磁盘—这一步代价比较高。不过只要文件已经在缓存中，就可以像其它文件一样被打开和读取了。
```

```shel
在 Elasticsearch中，写入和打开一个新段的轻量的过程叫做refresh 。 默认情况下每个分片会每秒自动刷新一次。这就是为什么我们说 Elasticsearch 是近实时搜索: 文档的变化并不是立即对搜索可见，但会在一秒之内变为可见。这些行为可能会对新用户造成困惑: 他们索引了一个文档然后尝试搜索它，但却没有搜到。这个问题的解决办法是用 refresh API 执行一次手动刷新: /users/_refresh

尽管刷新是比提交轻量很多的操作，它还是会有性能开销。当写测试的时候， 手动刷新很有用，但是不要在生产环境下每次索引一个文档都去手动刷新。相反，你的应用需要意识到 Elasticsearch 的近实时的性质，并接受它的不足。

并不是所有的情况都需要每秒刷新。可能你正在使用 Elasticsearch 索引大量的日志文件，你可能想优化索引速度而不是近实时搜索， 可以通过设置 refresh_interval ， 降低每个索引的刷新频率
```

```shell
{
  "settings": {
  "refresh_interval": "30s" 
  } 
}
#关闭自动刷新
PUT /users/_settings
{ "refresh_interval": -1 } 
#每一秒刷新
PUT /users/_settings
{ "refresh_interval": "1s" }
```

##### 持久化变更

```shell
如果没有用 fsync 把数据从文件系统缓存刷（flush）到硬盘，我们不能保证数据在断电甚至是程序正常退出之后依然存在。为了保证Elasticsearch的可靠性，需要确保数据变化被持久化到磁盘。在 动态更新索引，我们说一次完整的提交会将段刷到磁盘，并写入一个包含所有段列表的提交点。Elasticsearch 在启动或重新打开一个索引的过程中使用这个提交点来判断哪些段隶属于当前分片。即使通过每秒刷新（refresh）实现了近实时搜索，我们仍然需要经常进行完整提交来确保能从失败中恢复。但在两次提交之间发生变化的文档怎么办？我们也不希望丢失掉这些数
据。Elasticsearch 增加了一个 translog ，或者叫事务日志，在每一次对 Elasticsearch 进行操作时均进行了日志记录.
```

整个流程如下：

- 一个文档被索引之后，就会被添加到内存缓冲区，并且追加到了 translog

![image-20211207160531450](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211207160531450.png)

- 刷新（refresh）使分片每秒被刷新（refresh）一次：
  - 这些在内存缓冲区的文档被写入到一个新的段中，且没有进行 fsync 操作。
  - 这个段被打开，使其可被搜索
  - 内存缓冲区被清空

![image-20211207160609926](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211207160609926.png)

- 这个进程继续工作，更多的文档被添加到内存缓冲区和追加到事务日志

![image-20211207160642110](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211207160642110.png)

- 每隔一段时间—例如 translog 变得越来越大—索引被刷新（flush）；一个新的 translog 被创建，并且一个全量提交被执行
  - 所有在内存缓冲区的文档都被写入一个新的段。
  - 缓冲区被清空。
  - 一个提交点被写入硬盘。
  - 文件系统缓存通过 fsync 被刷新（flush）。
  - 老的 translog 被删除。

```shel
translog 提供所有还没有被刷到磁盘的操作的一个持久化纪录。当 Elasticsearch 启动的时候， 它会从磁盘中使用最后一个提交点去恢复已知的段，并且会重放 translog 中所有在最后一次提交后发生的变更操作。translog 也被用来提供实时 CRUD 。当你试着通过 ID 查询、更新、删除一个文档，它会在尝试从相应的段中检索之前， 首先检查 translog 任何最近的变更。这意味着它总是能够实时地获取到文档的最新版本。
```

![image-20211207161131225](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211207161131225.png)

```shell
执行一个提交并且截断translog的行为在 Elasticsearch被称作一次flush,分片每30分钟被自动刷新（flush），或者在translog 太大的时候也会刷新.
```

##### 段合并

```shell
由于自动刷新流程每秒会创建一个新的段,这样会导致短时间内的段数量暴增。而段数目太多会带来较大的麻烦。 每一个段都会消耗文件句柄、内存和 cpu运行周期。更重要的是,每个搜索请求都必须轮流检查每个段；所以段越多，搜索也就越慢。Elasticsearch通过在后台进行段合并来解决这个问题。小的段被合并到大的段，然后这些大的段再被合并到更大的段。段合并的时候会将那些旧的已删除文档从文件系统中清除。被删除的文档（或被更新文档的旧版本）不会被拷贝到新的大段中。启动段合并不需要你做任何事。进行索引和搜索时会自动进行。
```

- 当索引的时候，刷新（refresh）操作会创建新的段并将段打开以供搜索使用。
- 合并进程选择一小部分大小相似的段，并且在后台将它们合并到更大的段中。这并不会中断索引和搜索。

![image-20211207162113608](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211207162113608.png)

- 一旦合并结束，老的段被删除
  - 新的段被刷新（flush）到了磁盘。 ** 写入一个包含新段且排除旧的和较小的段的新提交点。
  - 新的段被打开用来搜索。
  - 老的段被删除。

![image-20211207162236580](/Users/kevinlee/Library/Application Support/typora-user-images/image-20211207162236580.png)

合并大的段需要消耗大量的 I/O 和 CPU 资源，如果任其发展会影响搜索性能。Elasticsearch在默认情况下会对合并流程进行资源限制，所以搜索仍然 有足够的资源很好地执行。
