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

## 5.2-MySQL数据备份

### 基本指数 - 备份用途

* 数据备灾
  * 应对硬件故障数据丢失
  * 应对人为或程序bug导致数据删除
* 制作镜像库以供服务
  * 需要将数据迁移、统计分析等用处
  * 需要为线上数据建立一个镜像

### 基本知识 - 备份内容

* 数据
  * 数据文件或文本格式数据
* 操作日志(binlog)
  * 数据库变更日志

### 基本知识 - 冷备份与热备份

* 冷备份
  * 关闭数据库服务，完整拷贝数据文件
* 热备份
  * 在不影响数据库读写服务的情况下备份数据库

### 基本知识 - 物理备份与逻辑备份

* 物理备份
  * 以数据页的形式拷贝数据
* 逻辑备份
  * 导出为裸数据或者SQL(insert)语句

### 基本知识 - 本地备份与远程备份

* 本地备份
  * 在数据库服务器本地进行备份
* 远程备份
  * 远程连接数据库进行备份

### 基本知识 - 全量备份与增量备份

* 全量备份
  * 备份完整的数据库
* 增量备份
  * 只备份上一次备份以来发生修改的数据

### 基本知识 - 备份周期

考虑因素：
  * 数据库大小(决定备份时间)
  * 恢复速度要求(快速or慢速)
  * 备份方式(全量or增量)

### 常用工具及用法

* mysqldump - 逻辑备份，热备
* xtrabackup - 物理备份， 热备
* Lvm/zfs snapshot - 物理备份
* mydumper - 逻辑备份，热备
* cp - 物理备份，冷备

### 常用工具及用法 - mysqldump

MySQL官方自带的命令行工具

主要示例：

* 演示使用mysqldump备份表、库、实例

```bash
# 备份所有数据库
mysqldump -uroot -p123456 --socket=/var/run/mysqld/mysqld.sock --all-databases > /dbbackup/all_db.sql
# 备份指定的数据库
mysqldump -uroot -p123456 --socket=/var/run/mysqld/mysqld.sock --databases db2 > /dbbackup/db2.sql
# 备份单个表
mysqldump -uroot -p123456 --socket=/var/run/mysqld/mysqld.sock db2 t1 >/dbbackup/db2_t1.sql
# 还原表
mysql > source /dbbackup/db2_t1.sql
```

* 演示使用mysqldump制作一致性备份

```bash
mysqldump --single-transaction -uroot -p123456 --all-databases > /dbbackup/add_db_2.sql
```

* 演示使用mysqldump远程备份一个数据库

```bash
mysqldump -utest -ptest -h192.168.0.68 -P3306 --all-databases > /dbbackup/remote_bakall.sql
```

* 演示使用mysqldump导出数据为csv格式

```bash
mysqldump -uroot -p123456 --single-transaction --fields-terminated-by=, db1 -T /tmp
```

### 常用工具及用法 - xtrabackup

特点：
  * 开源，在线备份InnoDB表
  * 支持限速备份，避免对业务造成影响
  * 支持流备
  * 支持增量备份
  * 支持备份文件压缩与加密
  * 支持并行备份与恢复，速度快

### xtrabackup备份原理

* 基于InnoDB的crash-recovery功能
* 备份期间允许用户读写，写请求产生redo日志
* 从磁盘上拷贝数据文件
* 从InnoDB redo log file实时拷贝走备份期间产生的所有redo日志
* 恢复的时候 数据文件 + redo日志 = 一致性数据

### 实用脚本innobackupex

* 开源Perl脚本，封装调用xtrabackup及一系列相关工具与OS操作，最终完成备份过程
* 支持备份InnoDB和其他引擎的表
* 备份一致性保证

### innobackupex备份基本流程

start xtrabackup_log -> copy .ibd; ibdata1 -> FLUSH TABLE WITH READ LOCK -> copy .FRM; MYD; MYI; misc files -> Get binary log position -> UNLOCK TABLES -> stop and copy xtrabackup_log

### innobackupex使用

主要示例：

* 全量备份

```bash
innobackupex --user=root --password=123456 --defaults-file=/etc/mysql/my.cnf /dbbackup
```

* 增量备份

```bash
innobackupex --user=root --password=123456 --defaults-file=/etc/mysql/my.cnf --incremental --incremental-dir /dbbackup/2016-4-3_13:24:32 /dbbackup
```

* 流方式备份

```bash
innobackupex --user=root --password=123456 --defaults-file=/etc/mysql/my.cnf --stream=xbstream /dbbackup/ > /dbbackup/stream.bak
```

* 并行备份

```bash
innobackupex --user=root --password=123456 --defaults-file=/etc/mysql/my.cnf --parallel=4 /dbbackup/
```

* 限流备份

```bash
innobackupex --user=root --password=123456 --defaults-file=/etc/mysql/my.cnf --throttle=10 /dbbackup/
```

* 压缩备份

```bash
innobackupex --user=root --password=123456 --defaults-file=/etc/mysql/my.cnf --compress --compress-thread 4 /dbbackup/
```

### 如何制定备份策略

需要考虑的因素

* 数据库是不是都是innodb引擎表 -> 备份方式，热备or冷备
* 数据量大小 -> 逻辑备份or物理备份，全量or增量
* 数据库本地磁盘空间十分充足 -> 备份到本地or远程
* 需要多块恢复 -> 备份频率 小时or天
