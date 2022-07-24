package sy.init.neo;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.neo4j.driver.v1.Driver;
import utils.PropertiesReader;

import java.security.KeyException;


/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class NeoPoolUtil {
    private static GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

    static {
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(1);
    }

    static String host;
    static String name;
    static String pwd;

    static {
        host = PropertiesReader.get("neo_host");
        name = PropertiesReader.get("neo_name");
        pwd = PropertiesReader.get("neo_pwd");
    }

    private static NeoPool neoPool = new NeoPool(host, name, pwd);

    private static GenericObjectPool<Driver> driverPool = new GenericObjectPool<>(neoPool, poolConfig);

    /**
     * get object
     * @return
     * @throws Exception
     */
    public static Driver borrowDriver() {
        Driver driver = null;
        // get object from pool
        try {
            driver = driverPool.borrowObject();
            return driver;
        } catch (Exception e) {
            try {
                throw new KeyException("获取neo4j连接失败！");
            } catch (KeyException ex) {
                ex.printStackTrace();
            }
        }
        return driver;
    }

    /**
     * return object
     * @param driver
     */
    public static void returnDriver(Driver driver) {
        driverPool.returnObject(driver);
    }

    /**
     *close object
     * @param driver
     */
    public static void closeDriver(Driver driver) {
        driver.close();
    }

}
