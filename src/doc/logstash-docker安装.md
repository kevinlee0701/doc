``` shell
#拉取镜像
docker pull docker.elastic.co/logstash/logstash:7.2.0 
#运行容器
docker run --name logstash --link=elasticsearch:logstash-es  -p 9001:9001 -d docker.elastic.co/logstash/logstash:7.2.0

#创建一个自己的network
docker network create kevinNetwork
#将elasticsearch加入自己创建的network
docker network connect kevinNetwork elasticsearch
#将logstash加入自己创建的network
docker network connect kevinNetwork logstash
#可以查看network下的容器信息
docker network inspect kevinNetwork

#进入容器
docker exec -it logstash /bin/bash

#指定容器启动时加载的配置文件，在logstash.yml里添加如下一句话
path.config: /usr/share/logstash/config/logstash-sample.conf

```

``` shell
#修改配置文件logstash-sample.conf 如下：
input {
  tcp {
    # Logstash 作为服务
    mode => "server"
    # 开放9001端口进行采集日志
    port => 9001
    # 编解码器
    #codec => json_lines
  }
}

output {
  elasticsearch {
  	action =>"index"
    # 配置ES的地址
    hosts => "http://elasticsearch:9200"
    # 在ES里产生的index的名称
    index => "logstash-log"
    #user => "elastic"
    #password => "changeme"
  }
  stdout {
    codec => rubydebug
  }
}
```

