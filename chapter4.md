# MySQL应用优化

## MySQL索引优化与设计

### 什么是索引

* 索引的意义 —— 快速定位要查找的数据

### 数据库索引查找

* 全表扫描 VS 索引查找

### 如何根据首字母找到所在行

* 二分查找
* B+tree

### InnoDB表聚簇索引

索引中只放着排序字段和ID

### 创建索引

* 单列索引

```sql
create index idx_test1 on tb_student (name);
```

* 联合索引

```sql
create index idx_test2 on tb_student (name, age);
```
  * 索引中先根据name排序，name相同的情况下，根据age排序

### 索引维护

* 索引维护由数据库自动完成
* 插入/修改/删除每一个索引行都会变成一个内部封装的事务
* 索引越多，事务越长，代价越高
* 索引越多对表的插入和索引字段修改就越慢
* 控制表上索引的数量，切忌胡乱添加无用索引

### 如何使用索引

* 依据WHERE查询条件建立索引

```sql
select a, b from tab_a where c=? ;
idx_c (c)
select a, b from tab_a where c=? and d=?;
idx_cd (c, d)
```

* 排序order by, group by, distinct字段添加索引

```sql
select * from tb_a order by a;
select a, count(*) from tb_a group by a;
idx_a (a)

select * from tb_a order by a, b;
idx_a_b (a, b)

select * from tb_a order where c=? by a;
idx_c_a (c, a)
```
