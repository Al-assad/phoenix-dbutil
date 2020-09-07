package site.assad.phoenix.dbutil.query;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * phoenix select 执行器
 *
 * @author yulinying
 * @since 2020/8/28
 */
public interface PhxSelectQuery {

    /**
     * 重新设置查询 SQL
     *
     * @param selectSql 查询 sql， 不可为空
     * @return this
     */
    PhxSelectQuery sql(@Nonnull String selectSql);

    /**
     * 设置查询参数列表，重复设置，参数会被覆盖
     *
     * @param args 查询参数列表，与 sql 占位符位置顺序对应
     * @return this
     */
    PhxSelectQuery param(Object... args);

    /**
     * 查询单个结果
     *
     * @param resultType 结果类型
     * @return 查询结果
     * @throws SQLException phoenix 查询异常
     */
    @Nonnull
    <T> Optional<T> queryOne(@Nonnull Class<T> resultType) throws SQLException;

    /**
     * 查询多个结果
     *
     * @param resultType 结果类型，不指定返回 empty list
     * @return 查询结果列表，查询结果为空返回 empty list
     * @throws SQLException phoenix 查询异常
     */
    @Nonnull
    <T> List<T> queryList(@Nonnull Class<T> resultType) throws SQLException;


}
