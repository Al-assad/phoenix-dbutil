package site.assad.phoenix.dbutil;

import java.util.Properties;


/**
 * Phoenix 连接信息
 *
 * @author yulinying
 * @since 2020/8/28
 */
public class PhxConnectInfo {

    private String jdbcUrl;
    private Properties props;

    public PhxConnectInfo() {
    }

    public PhxConnectInfo(String jdbcUrl, Properties props) {
        this.jdbcUrl = jdbcUrl;
        this.props = props;
    }

    public static PhxConnectInfo of(String jdbcUrl) {
        return new PhxConnectInfo(jdbcUrl, null);
    }

    public static PhxConnectInfo of(String jdbcUrl, Properties props) {
        return new PhxConnectInfo(jdbcUrl, props);
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    @Override
    public String toString() {
        return "PhxConnectInfo{" +
                "jdbcUrl='" + jdbcUrl + '\'' +
                ", props=" + props +
                '}';
    }

}
