### String类型

![image-20220113163034230](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220113163034230.png)

#### 常用操作命令

| 命令                                       | 说明                                       |
| ------------------------------------------ | ------------------------------------------ |
| set                                        | 设置一个key/value                          |
| get                                        | 根据key获得对应的value                     |
| mset                                       | 一次设置多个key value                      |
| mget                                       | 一次获得多个key的value                     |
| getset                                     | 获得原始key的值，同时设置新值              |
| strlen                                     | 获得对应key存储value的长度                 |
| append                                     | 为对应key的value追加内容                   |
| getrange 索引0开始                         | 截取value的内容                            |
| setex                                      | 设置一个key存活的有效期（秒）              |
| psetex                                     | 设置一个key存活的有效期（毫秒）            |
| setnx                                      | 存在不做任何操作,不存在添加                |
| msetnx原子操作(只要有一个存在不做任何操作) | 可以同时设置多个key,只有有一个存在都不保存 |
| decr                                       | 进行数值类型的-1操作                       |
| decrby                                     | 根据提供的数据进行减法操作                 |
| Incr                                       | 进行数值类型的+1操作                       |
| incrby                                     | 根据提供的数据进行加法操作                 |
| Incrbyfloat                                | 根据提供的数据加入浮点数                   |

---

### List类型

list 列表 相当于java中list 集合 特点 元素有序 且 可以重复

#### 内存存储模型

![image-20220113163240679](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220113163240679.png)

#### 常用操作指令

| 命令    | 说明                                 |
| ------- | ------------------------------------ |
| lpush   | 将某个值加入到一个key列表头部        |
| lpushx  | 同lpush,但是必须要保证这个key存在    |
| rpush   | 将某个值加入到一个key列表末尾        |
| rpushx  | 同rpush,但是必须要保证这个key存在    |
| lpop    | 返回和移除列表左边的第一个元素       |
| rpop    | 返回和移除列表右边的第一个元素       |
| lrange  | 获取某一个下标区间内的元素           |
| llen    | 获取列表元素个数                     |
| lset    | 设置某一个指定索引的值(索引必须存在) |
| lindex  | 获取某一个指定索引位置的元素         |
| lrem    | 删除重复元素                         |
| ltrim   | 保留列表中特定区间内的元素           |
| linsert | 在某一个元素之前，之后插入新元素     |

---

### Set类型

#### 内存存储模型

![image-20220117104545866](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117104545866.png)

#### 常用命令

| 命令        | 说明                                              |
| ----------- | ------------------------------------------------- |
| sadd        | 为集合添加元素                                    |
| smembers    | 显示集合中所有元素 无序                           |
| scard       | 返回集合中元素的个数                              |
| spop        | 随机返回一个元素 并将元素在集合中删除             |
| smove       | 从一个集合中向另一个集合移动元素 必须是同一种类型 |
| srem        | 从集合中删除一个元素                              |
| sismember   | 判断一个集合中是否含有这个元素                    |
| srandmember | 随机返回元素                                      |
| sdiff       | 去掉第一个集合中其它集合含有的相同元素            |
| sinter      | 求交集                                            |

---

### ZSet类型

特点: 可排序的set集合 排序 不可重复

Redis 有序集合和集合一样也是 string 类型元素的集合,且不允许重复的成员。

不同的是每个元素都会关联一个 double 类型的分数。redis 正是通过分数来为集合中的成员进行从小到大的排序。

有序集合的成员是唯一的,但分数(score)却可以重复。

#### 内存模型

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020120611105776.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaTE5ODYyMQ==,size_16,color_FFFFFF,t_70)

#### 常用命令

| 命令                       | 说明                         |
| -------------------------- | ---------------------------- |
| zadd                       | 添加一个有序集合元素         |
| zcard                      | 返回集合的元素个数           |
| zrange 升序 zrevrange 降序 | 返回一个范围内的元素         |
| zrangebyscore              | 按照分数查找一个范围内的元素 |
| zrank                      | 返回排名                     |
| zrevrank                   | 倒序排名                     |
| zscore                     | 显示某一个元素的分数         |
| zrem                       | 移除某一个元素               |
| zincrby                    | 给某个特定元素加分           |

---

### hash类型

特点: value 是一个map结构 存在key value key 无序的

#### 内存模型

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201210001232131.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaTE5ODYyMQ==,size_16,color_FFFFFF,t_70)

#### 常用命令

| 命令         | 说明                    |
| ------------ | ----------------------- |
| hset         | 设置一个key/value对     |
| hget         | 获得一个key对应的value  |
| hgetall      | 获得所有的key/value对   |
| hdel         | 删除某一个key/value对   |
| hexists      | 判断一个key是否存在     |
| hkeys        | 获得所有的key           |
| hvals        | 获得所有的value         |
| hmset        | 设置多个key/value       |
| hmget        | 获得多个key的value      |
| hsetnx       | 设置一个不存在的key的值 |
| hincrby      | 为value进行加法运算     |
| hincrbyfloat | 为value加入浮点值       |

### 持久化机制

#### 快照(Snapshot)

#####  特点

这种方式可以将某一时刻的所有数据都写入硬盘中,当然这也是**redis的默认开启持久化方式**,保存的文件是以.rdb形式结尾的文件因此这种方式也称之为RDB方式。

![image-20220117105612061](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117105612061.png)

##### 快照生成方式

- 客户端方式: BGSAVE 和 SAVE指令
- 服务器配置自动触发

```shell
# 1.客户端方式之BGSAVE
- a.客户端可以使用BGSAVE命令来创建一个快照,当接收到客户端的BGSAVE命令时,redis会调用fork¹来创建一个子进程,然后子进程负责将快照写入磁盘中,而父进程则继续处理命令请求。
`名词解释: fork当一个进程创建子进程的时候,底层的操作系统会创建该进程的一个副本,在类unix系统中创建子进程的操作会进行优化:在刚开始的时候,父子进程共享相同内存,直到父进程或子进程对内存进行了写之后,对被写入的内存的共享才会
```

![image-20220117110716929](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117110716929.png)

```shell
# 2.客户端方式之SAVE
- b.客户端还可以使用SAVE命令来创建一个快照,接收到SAVE命令的redis服务器在快照创建完毕之前将不再响应任何其他的命令
```

![image-20220117110812804](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117110812804.png)

- **注意: SAVE命令并不常用,使用SAVE命令在快照创建完毕之前,redis处于阻塞状态,无法对外服务**

```shell
# 3.服务器配置方式之满足配置自动触发.
如果用户在redis.conf中设置了save配置选项,redis会在save选项条件满足之后自动触发一次BGSAVE命令,如果设置多个save配置选项,当任意一个save配置选项条件满足,redis也会触发一次BGSAVE命令
```

![image-20220117111257163](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117111257163.png)

```shell
# 4.服务器接收客户端shutdown指令
- 当redis通过shutdown指令接收到关闭服务器的请求时,会执行一个save命令,阻塞所有的客户端,不再执行客户端执行发送的任何命令,并且在save命令执行完毕之后关闭服务器
```

##### 配置生成快照名称和位置

```shell
#1.修改生成快照名称
- dbfilename dump.rdb

# 2.修改生成位置
- dir ./
```

![image-20220117111954934](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117111954934.png)



### AOF 只追加日志文件

#### 特点

这种方式可以将所有客户端执行的写命令记录到日志文件中,AOF持久化会将被执行的写命令写到AOF的文件末尾,以此来记录数据发生的变化,因此只要redis从头到尾执行一次AOF文件所包含的所有写命令,就可以恢复AOF文件的记录的数据集.

![image-20220117162620925](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117162620925.png)



#### 开启AOF持久化

在redis的默认配置中AOF持久化机制是没有开启的，需要在配置中开启

```shell
# 1.开启AOF持久化
- a.修改 appendonly yes 开启持久化
- b.修改 appendfilename "appendonly.aof" 指定生成文件名称
```

![image-20220117162712312](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117162712312.png)

#### 日志追加频率

```shell
# 1.always 【谨慎使用】
- 说明: 每个redis写命令都要同步写入硬盘,严重降低redis速度
- 解释: 如果用户使用了always选项,那么每个redis写命令都会被写入硬盘,从而将发生系统崩溃时出现的数据丢失减到最少;遗憾的是,因为这种同步策略需要对硬盘进行大量的写入操作,所以redis处理命令的速度会受到硬盘性能的限制;
- 注意: 转盘式硬盘在这种频率下200左右个命令/s ; 固态硬盘(SSD) 几百万个命令/s;
- 警告: 使用SSD用户请谨慎使用always选项,这种模式不断写入少量数据的做法有可能会引发严重的写入放大问题,导致将固态硬盘的寿命从原来的几年降低为几个月。

# 2.everysec 【推荐】
- 说明: 每秒执行一次同步显式的将多个写命令同步到磁盘
- 解释： 为了兼顾数据安全和写入性能,用户可以考虑使用everysec选项,让redis每秒一次的频率对AOF文件进行同步;redis每秒同步一次AOF文件时性能和不使用任何持久化特性时的性能相差无几,而通过每秒同步一次AOF文件,redis可以保证,即使系统崩溃,用户最多丢失一秒之内产生的数据。

# 3.no	【不推荐】
- 说明: 由操作系统决定何时同步 
- 解释：最后使用no选项,将完全有操作系统决定什么时候同步AOF日志文件,这个选项不会对redis性能带来影响但是系统崩溃时,会丢失不定数量的数据,另外如果用户硬盘处理写入操作不够快的话,当缓冲区被等待写入硬盘数据填满时,redis会处于阻塞状态,并导致redis的处理命令请求的速度变慢。

```

#### 修改同步频率

```shell
# 1.修改日志同步频率
- 修改appendfsync everysec|always|no 指定
```

![image-20220117162919955](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117162919955.png)

### AOF文件的重写

#### AOF带来的问题

AOF的方式也同时带来了另一个问题。持久化文件会变的越来越大。例如我们调用incr test命令100次，文件中必须保存全部的100条命令，其实有99条都是多余的。因为要恢复数据库的状态其实文件中保存一条set test 100就够了。为了压缩aof的持久化文件Redis提供了AOF重写(ReWriter)机制。

#### AOF重写

用来在一定程度上减小AOF文件的体积

#### 触发重写方式

```shell
# 1.客户端方式触发重写
- 执行BGREWRITEAOF命令  不会阻塞redis的服务

# 2.服务器配置方式自动触发
- 配置redis.conf中的auto-aof-rewrite-percentage选项 参照下图↓↓↓
- 如果设置auto-aof-rewrite-percentage值为100和auto-aof-rewrite-min-size 64mb,并且启用的AOF持久化时,那么当AOF文件体积大于64M,并且AOF文件的体积比上一次重写之后体积大了至少一倍(100%)时,会自动触发,如果重写过于频繁,用户可以考虑将auto-aof-rewrite-percentage设置为更大
```

![image-20220117163258728](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117163258728.png)

#### 重写原理

**注意：重写aof文件的操作，并没有读取旧的aof文件，而是将整个内存中的数据库内容用命令的方式重写了一个新的aof文件,替换原有的文件这点和快照有点类似。**

```shell
# 重写流程
- 1. redis调用fork ，现在有父子两个进程 子进程根据内存中的数据库快照，往临时文件中写入重建数据库状态的命令
- 2. 父进程继续处理client请求，除了把写命令写入到原来的aof文件中。同时把收到的写命令缓存起来。这样就能保证如果子进程重写失败的话并不会出问题。
- 3. 当子进程把快照内容写入已命令方式写到临时文件中后，子进程发信号通知父进程。然后父进程把缓存的写命令也写入到临时文件。
- 4. 现在父进程可以使用临时文件替换老的aof文件，并重命名，后面收到的写命令也开始往新的aof文件中追加。
```

![image-20220117163509971](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220117163509971.png)

### 持久化总结

两种持久化方案既可以同时使用(aof),又可以单独使用,在某种情况下也可以都不使用,具体使用那种持久化方案取决于用户的数据和应用决定。

无论使用AOF还是快照机制持久化,将数据持久化到硬盘都是有必要的,除了持久化外,用户还应该对持久化的文件进行备份(最好备份在多个不同地方)。













