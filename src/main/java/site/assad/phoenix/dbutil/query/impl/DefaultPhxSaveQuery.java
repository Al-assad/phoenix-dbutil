package site.assad.phoenix.dbutil.query.impl;

import site.assad.phoenix.dbutil.PhxConnectInfo;
import site.assad.phoenix.dbutil.helper.PhxConnectionHelper;
import site.assad.phoenix.dbutil.query.PhxSaveQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Phoenix Bean 保存操作默认实现
 *
 * @author yulinying
 * @since 2020/8/19
 */
public class DefaultPhxSaveQuery implements PhxSaveQuery {

    /**
     * phoenix upsert sql 语句模版
     */
    private static final String PHOENIX_UPSERT_QUERY = "UPSERT INTO %s(%s) VALUES(%s)";
    private final PhxConnectInfo connectInfo;

    public DefaultPhxSaveQuery(@Nonnull PhxConnectInfo connectInfo) {
        this.connectInfo = connectInfo;
    }

    @Override
    public <T> int save(@Nonnull T bean, @Nonnull String tableName) throws SQLException{
        return save(bean, tableName, true);
    }

    @Override
    public <T> int save(@Nonnull T bean, @Nonnull String tableName, boolean overrideNullProps) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return 0;
        }
        if (bean == null) {
            return 1;
        }
        // 获取 bean 属性描述
        PropertyDescriptor[] props = this.propertyDescriptors(bean.getClass());
        if (props.length <= 1) {
            return 1;
        }
        // 提取 upsert sql 要素
        Pair<String, Object[]> upsertSqlPair = extractUpsertSql(props, tableName, bean, overrideNullProps);
        // upsert 操作
        Connection connection = PhxConnectionHelper.createPhoenixConnection(connectInfo);
        QueryRunner runner = new QueryRunner();
        int result = runner.update(connection, upsertSqlPair.getLeft(), upsertSqlPair.getRight());
        connection.commit();
        connection.close();
        return result;
    }

    @Override
    public <T> int saveAll(@Nonnull List<T> beanList, @Nonnull String tableName) throws SQLException {
        return saveAll(beanList, tableName, true);
    }

    @Override
    public <T> int saveAll(@Nonnull List<T> beanList, @Nonnull String tableName, boolean overrideAll) throws SQLException {
        if (StringUtils.isBlank(tableName)) {
            return 0;
        }
        if (CollectionUtils.isEmpty(beanList)) {
            return 1;
        }
        // 获取 bean 属性描述
        PropertyDescriptor[] props = this.propertyDescriptors(beanList.get(0).getClass());
        if (props.length <= 1) {
            return 1;
        }
        // 提取 upsert sql 要素
        List<Pair<String, Object[]>> upsertSqlPairs = new ArrayList<>(beanList.size());
        for (T bean : beanList) {
            upsertSqlPairs.add(extractUpsertSql(props, tableName, bean, overrideAll));
        }
        // upsert 操作
        Connection connection = PhxConnectionHelper.createPhoenixConnection(connectInfo);
        connection.setAutoCommit(false);
        QueryRunner runner = new QueryRunner();
        for (Pair<String, Object[]> pair : upsertSqlPairs) {
            int result = runner.update(connection, pair.getLeft(), pair.getRight());
            if (1 != result) {
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return 1;
    }

    /**
     * 提取 phoenix upsert sql 要素
     *
     * @param props    对象属性列表
     * @param instance 对象实例
     * @param saveAll  是否保存实例空值属性
     * @return Pair[placeholderSQL, placeholderParam]
     */
    private <T> Pair<String, Object[]> extractUpsertSql(@Nonnull PropertyDescriptor[] props,
                                                        @Nonnull String tableName,
                                                        @Nonnull T instance,
                                                        boolean saveAll) throws SQLException {
        // ? 占位符列表
        List<String> placeholders = new ArrayList<>(props.length);
        // phoenix 表字段列表
        List<String> tableProps = new ArrayList<>(props.length);
        // 占位字段填充值
        List<Object> propVals = new ArrayList<>(props.length);
        // 提取sql字段要素
        for (PropertyDescriptor prop : props) {
            String propName = prop.getName();
            if ("class".equals(propName)) {
                continue;
            }
            Method readMethod = prop.getReadMethod();
            Object propVal;
            try {
                propVal = readMethod.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new SQLException("Bean reflect failed：" + e.getMessage());
            }
            // 不保存空值属性
            if (!saveAll && propVal == null) {
                continue;
            }
            placeholders.add("?");
            tableProps.add(beanPropMap(propName));
            propVals.add(propVal);
        }
        // 组装 upsert sql 并填充参数
        String sql = genUpsertSQL(tableName, placeholders, tableProps);
        return Pair.of(sql, propVals.toArray(new Object[]{}));
    }

    /**
     * 获取类属性描述信息
     */
    private PropertyDescriptor[] propertyDescriptors(Class<?> c)
            throws SQLException {
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(c);
        } catch (IntrospectionException e) {
            throw new SQLException("Bean introspection failed: " + e.getMessage());
        }
        return beanInfo.getPropertyDescriptors();
    }

    /**
     * 映射 bean 属性名称到 phoenix 表字段
     * 驼峰风格 -> 下划线风格 + 全部大写
     */
    private String beanPropMap(@Nonnull String propName) {
        String[] split = StringUtils.splitByCharacterTypeCamelCase(propName);
        return StringUtils.upperCase(String.join("_", split));
    }

    /**
     * 构建 phoenix upsert sql 语句（使用 ？ 占位符）
     */
    private String genUpsertSQL(@Nonnull String tableName, @Nonnull List<String> placeholders, @Nonnull List<String> tableProps) {
        String placeholder = String.join(",", placeholders);
        String tableProp = String.join(",", tableProps);
        return String.format(PHOENIX_UPSERT_QUERY, tableName, tableProp, placeholder);
    }

}
