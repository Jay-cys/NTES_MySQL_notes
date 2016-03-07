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
