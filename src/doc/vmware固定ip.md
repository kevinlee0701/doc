引用：https://www.cnblogs.com/itbsl/p/10998696.html

> 修改虚拟机

- 把网络配置改成nat模式

![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610164805904-1878811384.png)



![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610164812040-64855612.png)

- 通过Mac终端进入VMware Fusion的vmnet8目录

```
cd /Library/Preferences/VMware\ Fusion/vmnet8
```

![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610164819920-1999038053.png)

- 查看nat.conf内容

![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610164827222-1773170825.png)

- 查看cat dhcpd.conf

![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610164835758-181641312.png)

==注意range 这个是虚拟机允许选择的静态ip地址范围，自定义的静态ip地址必须要在这个范围内==

> 查看mac网络配置

- 获取DNS(在mac系统偏好设置—>网络

![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610164843039-18488902.png)



![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610164849104-441757788.png)

![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610164855065-1139533275.png)



> 登录CentOS

- 进入虚拟机的network-scripts目录

```shell
cd /etc/sysconfig/network-scripts
```

![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610164947943-1391299598.png)

- 找到ifcfg-en开头的文件,上图中我的是ifcfg-ens33，通过vi编辑该文件，下图是默认配置

![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610164955215-574987874.png)

- 修改配置如下：

![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610165000839-393578534.png)









- 保存之后，重启服务使修改生效

```shel
service network restart
```

ping一下百度看看，成功Ping到

![img](https://img2018.cnblogs.com/blog/720430/201906/720430-20190610165009727-1011741530.png)

- 接下来我们就可以通过SecureCRT等工具远程连接了，有一点请记住，如果你换了一个地方上网的话，可能会发现你的虚拟机有不通了，那是因为DNS地址发生了变化，此时只需要再次编辑ifcfg-enxxx文件，然后加上你现在网络的DNS地址即可，如：

```
DNS1=192.168.0.1
DNS2=114.114.114.114
```

