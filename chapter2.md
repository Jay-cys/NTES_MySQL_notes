# MySQL数据库对象与应用

## 2.1-MySQL数据类型

### Number不止一种

* 整形
* 浮点型

### 整形

* INT
* SMALLINT
* MEDIUMINT
* BIGINT

| type | Storage | Minumun Value | Maximum Value|
| :------------- | :------------- | :------------- | :------------- |
||(Bytes)|(Signed/Unsigned)|(Signed/Unsigned)|
|TINYINT|1|-128|127|
|||0|255|
|SMALLINT|2|-32768|32767|
|||0|65535|
|MEDIUMINT|3|-8388608|8388607|
|||0|16777215|
|INT|4|-2147483648|2147483647|
|||0|4294967295|
|BIGINT|8|-9223372036854775808|9223372036854775807|
|||0|18446744073709551615|

### 老生常谈的问题

**int(11) VS int(21)**
存储空间，还是存储范围有区别？

答案是：**两者完全一样**，只是在显示的时候补全0的位数不一样。

可以通过下面的例子来验证：

```sql
create table t(a int(11) zerofill, b int(21) zerofill);
insert into t values (1, 1);
select * from t;
```

MySQL默认是不带0补全的。

只是在一些特殊情况下两者显示有区别，其本质完全一样。

### 浮点型

* FLOAT(M, D)
* DOUBLE(M, D)

| 属性 | 存储空间 | 精度 | 精确性 |
| :------------- | :------------- | :------------- | :------------- |
|Float|4 bytes|单精度|非精确|
|Double|8 bytes|双精度|比Float精度高|

### 精度丢失问题

* 精度丢失

一个例子：
```sql
create table t(a int(11), b float(7, 4));
insert into t values (2, 123.12345);
select * from t;
```

### 定点数-更精确的数字类型

* DECIMAL
  * 高精度的数据类型，常用来存储交易相关的数据
  * DECIMAL(M,N).M代表总精度，N代表小数点右侧的位数（标度）
  * 1 < M < 254, 0 < N < 60;
  * 存储空间变长

### 性别、省份信息

一般使用tinyint、char(1)、enum类型。

### 经验之谈

* 存储性别、省份、类型等分类信息时选择TINYINT或者ENUM
* BIGINT存储空间更大，INT和BIGINT之间通常选择BIGINT
* 交易等高精度数据选择使用DECIMAL

### 存储用户名的属性

* CHAR
* VARCHAR
* TEXT

### CAHR与VARCHAR

* CHAR和VARCHAR存储的单位都是字符
* CHAR存储定长，容易造成空间的浪费
* VARCHAR存储变长，节省存储空间

### 字符与字节的区别

|编码\输入字符串   | 网易            | netease        |
| :------------- | :------------- | :------------- |
| gbk(双字节)     | varchar(2)/4 bytes|varchar(7)/7 bytes|
| utf8(三字节)     | varchar(2)/6 bytes|varchar(7)/7 bytes|
| utf8mb4(四字节)     | varchar(2) ?|varchar(7)/7 bytes|

对于utf8mb4号称占用四字节但是并不绝对。如果在utf8可以覆盖到的范围则仍然占用3字节。

utf8mb4最有优势的应用场景是用于存储emoji表情

### emoji表情

* MySQL版本 > 5.5.3
* JDBC驱动版本 > 5.1.13
* 库和表的编码设为utf8mb4

### TEXT与CHAR和VARCHAR的区别

* CHAR和VARCHAR存储单位为字符
* TEXT存储单位为字节，总大小为65535字节，约为64KB
* CHAR数据类型最大为255字符
* VARCHAR数据类型为变长存储，可以超过255个字符
* TEXT在MySQL内部大多存储格式为溢出页，效率不如CHAR

一个例子：

```sql
create table t (a char(256));
create table t (a varchar(256));
```

### 存储头像

* BLOB
* BINARY

性能太差，不推荐

### 经验之谈

* CHAR与VARCHAR定义的长度是字符长度不是字节长度
* 存储字符串推荐使用VARCHAR(N),N尽量小
* 虽然数据库可以存储二进制数据，但是性能低下，不要使用数据库存储文件音频等二进制数据

### 存储生日信息

* DATE
* TIME
* DATETIME
* TIMESTAMP
* BIGINT

### 时间类型的区别在哪里

* 存储空间上的区别
  * DATE三字节，如：2015-05-01
  * TIME三字节，如：11:12:00
  * TIMESTAMP，如：2015-05-01 11::12:00
  * DATETIME八字节，如：2015-05-01 11::12:00

* 存储精度的区别
  * DATE精确到年月日
  * TIME精确到小时分钟和秒
  * TIMESTAMP、DATETIME都包含上述两者

### TIMESTAMP VS DATETIME

* 存储范围的区别
  * TIMESTAMP存储范围：1970-01-01 00::00:01 to 2038-01-19 03:14:07
  * DATETIME的存储范围：1000-01-01 00:00:00 to 9999-12-31 23:59:59

MySQL在5.6.4版本之后，TimeStamp和DateTime支持到微妙

* 字段类型与市区的关联关系
  * TIMESTAMP会根据系统时区进行转换，DATETIME则不会

### 字段类型和时区的关系

* 国际化的系统

一个例子：
```sql
create table test (a datetime, b timestamp);
select now();
insert into test values (now(), now());
select * from test;
set time_zone = '+00:00';
select * from test;
```

### BIGINT如何存储时间类型

* 应用程序将时间转换为数字类型


## 2.2-MySQL数据对象

### MySQL常见的数据对象有哪些

* DataBase/Schema
* Table
* Index
* View/Trigger/Function/Procedure

### 库、表、行层级关系

* 一个DataBase对应一个Schema
* 一个Schema包含一个或多个表
* 一个表里面包含一个或多个字段
* 一个表里包含一条或多条记录
* 一个表包含一个或多个索引

### 多DataBase用途

* 业务隔离
* 资源隔离

### 表上有哪些常用的数据对象

* 索引
* 约束
* 视图、触发器、函数、存储过程

### 什么是数据库索引

* 读书的时候如何快速定位某一章节
  * 查找书籍目录
  * 在自己喜欢的章节加书签，直接定位
* 索引就是数据库中的数据的目录（索引和数据是分开存储的）
  * 索引和数据是两个对象
  * 索引主要是用来提高数据库的查询效率
  * 数据库中数据变更同样需要同步索引数据的变更

## 如何创建索引（一）

```sql
CREATE [UNIQUE|FULLTEXT|SPATIAL] INDEX index_name
  [index_type]
  ON tbl_name (index_col_name,...)
  [index_option]
  [algorithm_option | lock_option] ...

index_col_name:
  col_name [(length)] [ASC | DESC]

index_type:
  USING {BTREE | HASH}
```

## 如何创建索引（二）

```sql
ALTER [IGNORE] TABLE tbl_name
  [alter_specification [, alter_specification] ...]
  [partition_options]

alter_specification:
    table_options
  | ADD [COLUMN] col_name column_definition
        [FIRST | AFTER col_name]
    ADD [COLUMN] (col_name column_definition,...)
    ADD {INDEX|KEY} [index_name]
        [index_type] (index_col_name,...) [index_option] ...
  | ADD [CONSTRAINT [symbol]] PRIMARY KEY
        [index_type] (index_col_name,...) [index_option] ...
  | ADD [CONSTRAINT [symbol]]
        UNIQUE [INDEX|KEY] [index_name]
```

### 约束

* 生活中的约束有哪些
  * 每个人的指纹信息必须唯一
  * 每个人的身份证要求唯一
  * 网上购物需要先登录才能下单
* 唯一约束
  * 对一张表的某个字段或者某几个字段设置唯一键约束，保证在这个表里对应的数据必须唯一，如：用户ID、手机号、身份证等。

### 创建唯一约束

* 唯一约束是一种特殊的索引
* 唯一约束可以是一个或者多个字段
* 唯一约束可以在创建表的时候建好，也可以后面再补上
* 主键也是一种唯一约束

### 唯一约束

以如下这张表为例

```sql
CREATE TABLE `order` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `orderid` int(10) unsigned NOT NULL,
  `bookid` int(10) unsigned NOT NULL DEFAULT '0',
  `userid` int(10) unsigned NOT NULL DEFAULT '0',
  `number` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `address` varchar(128) NOT NULL DEFAULT '',
  `postcode` varchar(128) NOT NULL DEFAULT '',
  `orderdate` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `status` tinyint(3) unsigned zerofill DEFAULT '000',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_orderid` (`orderid`),
  UNIQUE KEY `idx_uid_orderid` (`userid`, `orderid`),
  KEY `bookid` (`bookid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8
```

* 索引有哪些
  * 主键索引 ID
  * 单键索引 orderid
  * 单键索引 bookid
  * 组合索引 (userid + orderid)
* 唯一约束有哪些
  * 主键约束      (ID)
  * 单键唯一索引   (orderid)
  * 组合唯一索引   (userid + orderid)

### 添加唯一约束

* 添加主键
  * alter table \`order\` add primary key (id);
* 添加唯一索引
  * alter table \`order\` add unique key idx_uk_orderid (orderid);

### 外键约束
  * 外键指两张表的数据通过某种条件关联起来

### 创建外键约束

* 将用户表和订单表通过外键关联起来
  * alter table \`order\` add CONSTRAINT constraint_uid FOREIGN KEY (userid) REFERENCES user(userid);
* 使用外键的注意事项
  * 必须是INNODB表，Myisam和其他引擎不支持外键
  * 相互约束的字段类型必须要求一样
  * 主表的约束字段要求有索引
  * 约束名称必须要唯一，即使不在一张表上

### View

* 产品需求
  * 假如有其他部门的同事想查询我们数据库里的数据，但是我们并不想暴露表结构，并且只提供给他们部分数据

### View的作用

* 视图将一组查询语句构成的结果集，是一种虚拟结构，并不是实际数据
* 视图能简化数据库的访问，能够将多个查询语句结构化为一个虚拟结构
* 视图可以隐藏数据库后端表结构，提高数据库安全性
* 视图也是一种权限管理，只对用户提供部分数据

### 创建View

* 创建已完成订单的视图
  * create view order_view as select * from \`order\` where status=1;

### Trigger

* 产品需求
  * 随着客户个人等级的提升， 系统需要自动更新用户的积分，其中一共有两张表，分别为：用户信息表和积分表
* Trigger俗称触发器，指可以在数据写入表A之前或者之后可以做一些其他动作
* 使用Trigger在每次更新用户表的时候出发更新积分表

### 除此之外还有哪些

* Function
* Procedure


## 2.3-MySQL权限管理

### 连接MySQL的必要条件

* 网络要通畅
* 用户名和密码要正确
* 数据库需要加IP白名单
* 更细粒度的验证（库、表、列权限类型等等）

### 数据有哪些权限

`show privileges`命令可以查看全部权限

### 权限粒度

* Data Privileges
  * DATA: SELECT, INSERT, UPDATE, DELETE
* Definition Privileges
  * DataBase: CREATE, ALTER, DROP
  * Table: CREATE, ALTER, DROP
  * VIEW/FUNCTION/TRIGGER/PROCEDURE: CREATE, ALTER, DROP
* Administrator Privileges
  * Shutdown DataBase
  * Replication Slave
  * Replication Client
  * File Privilege

### MySQL赋权操作

```sql
GRANT
  priv_type [(column_list)]
    [, priv_type [column_list]] ...
  ON [object_type] priv_level
  TO user_specification [, user_specification] ...
  [REQUIRE {NONE | ssl_option [[AND] ssl_option] ...}]
  [WITH with_option ...]
GRANT PROXY ON user_specification
  TO user_specification [, user_specification] ...
  [WITH GRANT OPTION]
```

### 如何新建一个用户并赋权

* 使用MySQL自带的命令
  * `CREATE USER 'netease'@'localhost' IDENTIFIED BY 'netease163';`
  * `GRANT SELECT ON *.* TO 'netease'@'localhost' WITH GRANT OPTION;`

### 其他方法

* 更改数据库记录
  * 首先向User表里面插入一条记录，根据自己的需要选择是否向db和table_pirv表插入记录
  * 执行`flush privileges`命令，让权限信息生效

### 更简单的办法

* GRANT语句会判断是否存在该用户，如果不存在则新建
  * `GRANT SELECT ON *.* TO 'NETEASE'@'localhost' IDENTIFIED BY 'netease163' WITH GRANT OPTION;`

### 查看用户的权限信息

* 查看当前用户的权限
  * `show grants;`
* 查看其它用户的权限
  * `show grants for netease@'localhost';`

### 如何更改用户的权限

* 回收不需要的权限
  * `revoke select on *.* from netease@'localhost';`
* 重新赋权
  * `grant insert on *.* to netease@'localhost';`

### 如何更改用户密码

* 用新密码，grant语句重新授权
* 更改数据库记录，Update User表的Password字段
  * 注意：用这种办法，更改完需要flush privileges刷新权限信息，不推荐

### 删除用户

```sql
DROP USER user [, user] ...
```

### With Grant Option

* 允许被授予权利的人把这个权利授予其他的人

### MySQL权限信息存储结构

* MySQL权限信息是存在数据库表中
* MySQL账号对应的密码也加密存储在数据库表中
* 每一种权限类型在元数据里都是枚举类型，表明是否有该权限

### 有哪些权限相关的表

* user
* db
* table_pirv
* columns_pirv

### 权限验证流程

查询时从user->db->table_pirv->columns_pirv依次验证，如果通过则执行查询。

### 小结

* MySQL权限信息都是以数据记录的形式存储在数据库的表中。
* MySQL的权限验证相比网站登录多了白名单环节，并且粒度更细，可以精确到表和字段。

### MySQL权限上有哪些问题

* 使用Binary二进制安装管理用户没有设置密码
* MySQL默认的test库不受权限控制，存在安全风险

### mysql_secure_installation

* You can set a Password for root accounts.
* You can remove root accounts that are accessible from outside the localhost.
* You can remove anonymous-user accounts.
* You can remove the test database.

### 小结

* 权限相关的操作不要直接操作表，统一使用MySQL命令。
* 使用二进制安装MySQL安装后，需要重置管理用户(root)的密码。
* 线上数据库不要留test库
