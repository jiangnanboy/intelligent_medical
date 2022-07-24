package sy.init.spark;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.spark.sql.SparkSession;

import java.security.KeyException;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class SparkPoolUtil {
    private static GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
    static {
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(1);
    }

    private static SparkPool sparkPool = new SparkPool();
    private static GenericObjectPool<SparkSession> sessionPool = new GenericObjectPool<>(sparkPool, poolConfig);

    /**
     * get object
     * @return
     * @throws Exception
     */
    public static SparkSession borrowDriver() {
        SparkSession session = null;
        // get object from pool
        try {
            session = sessionPool.borrowObject();
            return session;
        } catch (Exception e) {
            try {
                throw new KeyException("获取spark连接失败！");
            } catch (KeyException ex) {
                ex.printStackTrace();
            }
        }
        return session;
    }

    /**
     * return object
     * @param session
     */
    public static void returnDriver(SparkSession session) {
        sessionPool.returnObject(session);
    }

    /**
     *close object
     * @param session
     */
    public static void closeDriver(SparkSession session) {
        session.close();
    }


}
