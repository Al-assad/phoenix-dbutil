package site.assad.phoenix.dbutil;


import site.assad.phoenix.dbutil.query.PhxBatchUpdateQuery;
import site.assad.phoenix.dbutil.query.PhxSaveQuery;
import site.assad.phoenix.dbutil.query.PhxSelectQuery;
import site.assad.phoenix.dbutil.query.PhxUpdateQuery;
import site.assad.phoenix.dbutil.query.impl.DefaultPhxBatchUpdateQuery;
import site.assad.phoenix.dbutil.query.impl.DefaultPhxSaveQuery;
import site.assad.phoenix.dbutil.query.impl.DefaultPhxSelectQuery;
import site.assad.phoenix.dbutil.query.impl.DefaultPhxUpdateQuery;

import javax.annotation.Nonnull;
import java.util.Properties;

/**
 * Phoenix sql 运行器
 *
 * @author yulinying
 * @since 2020/8/19
 */
public class PhxQueryRunner {

    private PhxConnectInfo connectInfo;

    public PhxQueryRunner(@Nonnull PhxConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }

    /**
     * 获取 jdbc 连接地址
     */
    public String getJdbcUrl() {
        return connectInfo.getJdbcUrl();
    }

    /**
     * 获取 jdbc 连接参数
     */
    public Properties getConnectionProps() {
        return connectInfo.getProps();
    }

    /**
     * 动态更改  phoenix 连接信息
     *
     * @param connectInfo Phoenix 连接信息
     */
    public void dynamicChangeConnectInfo(@Nonnull PhxConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }

    /**
     * 获取 select 执行器
     *
     * @param sql 查询模版sql文本
     * @return this
     * @see PhxSelectQuery
     */
    public PhxSelectQuery select(@Nonnull String sql) {
        return new DefaultPhxSelectQuery(connectInfo).sql(sql);
    }

    /**
     * 获取 orm upsert 执行器（自动化 Bean Upsert）
     *
     * @return this
     * @see PhxSaveQuery
     */
    public PhxSaveQuery ormUpsert() {
        return new DefaultPhxSaveQuery(connectInfo);
    }

    /**
     * 获取 upsert 执行器（手动编写 upsert sql）
     *
     * @param sql 删除sql
     * @return this
     * @see PhxUpdateQuery
     */
    public PhxUpdateQuery upsert(@Nonnull String sql) {
        return new DefaultPhxUpdateQuery(connectInfo).sql(sql);
    }

    /**
     * 获取 delete 执行器
     *
     * @param sql 删除sql
     * @return this
     * @see PhxUpdateQuery
     */
    public PhxUpdateQuery delete(@Nonnull String sql) {
        return new DefaultPhxUpdateQuery(connectInfo).sql(sql);
    }

    /**
     * 获取 batch update 执行器
     *
     * @param sql sql文本
     * @return this
     * @see PhxBatchUpdateQuery
     */
    public PhxBatchUpdateQuery batchUpdate(@Nonnull String sql) {
        return new DefaultPhxBatchUpdateQuery(connectInfo).sql(sql);
    }


}
