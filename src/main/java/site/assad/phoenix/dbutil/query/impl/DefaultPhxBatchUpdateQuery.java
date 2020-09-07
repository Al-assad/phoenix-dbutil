package site.assad.phoenix.dbutil.query.impl;

import site.assad.phoenix.dbutil.PhxConnectInfo;
import site.assad.phoenix.dbutil.helper.PhxConnectionHelper;
import site.assad.phoenix.dbutil.query.PhxBatchUpdateQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * phoenix 批量 update 执行器默认实现
 *
 * @author yulinying
 * @since 2020/8/19
 */
public class DefaultPhxBatchUpdateQuery implements PhxBatchUpdateQuery {

    private final PhxConnectInfo connectInfo;
    private String updateSql;
    private List<Object[]> sqlParams = new ArrayList<>();

    public DefaultPhxBatchUpdateQuery(PhxConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }

    @Override
    public DefaultPhxBatchUpdateQuery sql(@Nonnull String updateSql) {
        if (StringUtils.isBlank(updateSql)) {
            return this;
        }
        this.updateSql = updateSql;
        return this;
    }

    @Override
    public DefaultPhxBatchUpdateQuery putParam(Object... param) {
        sqlParams.add(param);
        return this;
    }

    public String getUpdateSql() {
        return updateSql;
    }

    public List<Object[]> getSqlParams() {
        return sqlParams;
    }

    @Override
    public int[] update() throws SQLException{
        if (CollectionUtils.isEmpty(sqlParams)) {
            return new int[0];
        }
        Object[][] params = toMatrix(sqlParams);
        if (params == null) {
            return new int[0];
        }
        // 执行批量更新
        Connection connection = PhxConnectionHelper.createPhoenixConnection(connectInfo);
        connection.setAutoCommit(false);
        QueryRunner runner = new QueryRunner();
        int[] result = runner.batch(connection, updateSql, params);
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 线性表转二维数组
     */
    @Nullable
    private Object[][] toMatrix(@Nonnull List<Object[]> list) {
        int maxLen = list.stream()
                .filter(ArrayUtils::isNotEmpty)
                .map(m -> m.length)
                .max(Integer::compareTo)
                .orElse(0);
        if (maxLen == 0) {
            return null;
        }
        Object[][] matrix = new Object[list.size()][maxLen];
        for (int i = 0; i < list.size(); i++) {
            matrix[i] = list.get(i);
        }
        return matrix;
    }

}
