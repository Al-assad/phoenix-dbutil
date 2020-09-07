package site.assad.phoenix.dbutil.query;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.List;

/**
 * Phoenix Bean 保存执行器
 *
 * @author yulinying
 * @since 2020/8/28
 */
public interface PhxSaveQuery {

    /**
     * 保存 Bean 实体到 Phoenix
     * <p>
     * 当 Bean 存在属性的值为 null 时，默认会覆盖持久化这些空值属性。
     * 如果在这种情况下不需要保存空值属性，请使用 {@link PhxSaveQuery#save(java.lang.Object, java.lang.String, boolean)}
     *
     * @param bean      实体对象实例
     * @param tableName 实体对象 phoenix 表名
     * @return 保存结果，0：保存失败，1：保存成功
     * @throws SQLException phoenix upsert 执行异常
     */
    <T> int save(@Nonnull T bean, @Nonnull String tableName) throws SQLException;

    /**
     * 保存 Bean 实体到 Phoenix
     *
     * @param bean              实体对象实例
     * @param tableName         实体对象 phoenix 表名
     * @param overrideNullProps 是否保存实例空值属性
     * @return 保存结果，0：保存失败，1：保存成功
     * @throws SQLException phoenix upsert 执行异常
     */
    <T> int save(@Nonnull T bean, @Nonnull String tableName, boolean overrideNullProps) throws SQLException;

    /**
     * 批量保存 Bean 实体列表到 Phoenix
     * <p>
     * 当 Bean 存在属性的值为 null 时，默认会覆盖持久化这些空值属性。
     * 当列表中出现任意一个元素插入失败，该批元素列表全部不提交。
     *
     * @param beanList  实体对象实例列表
     * @param tableName 实体对象 phoenix 表名
     * @return 保存结果，0：保存失败，1：保存成功
     * @throws SQLException phoenix upsert 执行异常
     */
    <T> int saveAll(@Nonnull List<T> beanList, @Nonnull String tableName) throws SQLException;

    /**
     * 批量保存 Bean 实体列表到 Phoenix
     * <p>
     * 当列表中出现任意一个元素插入失败，该批元素列表全部不提交。
     *
     * @param beanList          实体对象实例列表
     * @param tableName         实体对象 phoenix 表名
     * @param overrideNullProps 是否保存实例空值属性
     * @return 保存结果，0：保存失败，1：保存成功
     * @throws SQLException phoenix upsert 执行异常
     */
    <T> int saveAll(@Nonnull List<T> beanList, @Nonnull String tableName, boolean overrideNullProps) throws SQLException;


}
