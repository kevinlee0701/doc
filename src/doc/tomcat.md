### 下载

#### 地址

```http
https://tomcat.apache.org/download-90.cgi
```

### 安装

打开目录，将安装包上传到目录下

```shell
cd /home/kevinlee/software
#解压压缩包
tar -xzf apache-tomcat-9.0.56.tar.gz

```

### 配置tomcat权限

打开目录conf，编辑 tomcat-users.xml

```shell
cd apache-tomcat-9.0.56/conf
vim tomcat-users.xml
```

内容如下:

```xml
<role rolename="tomcat"/>
<role rolename="role1"/>
<role rolename="manager-script"/>
<role rolename="manager-gui"/>
<role rolename="manager-status"/>
<role rolename="admin-gui"/>
<role rolename="admin-script"/>
<user username="tomcat" password="tomcat" roles="tomcat,manager-gui,manager-script,admin-gui,admin-script"/>
```

修改远程访问

打开目录 /home/kevinlee/software/apache-tomcat-9.0.56/webapps/manager/META-INF,修改context.xml,注释以下内容

```xml
<!-- 
  <Valve className="org.apache.catalina.valves.RemoteAddrValve"
         allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1" />-->
```



