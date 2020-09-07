package site.assad.phoenix.dbutil.processor;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;

/**
 * Phoenix 行数据映射处理
 *
 * @author yulinying
 * @since 2020/8/19
 */
public class PhxRowProcessor extends BasicRowProcessor {

    private static final BeanProcessor CONVERT = new PhxBeanProcessor();

    private static final PhxRowProcessor INSTANCE = new PhxRowProcessor();

    public PhxRowProcessor() {
        super(CONVERT);
    }

    public static PhxRowProcessor getInstance() {
        return INSTANCE;
    }

}
