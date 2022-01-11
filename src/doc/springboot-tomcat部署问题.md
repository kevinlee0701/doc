springboot项目，打包成war，放到tomcat的webapp下，访问404。要查看问题根本，首先看看tomcat启动的时候，项目启动了么？本人是springboot的启动页面就没有加载。所以是项目本身就没启动成功。

解决办法：

#### 修改pom.xml

```xml
<packaging>war</packaging>
```

#### 移除[嵌入式](https://so.csdn.net/so/search?q=嵌入式)的tomcat依赖

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-tomcat</artifactId>
  <scope>provided</scope>
</dependency>
```

#### 修改启动类，启动类继承SpringBootServletInitializer

```java
public class BookApplication extends SpringBootServletInitializer{}
```

#### 打包

```shell
mvn clean package -Dmaven.test.skip=true
```

