# MySQL数据库基础

## 1.1-认识MySQL

### 什么是数据库

* 计算机处理和存储的一切信息都是数据。
* 计算机系统中一种用于存取数据的程序。
* 一种：
  * 计算机系统中有很多种能够存取数据的程序
  * 它们各有特征和长处，有自己的适用范围。
* 存取：
  * 能够保存数据避免丢失。
  * 能够按照需求找到符合条件的数据。

### 为什么要使用数据库

数据库帮助我们解决一下数据存取难题：

* 较大数据量
* 网络控制
* 并发访问
* 高性能要求
* 事务控制
* 持久化和数据安全
* 查询数据需求逻辑复杂

### 数据库分类

* 关系型数据库
  * MySQL
  * Oracle
  * SQL Server
  * PostgreSQL

* 非关系型数据库
  * hadoop：存放大数据
  * mongoDB： 文档型数据库
  * redis：键值型数据库
  * Cassandra：分布式数据库

**最显著的区别：是否使用结构化查询语句（SQL）**

### 为什么学习MySQL

MySQL：The world's most popular open source database

* 最流行
* 开源
* 并不是最先进

* 前三强中唯一的开源数据库。
* 在互联网企业中占据绝对主流地位。

* 基于GPL协议开放源代码
* 社区版完全免费
* 代码允许自由的进行修改

* 易于学习：
  * MySQL具备关系型数据库核心功能但是特性并不繁多。
  * 架构设计上趋于精简。
  * 非常适合新手学习关系型数据库，入门后可向其他数据库发展。

### 谁需要学习MySQL

* 应用开发者
* DBA

### 学习目标 - 应用开发者

有助于利用MySQL开发出性能优异的应用程序

### 学习目标 - DBA

为企业提供可靠的数据库技术保障

## 1.2-轻松安装MySQL

### 轻松部署MySQL

* Windows下安装MySQL
  * 图形化工具安装， MySQL Installer
* Linux（Ubuntu）下安装MySQL
  * 包管理安装，apt-get

**Windows安装时在安装中间starting server时报错解决办法是手动进入服务管理把MySQL服务的登录方式改为用本地账户且允许与桌面交互**

在Ubuntu下可以用`apt-cache search mysql-server`查看可用的软件包

使用`sudo apt-get install mysql-server-5.6`安装MySQL5.6

启动与停止MySQL服务：

```bash
# 启动
sudo /etc/init.d/mysql start
# 或者
sudo service mysql start

# 停止
sudo /etc/init.d/mysql stop
# 或者
sudo service mysql stop

# 重启
sudo service mysql restart

# 查看状态
sudo /etc/init.d/mysql status
```


## 1.3-MySQL数据库连接

### 工作中常用到的三种连接方式

* Java App + JDBC client（其他语言也有，比如Python的MySQLdb）
* MySQL client
* "MySQL" utility

### 使用应用程序连接MySQL

* 应用程序使用驱动（connector/driver）客户端连接MySQL
* MySQL驱动程序涵盖各种主流语言

### 使用命令行连接MySQL

* 安装MySQL客户端软件包
* 设置环境变量（Linux）

#### 如何安装MySQL-client

* 从软件源安装`sudo apt-get install mysql-client`

#### 验证MySQL的安装

`mysql -V`

### 命令行连接MySQL的两种方式

* Socket连接（本地连接）
* TCP/IP连接（远程连接）

### 使用Socket连接

```bash
# 需要指定socket文件和用户名、密码
mysql -S/tmp/mysql.sock -uroot -p
```

### 远程连接

```bash
# 需要指定IP和端口
mysql -h127.0.0.1 -P3306 -uroot -p
```

### 本地连接VS远程连接

* 本地连接只能在MySQL服务器上创建，常用作为MySQL状态检查，或程序和MySQL部署在一台机器上。
* 远程连接在MySQL服务器内外都能生效，适合应用服务器和MySQL部署在不同机器上的场景。

### 在Windows下用命令行连接MySQL

```cmd
mysql -hlocalhost -P3306 -uroot -p
```

### 连接进入之后可以做什么

```sql
# 数据库状态
status;

# 展示当前连接
show processlist;
```

### 使用命令行连接MySQL的注意事项

* socket一般存储路径为：/tmp/mysql.sock
```sql
# 如果找不到文件可以通过tcp连接进来然后通过如下命令查找
show global variables like 'socket';
```
* socket文件的权限必须是777
* 不要将密码直接输入在命令行里，存在安全风险！

### 命令行连接MySQL的特点

* MySQL命令行里有丰富的扩展参数
* DBA运维管理工具大多使用命令行方式
* 多台机器可以同时操作，对于DBA来说非常有效率

### 使用图形客户端连接MySQL

* 常用的图形客户端工具
  * Navicat
  * MySQLWorkBench

### 图形GUI工具的优势

* 操作简单易于上手
* 支持图形化的导入、导出
* 可视化界面输出，输出可视化

### 总结

* 应用程序需要使用API接口连接MySQL
* 开发工程师可以使用图形工具连接MySQL
* 命令行客户端才是DBA的最爱
