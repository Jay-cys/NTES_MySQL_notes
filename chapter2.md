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


## 2.4-SQL语言进阶

本课程涉及建表SQL

```SQL
-- ----------------------------
-- Table structure for `play_fav`
-- ----------------------------
DROP TABLE IF EXISTS `play_fav`;
CREATE TABLE `play_fav` (
  `userid` bigint(20) NOT NULL COMMENT '收藏用户id',
  `play_id` bigint(20) NOT NULL COMMENT '歌单id',
  `createtime` bigint(20) NOT NULL COMMENT '收藏时间',
  `status` int(11) DEFAULT '0' COMMENT '状态，是否删除',
  PRIMARY KEY (`play_id`,`userid`),
  KEY `IDX_USERID` (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='歌单收藏表';

-- ----------------------------
-- Records of play_fav
-- ----------------------------
INSERT INTO play_fav VALUES ('2', '0', '0', '0');
INSERT INTO play_fav VALUES ('116', '1', '1430223383', '0');
INSERT INTO play_fav VALUES ('143', '1', '0', '0');
INSERT INTO play_fav VALUES ('165', '2', '0', '0');
INSERT INTO play_fav VALUES ('170', '3', '0', '0');
INSERT INTO play_fav VALUES ('185', '3', '0', '0');
INSERT INTO play_fav VALUES ('170', '4', '0', '0');
INSERT INTO play_fav VALUES ('170', '5', '0', '0');

-- ----------------------------
-- Table structure for `play_list`
-- ----------------------------
DROP TABLE IF EXISTS `play_list`;
CREATE TABLE `play_list` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `play_name` varchar(255) DEFAULT NULL COMMENT '歌单名字',
  `userid` bigint(20) NOT NULL COMMENT '歌单作者账号id',
  `createtime` bigint(20) DEFAULT '0' COMMENT '歌单创建时间',
  `updatetime` bigint(20) DEFAULT '0' COMMENT '歌单更新时间',
  `bookedcount` bigint(20) DEFAULT '0' COMMENT '歌单订阅人数',
  `trackcount` int(11) DEFAULT '0' COMMENT '歌曲的数量',
  `status` int(11) DEFAULT '0' COMMENT '状态,是否删除',
  PRIMARY KEY (`id`),
  KEY `IDX_CreateTime` (`createtime`),
  KEY `IDX_UID_CTIME` (`userid`,`createtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='歌单';

-- ----------------------------
-- Records of play_list
-- ----------------------------
INSERT INTO play_list VALUES ('1', '老男孩', '1', '1430223383', '1430223383', '5', '6', '0');
INSERT INTO play_list VALUES ('2', '情歌王子', '3', '1430223384', '1430223384', '7', '3', '0');
INSERT INTO play_list VALUES ('3', '每日歌曲推荐', '5', '1430223385', '1430223385', '2', '4', '0');
INSERT INTO play_list VALUES ('4', '山河水', '2', '1430223386', '1430223386', '5', null, '0');
INSERT INTO play_list VALUES ('5', '李荣浩', '1', '1430223387', '1430223387', '1', '10', '0');
INSERT INTO play_list VALUES ('6', '情深深', '5', '1430223388', '1430223389', '0', '0', '1');

-- ----------------------------
-- Table structure for `song_list`
-- ----------------------------
DROP TABLE IF EXISTS `song_list`;
CREATE TABLE `song_list` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `song_name` varchar(255) NOT NULL COMMENT '歌曲名',
  `artist` varchar(255) NOT NULL COMMENT '艺术节',
  `createtime` bigint(20) DEFAULT '0' COMMENT '歌曲创建时间',
  `updatetime` bigint(20) DEFAULT '0' COMMENT '歌曲更新时间',
  `album` varchar(255) DEFAULT NULL COMMENT '专辑',
  `playcount` int(11) DEFAULT '0' COMMENT '点播次数',
  `status` int(11) DEFAULT '0' COMMENT '状态,是否删除',
  PRIMARY KEY (`id`),
  KEY `IDX_artist` (`artist`),
  KEY `IDX_album` (`album`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='歌曲列表';

-- ----------------------------
-- Records of song_list
-- ----------------------------
INSERT INTO song_list VALUES ('1', 'Good Lovin\' Gone Bad', 'Bad Company', '0', '0', 'Straight Shooter', '453', '0');
INSERT INTO song_list VALUES ('2', 'Weep No More', 'Bad Company', '0', '0', 'Straight Shooter', '280', '0');
INSERT INTO song_list VALUES ('3', 'Shooting Star', 'Bad Company', '0', '0', 'Straight Shooter', '530', '0');
INSERT INTO song_list VALUES ('4', '大象', '李志', '0', '0', '1701', '560', '0');
INSERT INTO song_list VALUES ('5', '定西', '李志', '0', '0', '1701', '1023', '0');
INSERT INTO song_list VALUES ('6', '红雪莲', '洪启', '0', '0', '红雪莲', '220', '0');
INSERT INTO song_list VALUES ('7', '风柜来的人', '李宗盛', '0', '0', '作品李宗盛', '566', '0');

-- ----------------------------
-- Table structure for `stu`
-- ----------------------------
DROP TABLE IF EXISTS `stu`;
CREATE TABLE `stu` (
  `id` int(10) NOT NULL DEFAULT '0',
  `name` varchar(20) DEFAULT NULL,
  `age` int(10) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of stu
-- ----------------------------

-- ----------------------------
-- Table structure for `tbl_proc_test`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_proc_test`;
CREATE TABLE `tbl_proc_test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `num` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tbl_proc_test
-- ----------------------------
INSERT INTO tbl_proc_test VALUES ('11', '1');
INSERT INTO tbl_proc_test VALUES ('12', '2');
INSERT INTO tbl_proc_test VALUES ('13', '6');
INSERT INTO tbl_proc_test VALUES ('14', '24');
INSERT INTO tbl_proc_test VALUES ('15', '120');
INSERT INTO tbl_proc_test VALUES ('16', '720');
INSERT INTO tbl_proc_test VALUES ('17', '5040');
INSERT INTO tbl_proc_test VALUES ('18', '40320');
INSERT INTO tbl_proc_test VALUES ('19', '362880');
INSERT INTO tbl_proc_test VALUES ('20', '3628800');
INSERT INTO tbl_proc_test VALUES ('21', '1');
INSERT INTO tbl_proc_test VALUES ('22', '2');
INSERT INTO tbl_proc_test VALUES ('23', '6');
INSERT INTO tbl_proc_test VALUES ('24', '24');
INSERT INTO tbl_proc_test VALUES ('25', '1');
INSERT INTO tbl_proc_test VALUES ('26', '2');
INSERT INTO tbl_proc_test VALUES ('27', '6');
INSERT INTO tbl_proc_test VALUES ('28', '24');
INSERT INTO tbl_proc_test VALUES ('29', '120');

-- ----------------------------
-- Table structure for `tbl_test1`
-- ----------------------------
DROP TABLE IF EXISTS `tbl_test1`;
CREATE TABLE `tbl_test1` (
  `user` varchar(255) NOT NULL COMMENT '主键',
  `key` varchar(255) NOT NULL,
  `value` varchar(255) NOT NULL,
  PRIMARY KEY (`user`,`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='行列转换测试';

-- ----------------------------
-- Records of tbl_test1
-- ----------------------------
INSERT INTO tbl_test1 VALUES ('li', 'age', '18');
INSERT INTO tbl_test1 VALUES ('li', 'dep', '2');
INSERT INTO tbl_test1 VALUES ('li', 'sex', 'male');
INSERT INTO tbl_test1 VALUES ('sun', 'age', '44');
INSERT INTO tbl_test1 VALUES ('sun', 'dep', '3');
INSERT INTO tbl_test1 VALUES ('sun', 'sex', 'female');
INSERT INTO tbl_test1 VALUES ('wang', 'age', '20');
INSERT INTO tbl_test1 VALUES ('wang', 'dep', '3');
INSERT INTO tbl_test1 VALUES ('wang', 'sex', 'male');

-- ----------------------------
-- Procedure structure for `proc_test1`
-- ----------------------------
DROP PROCEDURE IF EXISTS `proc_test1`;
DELIMITER ;;
CREATE DEFINER=`root` PROCEDURE `proc_test1`(IN total INT,OUT res INT)
BEGIN   
    DECLARE i INT;  
    SET i = 1;
    SET res = 1;
    IF total <= 0 THEN   
        SET total = 1;   
    END IF;   
    WHILE i <= total DO
        SET res = res * i;
        INSERT INTO tbl_proc_test(num) VALUES (res);  
        SET i = i + 1;
    END WHILE;
END
;;
DELIMITER ;
```

### 说明

* 本课程介绍以MySQL SQL语法为基础，不同数据库SQL语法存在差异，并未完全遵照ANSI标准。
* 本课程结合一个实际项目（云音乐），介绍各种SQL语言在实际应用中如何实现业务功能。

### SQL进阶语法——order by

场景1：歌单按时间排序

```sql
-- 查看全部歌单
select * from play_list;

-- 按创建时间排序
select * from play_list order by createtime;
-- MySQL默认升序，如果按降序排列，则使用如下语句。
select * from play_list order by createtime desc;
-- 也可以按照多个字段来排序
select * from play_list order by bookedcount, trackcount;
```

### SQL进阶语法——distinct

场景2：统计云音乐创建歌单的用户

```sql
-- 有重复
select userid from play_list;

-- 去重
select distinct userid from play_list;

-- 多个字段
select distinct userid, play_name from play_list;
```

* distinct用于返回唯一不同的值
* 可以返回多列的唯一组合
* 底层实现使用排序，如果数据量大会消耗较多的IO和CPU

### SQL进阶语法——group by

场景3-1：统计云音乐创建歌单的用户列表和每人创建歌单的数量。

```sql
-- 每个用户歌单的最大订阅数
select userid, max(bookedcount) from play_list group by userid;

-- 每个用户歌单的数量
select userid, count(*) from play_list group by userid;
```

* group by 根据单列或多列对数据进行分组，通常结合聚合函数使用，如count(\*).

### SQL进阶语法——group by having

场景3-2：统计云音乐创建歌单的用户列表和每人创建歌单的数量，并且只显示歌单数量排序大于等于2的用户

```sql
select userid, count(*) from play_list group by userid having count(*) >= 2;
```

* having 是对结果进行过滤

SQL进阶语法-like

```sql
select * from play_list where play_name like '%男孩%';
```

| 通配符 | 描述 |
| :------------- | :------------- |
| % | 代替一个或多个字符 |
| _ | 代替单个字符      |
| [charlist] | 中括号中的任何一个字符 |
| [^charlist] 或者 [!charlist] | 不在中括号中的任何单一字符 |

* 除了百分号在最右面的情况以外，他会对这个表中所有的记录进行一次查询匹配，而没办法使用索引，效率较低。大表中需要慎用like。可以使用全文检索的手段。

### SQL进阶语法-limit, offset

场景4：查询一个月内创建歌单（从第6行开始显示10条记录）

```sql
select * from play_list where (createtime between 1427791323 and 1430383307) limit 10 offset 6;
```

* offset后的值不建议太大，需要消耗的IO较大

### case when

* case when 实现类似编程语言的if else功能，可以对SQL的输出结果进行选择判断。

场景5：对于未录入歌曲的歌单(trackcount = null)，输出结果时歌曲数返回0.

```sql
select case when play_name, trackcount is null then 0 else trackcount end from play_list;
```

### select相关进阶语法

```sql
SELECT
  [DISTINCT]
  select_expr [, select_expr ...]
  [FROM table_references
  [WHERE where_condition]
  [GROUP BY {col_name | expr | position}
    [ASC | DESC], ... [WITH ROLLUP]]
  [HAVING where_condition]
  [ORDER BY {col_name | expr | position}
    [ASC | DESC], ...]
  [LIMIT { [offset, ] row_count | row_count OFFSET offset}]
    [FOR UPDATE | LOCK IN SHARE MODE]]
```

### 连接-Join

连接的作用是用一个SQL语句把多个表中相互关联的数据查出来

场景6：查询收藏“老男孩”歌单的用户列表

```sql
select * from play_list, play_fav where play_list.id=play_fav.play_id;
select play_fav.userid from play_list, play_fav where play_list.id=play_fav.play_id and play_list.play_name='老男孩';
-- 另一种写法
select f.userid from play_list lst join play_fav f on lst.id=f.play_id where lst.play_name = '老男孩';
```

### 子查询

* MySQL还有另一种写法，可以实现同样的功能。
```sql
select userid from play_fav where play_id=(select id from play_list where play_name = '老男孩');
```

子查询：内层查询的结果作为外层的比较条件。一般子查询都可以转换成连接，推荐使用连接。

* 不利于MySQL的查询优化器进行优化，可能存在性能问题
* 连接的实现是嵌套循环，选择一个驱动表，遍历驱动表，查询内层表，依次循环。驱动表会至少查询一边，如果有索引等，内层表可以非常快，查询优化器会选择数据小的表作为驱动表。
* 子查询由人为规定驱动表和内层表

### 连接- left Join

```sql
select lst.play_name from play_list lst left join play_fav f on lst.id = f.play_id where f.play_id is null;
```

* LEFT JOIN从左表(play_list)返回所有的行，即使在右表中(play_fav)中没有匹配的行。
* 与LEFT JOIN相对应的有RIGHT JOIN关键字，会从右表那里返回所有的行，即使在左表中没有匹配的行。

场景7：查询出没有用户收藏的歌单

### SQL进阶语法-union

场景8：老板想看创建和收藏歌单的所有用户，查询play_list和play_fav两表中所有的userid

```sql
select userid from play_list union select userid from play_fav;
-- 默认会去重， 不想去重的话使用union all代替union。
```

### DML进阶语法

* 多值插入： insert into table values(....),(....)
  * 可以一次插入多行数据，减少与数据库的交互提高效率
  * eg： `insert into A values(4, 33), (5, 33);`
* 覆盖插入： replace into table values (....)
  * 可以简化业务逻辑的判断
* 忽略插入： insert ignore into table value (....)
  * 可以简化业务逻辑的判断
* 查询插入： insert into table_a select \* from table_b
  * 常用于导表操作
* insert主键重复则update
  * `INSERT INTO TABLE tbl VALUES (id, col1, col2) ON DUPLICATE KEY UPDATE col2=....;`
  * eg: `insert into A values(2, 40) on duplicate key update age=40;`
  * 可以简化前端业务逻辑的判断
* 连表update
  * A表：id, age
  * B表：id, name, age
  * A表id与B表id关联，根据B表的age值更新A表的age。
  * eg: `update A,B set A.age=B.age where A.id=B.id;`
* 连表删除
  * A表：id, age
  * B表：id, name, age
  * A表id与B表id关联，根据B表的age值删除A表的数据。
  * eg: `delete A from A,B where A.id=B.id and B.name='pw';`

### 总结

* select查询进阶语法
  * order by/distinct/group by having (聚合函数) /like (%前缀后缀)
* 连接语法
  * 内连接、左连接、右连接、 Union [ALL]
* DML进阶语法
  * insert/连表update/连表delete
