package site.assad.phoenix.dbutil;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import site.assad.phoenix.dbutil.helper.PhxDateHelper;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

import static site.assad.phoenix.dbutil.helper.PhxDateHelper.toSqlTimestamp;

/**
 * select in 查询测试
 *
 * @author yulinying
 * @since 2020/8/27
 */
@DisplayName("Phoenix select in 查询测试")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PhoenixSelectInQueryTest {

    private final static transient Logger log = LoggerFactory.getLogger(PhoenixDbUtilTest.class);
    private PhxQueryRunner queryRunner;

    @BeforeAll
    public void initJdbcUrl() throws SQLException {
        queryRunner = PhoenixDbUtil.queryRunner("jdbc:phoenix:do1cloud01,do1cloud02,do1cloud03");
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

    @DisplayName("PhxSelectQuery select in 查询测试 - 2")
    @Test
    public void testSelectIn2() throws ParseException, SQLException {
        List<Record> records = queryRunner
                .select("select * from test.record where uid in (#) and  punch_date > ? and punch_date < ?")
                .param(Lists.newArrayList("12345", "23333"), PhxDateHelper.toSqlTimestamp("2020-08-20 00:00:00"), PhxDateHelper.toSqlTimestamp("2020-08-21 00:00:00"))
                .queryList(Record.class);
        log.info(() -> "record list size: " + records.size());
        records.forEach(e -> log.info(() -> "record=" + e));
    }

    @DisplayName("PhxSelectQuery select in 查询测试 - 3")
    @Test
    public void testSelectIn3() throws ParseException, SQLException {
        List<Record> records = queryRunner
                .select("select * from test.record where punch_date > ? and punch_date < ? and uid in (#) ")
                .param(PhxDateHelper.toSqlTimestamp("2020-08-20 00:00:00"), PhxDateHelper.toSqlTimestamp("2020-08-21 00:00:00"), Lists.newArrayList("12345", "23333"))
                .queryList(Record.class);
        log.info(() -> "record list size: " + records.size());
        records.forEach(e -> log.info(() -> "record=" + e));
    }

    @DisplayName("PhxSelectQuery select in 查询测试 - 4")
    @Test
    public void testSelectIn4() throws ParseException, SQLException {
        List<Record> records = queryRunner
                .select("select * from test.record where punch_date > ? and uid in (#) and punch_date < ?  ")
                .param(PhxDateHelper.toSqlTimestamp("2020-08-20 00:00:00"), Lists.newArrayList("12345", "23333"), PhxDateHelper.toSqlTimestamp("2020-08-21 00:00:00"))
                .queryList(Record.class);
        log.info(() -> "record list size: " + records.size());
        records.forEach(e -> log.info(() -> "record=" + e));
    }

    @DisplayName("PhxSelectQuery select in 查询测试 - 5")
    @Test
    public void testSelectIn5() throws SQLException {
        List<Record> records = queryRunner
                .select("select * from test.record where uid in (#) and username in (#)")
                .param(Lists.newArrayList("12345", "23333"),
                        Lists.newArrayList("alex", "assad", "vancy"))
                .queryList(Record.class);
        log.info(() -> "record list size: " + records.size());
        records.forEach(e -> log.info(() -> "record=" + e));
    }


    @DisplayName("PhxSelectQuery select in 查询测试 - 6")
    @Test
    public void testSelectIn6() throws ParseException, SQLException {
        List<Record> records = queryRunner
                .select("select * from test.record where uid in (#) and punch_date > ? and username in (#)")
                .param(Lists.newArrayList("12345", "23333"),
                        PhxDateHelper.toSqlTimestamp("2020-08-21 00:00:00"),
                        Lists.newArrayList("alex", "assad", "vancy"))
                .queryList(Record.class);
        log.info(() -> "record list size: " + records.size());
        records.forEach(e -> log.info(() -> "record=" + e));
    }


}
