package site.assad.phoenix.dbutil.query;

import javax.annotation.Nonnull;
import java.sql.SQLException;

/**
 * phoenix update 操作执行器（upsert、delete）
 *
 * @author yulinying
 * @since 2020/8/28
 */
public interface PhxUpdateQuery {

    /**
     *  重新设置 Update SQL
     *
     * @param updateSql 更新sql文本
     * @return this
     */
    PhxUpdateQuery sql(@Nonnull String updateSql);

    /**
     * 设置查询参数列表，重复设置，参数会被覆盖
     *
     * @param args 查询参数列表，与 sql 占位符位置顺序对应
     * @return this
     */
    PhxUpdateQuery param(Object... args);

    /**
     * 执行 update 操作
     *
     * @return update 操作影响记录数量
     * @throws SQLException phoenix update 执行异常
     */
    int update() throws SQLException;

}
