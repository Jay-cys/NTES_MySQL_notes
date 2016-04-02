# MySQL运维实践

## 5.1-MySQL日志系统

### 什么是日志

* 日志(log)是一种顺序记录事件流水的文件
* 记录计算机程序运行过程中发生了什么
* 多种多样的用途
  * 帮助分析程序问题
  * 分析服务请求的特征、流量等
  * 判断工作是否成功执行
  * 等等……

### MySQL日志的分类

* 服务器日志
  * 记录进程启动运行过程中的特殊事件，帮助分析MySQL服务遇到的问题
  * 根据需求抓取特定的SQL语句，追踪性能可能存在的问题的业务SQL
* 事务日志
  * 记录应用程序对数据的所有更改
  * 可用于数据恢复
  * 可用于实例间数据同步

| 分类 | 日志名称 |
| :------------- | :------------- |
| 服务器日志 | 服务错误日志 |
| 服务器日志 | 慢查询日志 |
| 服务器日志 | 综合查询日志 |
| 事务日志 | 存储引擎事务日志 |
| 事务日志 | 二进制日志 |

### 服务错误日志

* 记录实例启动运行过程中重要消息
* 配置参数
  * `log_error = /data/mysql_data/node-1/mysql.log`
* 内容并非全是错误消息
* 如果mysqld进程无法正常启动首先查看错误日志

### 慢查询日志

* 记录执行时间超过一定阈值的SQL语句
* 配置参数

```sql
slow_query_log = 1
slow_query_log_file = /data/mysql_data/node-1/mysql-slow.log
long_query_time = 5
```

* 用于分析系统中可能存在性能问题的SQL

### 综合查询日志

* 如果开启将会记录系统中所有SQL语句
* 配置参数

```sql
general_log = 1
general_log_file = /data/mysql_data/node-1/mysql-slow.log
```

* 偶尔用于帮助分析系统问题，对性能有影响

### 查询日志的输出与文件切换

* 日志输出参数

`log_output={file|table|none}`

* 如果日志文件过大，可以定期截断并切换新文件

`flush log;`

### 存储引擎事务日志

* 部分存储引擎拥有重做日志(redo log)
* 如InnoDB, TokuDB等WAL(Write Ahead Log)机制存储引擎
* 日志随着事务commit优先持久化，确保异常恢复不丢数据
* 日志顺序写性能较好

### InnoDB事务日志重用机制

* InnoDB事务日志采用两组文件交替重用

### 二进制日志binlog

* binlog (binary log)
* 记录数据引起数据变化的SQL语句或数据逻辑变化的内容
* MySQL服务层记录，无关存储引擎
* binlog的主要作用：
  * 基于备份恢复数据
  * 数据库主从同步
  * 挖掘分析SQL语句

### 开启binlog

* 主要参数

```
log_bin = c:/tmp/mylog/mysql-bin
sql_log_bin = 1
sync_binlog = 1
```

* 查看binlog

`show binary logs;`

### binlog管理

* 主要参数

```
max_binlog_size = 100MB
expire_logs_days = 7
```

* binlog始终生成新文件，不会重用

* 手工清理binlog

```
purge binary logs to 'mysql-bin.000009';
purge binary logs before '2016-4-2 21:00:40'
```

### 查看binlog内容

* 日志

```
show binlog events in 'mysql-bin.000011';
show binlog events in 'mysql-bin.000011' from 60 limit 3;
```

* mysqlbinlog工具

```
mysqlbinlog c:/tmp/mylog/mysql-bin.000001
--start-datetime | --stop-datetime
--start-position | --stop-position
```

### binlog格式

* 主要参数

`binlog_format = {ROW|STATEMENT|MIXED}`

* 查看row模式的binlog内容

`mysqlbinlog --base64-output=decode-rows -v c:/tmp/mylpg/mysql-bin.000001`
