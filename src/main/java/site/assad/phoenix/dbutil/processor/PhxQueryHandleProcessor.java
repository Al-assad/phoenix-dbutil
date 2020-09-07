package site.assad.phoenix.dbutil.processor;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * phoenix query 处理过程
 *
 * @author yulinying
 * @since 2020/8/27
 */
public class PhxQueryHandleProcessor {

    /**
     * 模版 sql 中数组占位符
     * 如 select * from xx where uid in (#);
     * 对应填充参数必须是 Collection 子类
     */
    public final static String ARRAY_PLACEHOLDER = "#";

    /**
     * 请求 sql 参数替换
     *
     * @param sql    sql文本
     * @param params 参数列表
     * @return pair
     */
    public static Pair<String, Object[]> handleQueryParams(@Nonnull String sql, Object[] params) {
        return handleArrayPlaceHolder(sql, params);
    }

    /**
     * 处理数组占位符
     */
    private static Pair<String, Object[]> handleArrayPlaceHolder(String sql, Object[] params) {
        if (ArrayUtils.isEmpty(params)) {
            return Pair.of(sql, params);
        }
        int flag = 0;
        List<Object> paramList = new ArrayList<>();
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            // 处理 in 拼接
            if (param instanceof Collection) {
                String placeholderStr = repeatChar("?", ((Collection<?>) param).size(), ",");
                sql = sql.replaceFirst(ARRAY_PLACEHOLDER, placeholderStr);
                paramList.addAll(Lists.newArrayList(ArrayUtils.subarray(params, flag, i)));
                paramList.addAll((Collection<?>) param);
                flag = i + 1;
            }
        }
        if (paramList.size() == 0) {
            return Pair.of(sql, params);
        }
        if (flag < params.length) {
            paramList.addAll(Lists.newArrayList(ArrayUtils.subarray(params, flag, params.length)));
        }
        return Pair.of(sql, paramList.toArray(new Object[]{}));
    }

    /**
     * 创建重复字符字符串
     *
     * @param ch        字符
     * @param size      字符重复次数
     * @param delimiter 风隔符
     * @return string
     */
    private static String repeatChar(String ch, int size, String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(ch).append(delimiter);
        }
        if (sb.length() <= 1) {
            return sb.toString();
        }
        return sb.substring(0, sb.length() - 1);
    }
}
