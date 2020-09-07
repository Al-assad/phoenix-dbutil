package site.assad.phoenix.dbutil;


import site.assad.phoenix.dbutil.helper.PhxConnectionHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.Properties;

/**
 * phoenix 数据库操作工具
 * 提供对 PhxQueryRunner 快速调用
 *
 * @author yulinying
 * @since 2020/8/28
 */
public class PhoenixDbUtil {

    /**
     * 获取指定连接参数的 Phoenix 指令执行器实例
     *
     * @param jdbcUrl jdbc url 地址
     * @return PhxQueryRunner
     * @see PhxQueryRunner
     */
    public static PhxQueryRunner queryRunner(@Nonnull String jdbcUrl) throws SQLException {
        boolean flag = PhxConnectionHelper.checkConnection(PhxConnectInfo.of(jdbcUrl));
        if (!flag) {
            throw new SQLException("Phoenix Server 连接失败。连接信息: jdbcUrl=" + jdbcUrl);
        }
        return new PhxQueryRunner(PhxConnectInfo.of(jdbcUrl));
    }

    /**
     * 获取指定连接参数的 Phoenix 指令执行器实例
     *
     * @param jdbcUrl      jdbc url 地址
     * @param connectProps jdbc 额外连接参数
     * @return PhxQueryRunner
     * @see PhxQueryRunner
     */
    public static PhxQueryRunner queryRunner(@Nonnull final String jdbcUrl, @Nullable final Properties connectProps) throws SQLException {
        boolean flag = PhxConnectionHelper.checkConnection(PhxConnectInfo.of(jdbcUrl, connectProps));
        if (!flag) {
            throw new SQLException("Phoenix Server 连接失败。连接信息：jdbcUrl=" + jdbcUrl + ", connectProps=" + connectProps);
        }
        return new PhxQueryRunner(PhxConnectInfo.of(jdbcUrl, connectProps));
    }



}

