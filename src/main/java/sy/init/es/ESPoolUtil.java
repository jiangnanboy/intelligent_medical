package sy.init.es;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.elasticsearch.client.RestHighLevelClient;
import utils.CollectionUtil;
import utils.PropertiesReader;

import java.io.IOException;
import java.security.KeyException;
import java.util.Arrays;
import java.util.List;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class ESPoolUtil {

    /**
     * 对象池配置，池中有10个client。
     */
    private static GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();

    static {
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(10);
        poolConfig.setMinIdle(1);
    }

    /**
     * 池化的对象工厂类
      */
    private static List<String> host = CollectionUtil.newArrayList();
    private static String http;
    private static int port;

    static {
        String hostString = PropertiesReader.get("es_host");
        String[] hostStrs = hostString.split(",");
        Arrays.asList(hostStrs).forEach(hostStr -> host.add(hostStr));
        http = PropertiesReader.get("es_http");
        port = Integer.parseInt(PropertiesReader.get("es_port"));
    }
    private static ESPool esPool = new ESPool(host, http, port);

    /**
     * 利用工厂类和配置类生成对象池
     */
    private static GenericObjectPool<RestHighLevelClient> clientPool = new GenericObjectPool<>(esPool, poolConfig);

    /**
     * get object
     * @return
     * @throws Exception
     */
    public static RestHighLevelClient borrowClient() {
        RestHighLevelClient client = null;
        // get object from pool
        try {
            client = clientPool.borrowObject();
            return client;
        } catch (Exception e) {
            try {
                throw new KeyException("获取ES连接失败！");
            } catch (KeyException ex) {
                ex.printStackTrace();
            }
        }
        return client;
    }

    /**
     * return object
     * @param client
     */
    public static void returnClient(RestHighLevelClient client) {
        clientPool.returnObject(client);
    }

    /**
     *close object
     * @param client
     */
    public static void closeClient(RestHighLevelClient client) {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

