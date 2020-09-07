# do1cloud-phoenix-dbutil

轻量化 phoenix-orm 客户端，基于 Apache commons-dbutils、原生 phoenix-client。



- [组件说明](#组件说明)
- [环境要求](#环境要求)
- [依赖引入](#依赖引入)
- [快速使用](#快速使用)
    - [select 查询操作](#select-查询操作)
    - [upsert 新增/更新操作](#upsert-新增更新操作)
    - [delete 删除操作 ](#delete-删除操作-)
- [Phoenix JDBC 连接设置](#phoenix-jdbc-连接设置)
- [Phoenix 字段与 Java Bean 属性映射](#phoenix-字段与-java-bean-属性映射)

<br>

### 组件说明

* 轻量化 phoenix-orm 客户端，基于 Apache commons-dbutils、原生 phoenix-client。
* 不依赖 Spring、MyBtais、Hibernate 等框架生态，提供 phoenix-orm 功能的最小依赖客户端工具，可以作为原生 pheonix-client 代替品（目前版本仅支持 Phoenix Fat Client）。
* 适合直接嵌入到 Flink Job 等对包体依赖要求较为苛刻的场景使用。
* 可以通过替换 pom 中的 phoenix-client 版本兼容多个版本的 phoenix-client。

### 环境要求

* JDK 1.8 + 
* Phoenix：5.0.0-hbase-2.0

<br>

### 依赖引入

待定。

<br>

### 快速使用

完整使用可以参考 site.assad.phoenix.dbutil

以下示例实体 Record 结构：

```java
public class Record {
    private String uid;           // phoenix id
    private Timestamp punchDate;  // phoenix id
    private Integer amount;
    private String username;
		// getter and setter 
  	...
}
```

<br>

#### select 查询操作

```java
// 获取 Phoenix 执行器
PhxQueryRunner queryRunner = PhoenixDbUtil.queryRunner("jdbc:phoenix:do1cloud01,do1cloud02,do1cloud03");

// 查询单条记录
Optional<Record> record = queryRunner
                .select("select * from test.record where uid=? and punch_date=?") // 指定sql，占位符为 ？
                .param("12345", toSqlTimestamp("2020-08-20 12:00:00"))            // 指定占位符填充参数
                .queryOne(Record.class);       																		// 指定查询结果映射类型

// 查询多条纪录
List<Record> records = queryRunner
                .select("select * from test.record where uid=?")    // 指定sql，占位符为 ？
                .param("12345")                  										// 指定占位符填充参数
                .queryList(Record.class);														// 指定查询结果映射类型

// select in 操作，使用 # 作为占位符，且对应参数必须是 Collection 类型
List<Record> records = queryRunner
                .select("select * from test.record where uid in (#) and punch_date=?")    
                .param(Lists.newArrayList("12345", "23333"), toSqlTimestamp("2020-08-20 12:00:00"))                  
                .queryList(Record.class);														
```

<br>

#### upsert 新增/更新操作

**1）自动 ORM Upsert 记录**

```java
// 保存单条记录
Record bean = ...
queryRunner.ormUpsert().save(bean, "test.record");       // 参数：实体示例，实体表名
// 保存多条记录
List<Record> beans = ...
queryRunner.ormUpsert().saveAll(recordList, "test.record"); // 参数：实体示例，实体表名
```

默认情况下，`save()` 会持久化 bean 实例的全部属性，包括空值属性，可以设置 `overrideNullProps = false` 不持久化空值属性（一般为更新场景下使用）

```java
queryRunner.ormUpsert().save(bean, "test.record", false);  
queryRunner.ormUpsert().saveAll(recordList, "test.record", false);
```

**2）手动拼接 Upsert 语句**

除了 orm 自动拼接 upsert ，可以选择手动编写 upsert 更新语句。

```java
// upsert 单条记录
queryRunner.upsert("upsert into test.record(uid, punch_date, username, amount) values(?,?,?,?)")
              .param("23333", toSqlTimestamp("2020-08-20 08:12:13"), "Benson", 13)
              .update();
// 批量 upsert 记录
queryRunner.batchUpdate("upsert into test.record(uid, punch_date, username, amount) values(?,?,?,?)")
                .putParam("23333", toSqlTimestamp("2020-08-20 08:13:15"), "Benson", 20)
                .putParam("23333", toSqlTimestamp("2020-08-20 09:34:00"), "Benson", 500)
                .putParam("23333", toSqlTimestamp("2020-08-20 10:22:00"), "Benson", 45)
                .putParam("53333", toSqlTimestamp("2020-08-20 12:12:12"), "Darcy", 999)
                .update();
```

 <br>

#### delete 删除操作 

```java
queryRunner.delete("delete from test.record where uid=? and punch_date=?")
              .param("12345", toSqlTimestamp("2020-08-20 12:00:00"))
              .update();
// delete in 操作
queryRunner.delete("delete from test.record where uid in (#)")
              .param(Lists.newArrayList("12345", "23333"))
              .update();							
```

<br>

### Phoenix JDBC 连接设置

建议对同一个 Phoenix 连接配置的 `PhxQueryRunner` 实例公用，在 Spring 环境下建议在初始化阶段将该对象注册为 Bean。

可以通过以下方法创建指定 Phoenix 连接配置的执行器：

```java
// 指定 phoenix jdbcUrl 配置
PhoenixDbUtil#queryRunner(String jdbcUrl)
// 指定 phoenix jdbcUrl配置、额外 jdbc 连接配置
PhxQueryRunner queryRunner(String jdbcUrl, Properties connectProps)
```

`PhxQueryRunner`  实例支持动态更换 Phoenix 连接，这是基于 Phoenix Fat Client Connection 十分轻量实现的，提供更佳灵活的业务使用场景。

<br>

### Phoenix 字段与 Java Bean 属性映射

Pheonix 会自动将所有 SQL 语句转换大写，包括表名、所有字段名称。

do1cloud-phoenix-dbutil 支持以下关系的字段名称自动映射：

* Java Bean：驼峰风格命名；
* Phoenix Property：下划线风格，全大写；

如下示例 Java Bean：

```java
public class Record {
    private String uid;           
    private Timestamp punchDate;  
    private Integer amount;
    private String username;
}
```

与以下 Phoenix 字段自动映射：

```sql
CREATE TABLE TEST.RECORD
(
    UID        VARCHAR(5) NOT NULL,
    PUNCH_DATE TIMESTAMP  NOT NULL,
    AMOUNT     INTEGER,
    USERNAME   VARCHAR,
    CONSTRAINT ROWKEY PRIMARY KEY (UID, PUNCH_DATE)
);
```

