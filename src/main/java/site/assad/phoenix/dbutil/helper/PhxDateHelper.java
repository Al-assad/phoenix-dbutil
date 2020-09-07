package site.assad.phoenix.dbutil.helper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;

/**
 * Phoenix DbUtil 外部事件转换帮助类
 *
 * @author yulinying
 * @since 2020/8/20
 */
public class PhxDateHelper {


    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private static final String TIME_PATTERN = "HH:mm:ss";
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * java.sql.Date 转 java.util.Date
     */
    public static java.util.Date toDate(java.sql.Date date) {
        if (date == null) {
            return null;
        }
        return new java.util.Date(date.getTime());
    }

    /**
     * java.sql.Time 转 java.util.Date
     */
    public static java.util.Date toDate(java.sql.Time time) {
        if (time == null) {
            return null;
        }
        return new java.util.Date(time.getTime());
    }

    /**
     * java.sql.Timestamp 转 java.util.Date
     */
    public static java.util.Date toDate(java.sql.Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return new java.util.Date(timestamp.getTime());
    }

    /**
     * java.util.Date 转 java.sql.Date
     */
    public static java.sql.Date toSqlDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }

    /**
     * 日期格式化字符串转 java.sql.Date
     *
     * @param dateStr 格式化时间字符串，yyyy-MM-dd
     */
    public static java.sql.Date toSqlDate(String dateStr) throws ParseException {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        return toSqlDate(DateUtils.parseDate(dateStr, DATE_PATTERN));
    }

    /**
     * java.util.Date 转 java.sql.Time
     */
    public static java.sql.Time toSqlTime(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Time(date.getTime());
    }

    /**
     * 时间格式化字符串转 java.sql.Time
     *
     * @param dateStr 格式化时间字符串，HH:mm:ss
     */
    public static java.sql.Time toSqlTime(String dateStr) throws ParseException {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        return toSqlTime(DateUtils.parseDate(dateStr, TIME_PATTERN));
    }

    /**
     * java.util.Date 转 java.sql.Timestamp
     */
    public static java.sql.Timestamp toSqlTimestamp(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Timestamp(date.getTime());
    }

    /**
     * 时间格式化字符串转 java.sql.Timestamp
     *
     * @param dateStr 格式化时间字符串，yyyy-MM-dd HH:mm:ss
     */
    public static java.sql.Timestamp toSqlTimestamp(String dateStr) throws ParseException {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        }
        return toSqlTimestamp(DateUtils.parseDate(dateStr, DATETIME_PATTERN));
    }

}
