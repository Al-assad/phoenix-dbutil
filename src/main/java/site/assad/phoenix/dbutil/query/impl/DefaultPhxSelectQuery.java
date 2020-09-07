package site.assad.phoenix.dbutil.query.impl;

import site.assad.phoenix.dbutil.PhxConnectInfo;
import site.assad.phoenix.dbutil.helper.PhxConnectionHelper;
import site.assad.phoenix.dbutil.processor.PhxQueryHandleProcessor;
import site.assad.phoenix.dbutil.processor.PhxRowProcessor;
import site.assad.phoenix.dbutil.query.PhxSelectQuery;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * phoenix select 执行器默认实现
 *
 * @author yulinying
 * @since 2020/8/19
 */
public class DefaultPhxSelectQuery implements PhxSelectQuery {

    private final PhxConnectInfo connectInfo;
    private String selectSql;
    private Object[] params;

    public DefaultPhxSelectQuery(@Nonnull PhxConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }

    @Override
    public DefaultPhxSelectQuery sql(@Nonnull String selectSql) {
        if (StringUtils.isBlank(selectSql)) {
            return this;
        }
        this.selectSql = selectSql;
        return this;
    }

    @Override
    public DefaultPhxSelectQuery param(Object... args) {
        this.params = args;
        return this;
    }

    public String getSelectSql() {
        return selectSql;
    }

    public Object[] getParams() {
        return params;
    }

    @Override
    @Nonnull
    public <T> Optional<T> queryOne(@Nonnull Class<T> resultType) throws SQLException {
        if (resultType == null || StringUtils.isBlank(selectSql)) {
            return Optional.empty();
        }
        Pair<String, Object[]> pair = PhxQueryHandleProcessor.handleQueryParams(selectSql, params);

        Connection connection = PhxConnectionHelper.createPhoenixConnection(connectInfo);
        ResultSetHandler<T> handler = new BeanHandler<>(resultType, PhxRowProcessor.getInstance());
        QueryRunner runner = new QueryRunner();
        Optional<T> result = Optional.ofNullable(runner.query(connection, pair.getLeft(), handler, pair.getRight()));
        connection.close();
        return result;
    }

    @Override
    @Nonnull
    public <T> List<T> queryList(@Nonnull Class<T> resultType) throws SQLException {
        if (resultType == null || StringUtils.isBlank(selectSql)) {
            return new ArrayList<>(0);
        }
        Pair<String, Object[]> pair = PhxQueryHandleProcessor.handleQueryParams(selectSql, params);

        Connection connection = PhxConnectionHelper.createPhoenixConnection(connectInfo);
        ResultSetHandler<List<T>> handler = new BeanListHandler<>(resultType, PhxRowProcessor.getInstance());
        QueryRunner runner = new QueryRunner();
        List<T> result = runner.query(connection, pair.getLeft(), handler, pair.getRight());
        if (result == null) {
            result = new ArrayList<>(0);
        }
        connection.close();
        return result;
    }



}
