package site.assad.phoenix.dbutil.query.impl;

import site.assad.phoenix.dbutil.PhxConnectInfo;
import site.assad.phoenix.dbutil.helper.PhxConnectionHelper;
import site.assad.phoenix.dbutil.processor.PhxQueryHandleProcessor;
import site.assad.phoenix.dbutil.query.PhxUpdateQuery;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;

/**
 *  phoenix update 操作执行器默认实现
 *
 * @author yulinying
 * @since 2020/8/19
 */
public class DefaultPhxUpdateQuery implements PhxUpdateQuery {

    private final PhxConnectInfo connectInfo;
    private String updateSql;
    private Object[] params;

    public DefaultPhxUpdateQuery(@Nonnull PhxConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }

    @Override
    public DefaultPhxUpdateQuery sql(@Nonnull String updateSql) {
        if (StringUtils.isBlank(updateSql)) {
            return this;
        }
        this.updateSql = updateSql;
        return this;
    }

    @Override
    public DefaultPhxUpdateQuery param(Object... args) {
        this.params = args;
        return this;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public Object[] getParams() {
        return params;
    }

    @Override
    public int update() throws SQLException {
        if (StringUtils.isBlank(updateSql)) {
            return 0;
        }
        Pair<String, Object[]> pair = PhxQueryHandleProcessor.handleQueryParams(updateSql, params);
        Connection connection = PhxConnectionHelper.createPhoenixConnection(connectInfo);
        QueryRunner runner = new QueryRunner();
        int result = runner.update(connection, pair.getLeft(), pair.getRight());
        connection.commit();
        connection.close();
        return result;
    }

}
