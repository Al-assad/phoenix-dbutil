package site.assad.phoenix.dbutil;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static site.assad.phoenix.dbutil.helper.PhxDateHelper.toSqlTimestamp;

/**
 * @author yulinying
 * @since 2020/8/20
 */
@DisplayName("PhxQueryRunner 功能测试")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PhoenixDbUtilTest {

    private final static transient Logger log = LoggerFactory.getLogger(PhoenixDbUtilTest.class);
    private PhxQueryRunner queryRunner;

    @BeforeAll
    public void initJdbcUrl() throws SQLException {
        queryRunner = PhoenixDbUtil.queryRunner("jdbc:phoenix:hdp01,hdp02,hdp03");
    }


    @DisplayName("PhxSaveQuery 保存单个 bean")
    @Test
    public void testSave() throws ParseException, SQLException {
        // save bean
        Record bean = new Record();
        bean.setUid("12345");
        bean.setPunchDate(toSqlTimestamp("2020-08-20 11:00:00"));
        bean.setUsername("assad");
        bean.setAmount(120);
        int result = queryRunner.ormUpsert().save(bean, "test.record");
        Assertions.assertEquals(1, result);

        // get bean
        Record record = queryRunner.select("select * from test.record where uid=? and punch_date=?")
                .param("12345", toSqlTimestamp("2020-08-20 11:00:00"))
                .queryOne(Record.class)
                .orElse(null);
        log.info(() -> "select record=" + record);
        Assertions.assertEquals(bean, record);
    }

    @DisplayName("PhxSaveQuery 保存多个 bean")
    @Test
    public void testSaveAll() throws ParseException, SQLException {
        List<Record> recordList = Arrays.asList(
                new Record("12345", toSqlTimestamp("2020-08-20 12:00:00"), 100, "assad"),
                new Record("12345", toSqlTimestamp("2020-08-20 12:05:00"), 99, "assad"),
                new Record("12345", toSqlTimestamp("2020-08-20 12:40:00"), 1024, "assad"),
                new Record("12346", toSqlTimestamp("2020-08-21 09:20:00"), 1024, "alex"),
                new Record("12346", toSqlTimestamp("2020-08-21 16:32:00"), 8, "alex"),
                new Record("12346", toSqlTimestamp("2020-08-22 13:20:00"), 19, "alex")
        );
        int result = queryRunner.ormUpsert().saveAll(recordList, "test.record");
        Assertions.assertEquals(1, result);
    }

    @DisplayName("PhxUpdateQuery 手动 upsert")
    @Test
    public void testUpsert() throws ParseException, SQLException {
        // 手动新增
        int result = queryRunner
                .upsert("upsert into test.record(uid, punch_date, username, amount) values(?,?,?,?)")
                .param("23333", toSqlTimestamp("2020-08-20 08:12:13"), "Benson", 13)
                .update();

        // 手动批量新增
        queryRunner.batchUpdate("upsert into test.record(uid, punch_date, username, amount) values(?,?,?,?)")
                .putParam("23333", toSqlTimestamp("2020-08-20 08:13:15"), "Benson", 20)
                .putParam("23333", toSqlTimestamp("2020-08-20 09:34:00"), "Benson", 500)
                .putParam("23333", toSqlTimestamp("2020-08-20 10:22:00"), "Benson", 45)
                .putParam("53333", toSqlTimestamp("2020-08-20 12:12:12"), "Darcy", 999)
                .update();
    }


    @DisplayName("PhxSelectQuery 查询测试")
    @Test
    public void testSelectList() throws ParseException, SQLException {
        // 查询指定 uid、punch_date 记录
        Record record = queryRunner
                .select("select * from test.record where uid=? and punch_date=?")
                .param("12345", toSqlTimestamp("2020-08-20 12:00:00"))
                .queryOne(Record.class)
                .orElse(null);
        log.info(() -> "record=" + record);

        //查询指定 uid，且 punch_date 在指定日期的记录
        List<Record> records = queryRunner
                .select("select * from test.record where uid=? and punch_date between ? and ? ")
                .param("12345", toSqlTimestamp("2020-08-20 00:00:00"), toSqlTimestamp("2020-08-21 00:00:00"))
                .queryList(Record.class);
        log.info(() -> "record list size: " + records.size());
        records.forEach(e -> log.info(() -> "record=" + e));
    }

    @DisplayName("PhxSelectQuery select in 查询测试")
    @Test
    public void testSelectIn() throws SQLException {
        List<Record> records = queryRunner
                .select("select * from test.record where uid in (#)")
                .param(Lists.newArrayList("12345", "23333"))
                .queryList(Record.class);
        log.info(() -> "record list size: " + records.size());
        records.forEach(e -> log.info(() -> "record=" + e));
    }


    @DisplayName("PhxSelectQuery 强制倒排索引查询测试")
    @Test
    public void testSelectForceIndex() throws SQLException {
        // 强制使用倒排索引查询
        List<Record> records = queryRunner
                .select("select /*+ INDEX(test.record idx_record_username) */ * from test.record where username=?")
                .param("assad")
                .queryList(Record.class);
        log.info(() -> "record2 list size: " + records.size());
        records.forEach(e -> log.info(() -> "record=" + e));
    }


    @DisplayName("PhxUpdateQuery 删除测试")
    @Test
    public void testDelete() throws ParseException, SQLException {
        // 条件删除
        int result = queryRunner
                .delete("delete from test.record where uid=? and punch_date=?")
                .param("12345", toSqlTimestamp("2020-08-20 12:00:00"))
                .update();
    }

    @DisplayName("PhxUpdateQuery 删除测试2")
    @Test
    public void testDeleteIn() throws SQLException {
        // 条件删除
        int result = queryRunner
                .delete("delete from test.record where uid in (#)")
                .param(Lists.newArrayList("12345", "53333"))
                .update();
    }

    @DisplayName("PhxSaveQuery 保存 bean，属性覆盖测试")
    @Test
    public void testSaveOverride() throws ParseException, SQLException {
        final String selectRecordSql = "select * from test.record where uid=? and punch_date=?";
        String uid = "12345";
        Timestamp punchTime = toSqlTimestamp("2020-08-20 12:20:00");

        // save bean
        Record bean = new Record();
        bean.setUid(uid);
        bean.setPunchDate(punchTime);
        bean.setUsername("assad");
        bean.setAmount(200);
        queryRunner.ormUpsert().save(bean, "test.record");

        // get bean
        Record bean1 = queryRunner.select(selectRecordSql)
                .param(uid, punchTime)
                .queryOne(Record.class).orElse(null);

        // update bean
        bean1.setAmount(null);
        bean1.setUsername(null);

        // save not override properties
        queryRunner.ormUpsert().save(bean1, "test.record", false);
        Record bean2 = queryRunner.select(selectRecordSql)
                .param(uid, punchTime)
                .queryOne(Record.class).orElse(null);
        Assertions.assertEquals(bean, bean2);

        // save override properties
        queryRunner.ormUpsert().save(bean1, "test.record", true);
        bean2 = queryRunner.select(selectRecordSql)
                .param(uid, punchTime)
                .queryOne(Record.class).orElse(null);
        Assertions.assertEquals(bean1, bean2);
    }


}
