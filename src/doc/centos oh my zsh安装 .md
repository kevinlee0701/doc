#### 1.安装zsh包

```shell
yum -y install zsh
```

#### 2.切换默认shell为zsh

```shell
chsh -s /bin/zsh
```

#### 3.重启服务器让修改的配置生效

```shell
shutdow r now
```

#### 4.安装on my zsh

```shell
sh -c "$(wget https://raw.githubusercontent.com/robbyrussell/oh-my-zsh/master/tools/install.sh -O -)"
```

#### 5.增强的实时自动命令补全插件：Incremental completion on zsh

```shell
mkdir $ZSH_CUSTOM/plugins/incr
curl -fsSL https://mimosa-pudica.net/src/incr-0.2.zsh -o $ZSH_CUSTOM/plugins/incr/incr.zsh
echo 'source $ZSH_CUSTOM/plugins/incr/incr.zsh' >> ~/.zshrc
source ~/.zshrc
```

