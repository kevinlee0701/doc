### 安装

#### 	官网地址

```http
https://www.jenkins.io/download/
```

![image-20220107155537168](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107155537168.png)

#### 命令安装

安装jenkins命令如下：

```shell
sudo wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key

yum install epel-release # repository that provides 'daemonize'
yum install java-11-openjdk-devel
yum install jenkins
```

jenkins的启动命令：

```shell
sudo service jenkins start
```

当然，启动之后，它可能出现下面的问题：

![image-20220107160031354](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160031354.png)

图上表明是你`java`的引入路径有问题了~

上图意思是：启动程序找不到`java`。

那么，你需要执行`vim /etc/init.d/jenkins`命令进入文件，修改其路径就行了，如下：

![image-20220107160116436](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160116436.png)

顺利启动后，控制台有个`success`的提醒。这时，你在浏览器上输入`http://your server ip:8080`就可以打开了，呈现信息如下图。`jenkins`的默认端口号是`8080`，当然，你可以执行`vim /etc/sysconfig/jenkins`进入文件修改端口号等。

#### Jenkins的使用

#### 解锁

![image-20220107160214346](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160214346.png)

我们执行`cat /var/lib/jenkins/secrets/initialAdminPassword`命令得到`Administrator password`，然后点击**继续**按钮，往下走

![image-20220107160243585](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160243585.png)

我们选择`安装推荐的插件`，等待安装完毕。如果有安装失败的插件可以跳过，之后可以根据需求安装。【安装过程请保持网络的顺畅】

#### 初始化账号和密码

你可以创建自己的管理员用户信息，当然也可以点击`使用admin账号继续`链接跳过。

![image-20220107160335692](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160335692.png)

设置完成之后，进入界面：

![image-20220107160353902](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160353902.png)

#### 关联 github

```shell
secret text`在`github`上被称为`token
```

进入github --> Settings --> Developer settings --> Personal access tokens -> Generate new token

![image-20220107160456436](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160456436.png)

之后生成一个新的`token`:

![image-20220107160519902](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160519902.png)

填写好`token`的名称，勾选完上面的两个选项，之后按`Generate token`按钮进行确认：

![image-20220107160553473](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160553473.png)

自己先保存此`token`，如果丢失，之后再也无法找到这个`token`了。

#### 设置 github webhooks

接着，选择自己的一个`github`项目，这里我用仓库地址为https://github.com/reng99/blogs作为例子：

进入github上指定的项目 --> Settings --> Webhooks --> Add Webhook --> 输入刚刚部署jenkins的服务器的IP

![image-20220107160717573](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160717573.png)

```
Payload URL`的内容就是`http://your ip:端口
```

#### 配置GitHub Plugin

系统管理 --> 系统设置 --> GitHub --> 添加Github服务器

![image-20220107160859674](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107160859674.png)



`API URL` 输入 `https://api.github.com`，凭证Credentials点击添加，类型选择`Secret Text`,具体如下图所示:

![image-20220107161112617](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161112617.png)

点击`添加`按钮后，下拉选择凭证，选择你新增的那个凭证，然后点击`连接测试`按钮，提示`Credentials verified for user xxx, rate limit: xxx`信息，则表明有效。

![image-20220107161138433](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161138433.png)

#### 创建一个freestyle任务

进入主页 --> 新建任务

![image-20220107161214893](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161214893.png)

#### General设置

填写`GitHub project URL`，也就是你的主页`https://github.com/your_name/your_repo_name`，我这里使用我的博客仓库进行尝试`https://github.com/reng99/blogs`

![image-20220107161250669](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161250669.png)

#### 配置源码管理

勾选`Git`选项之后，就会出现相关的填写项，根据下图的指引来填写就行了~

![image-20220107161319921](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161319921.png)

填写项目的git地址, eg： `https://github.com/your_name/your_repo_name.git`

添加github用户和密码（要是一个有写权限的github账号，此步骤见下图）

选择githubweb源码库浏览器，并填上你的项目URL，这样每次构建都会生成对应的changes，可直接链到github上看变更详情


点击`添加`增加`Credentials`：

![image-20220107161347747](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161347747.png)

#### 构建触发器

勾选`GitHub hook trigger for GITScm polling`即可~

![image-20220107161413331](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161413331.png)

#### 构建环境配置

勾选`Use secret test(s) or file(s)`即可~

![image-20220107161439602](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161439602.png)

#### 设置绑定

选择绑定 --> 点击新增按钮 --> 选择Secret text --> 下拉选择你之前设置的secret token --> 之后应用

![image-20220107161503647](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161503647.png)

#### 设置构建

我这里基本没怎么操作，你可以根据自己的需求来编写构建的脚本了。

![image-20220107161538355](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161538355.png)

#### 构建后操作

![image-20220107161601432](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161601432.png)

你可以根据需求更改构建后的操作，我这里都默认

### 错误处理

在进行集成的过程中，你可能会遇到下面的这些问题~

#### 重新安插插件

在进行初始化的时候，一些插件我们可能会安装失败。不过，不要在意，你可以进来之后再针对需要的插件进行安装。

![image-20220107161651636](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161651636.png)

安装成功之后，进入`/restart/`路径对`jenkins`重启。

![image-20220107161712985](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161712985.png)

#### git command错误

![image-20220107161741876](/Users/kevinlee/Library/Application Support/typora-user-images/image-20220107161741876.png)

进入服务器查看是否安装了git

```shell
git version
```

没有git，此时需要安装git。

```shell
yum install git
```

#### 构建触发器找不到github选项

在构建触发器的时候，找不到选项`Build when a change is pushed to Github`

那是因为新版的`jenkins`将其修改为`GitHub hook trigger for GITScm polling`了。