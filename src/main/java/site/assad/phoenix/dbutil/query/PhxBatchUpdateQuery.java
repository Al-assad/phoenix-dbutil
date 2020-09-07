package site.assad.phoenix.dbutil.query;

import javax.annotation.Nonnull;
import java.sql.SQLException;

/**
 * phoenix 批量 update 执行器
 *
 * @author yulinying
 * @since 2020/8/28
 */
public interface PhxBatchUpdateQuery {

    /**
     *  重新设置 Update SQL
     *
     * @param updateSql 更新sql文本
     * @return this
     */
    PhxBatchUpdateQuery sql(@Nonnull String updateSql);

    /**
     * 新增填充参数
     *
     * @param param sql占位符参数
     * @return this
     */
    PhxBatchUpdateQuery putParam(Object... param);

    /**
     * 执行批量更新
     * 当任何一条sql执行失败，全部数据不提交
     *
     * @return 每一条更新sql影响记录数量
     * @throws SQLException phoenix 批量更新执行异常
     */
    int[] update() throws SQLException;

}
