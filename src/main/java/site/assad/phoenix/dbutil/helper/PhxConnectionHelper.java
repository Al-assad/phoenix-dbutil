package site.assad.phoenix.dbutil.helper;

import site.assad.phoenix.dbutil.PhxConnectInfo;

import javax.annotation.Nonnull;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * phoenix 连接辅助
 *
 * @author yulinying
 * @since 2020/8/19
 */
public class PhxConnectionHelper {

    /*
     * 注册 phoenix connect driver
     */
    static {
        try {
            DriverManager.registerDriver(new org.apache.phoenix.jdbc.PhoenixDriver());
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 确认  phoenix 连接是否有效
     *
     * @param connectInfo phoenix 连接信息
     * @return true：有效 phoenix 连接，false：无效 phoenix 连接
     */
    public static boolean checkConnection(@Nonnull PhxConnectInfo connectInfo) {
        if (connectInfo == null) {
            return false;
        }
        try {
            Connection connection = createPhoenixConnection(connectInfo);
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取 phoenix jdbc connection 连接对象
     *
     * @param connectInfo phoenix 连接信息
     * @return jdbc 连接对象
     * @throws SQLException phoenix 连接异常
     */
    public static Connection createPhoenixConnection(@Nonnull PhxConnectInfo connectInfo) throws SQLException {
        if (connectInfo.getProps() == null) {
            return DriverManager.getConnection(connectInfo.getJdbcUrl());
        } else {
            return DriverManager.getConnection(connectInfo.getJdbcUrl(), connectInfo.getProps());
        }
    }


}
