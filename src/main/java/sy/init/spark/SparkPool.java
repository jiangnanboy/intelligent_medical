package sy.init.spark;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

import java.util.Optional;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class SparkPool implements PooledObjectFactory<SparkSession> {

    SparkConf sparkConf;

    public SparkPool() {
        initSparkConf();
    }

    private void initSparkConf() {
        sparkConf = new SparkConf()
                .setAppName("ProcessData")
                .setMaster("local[*]")
                .set("spark.executor.memory", "4g")
                .set("spark.network.timeout", "1000")
                .set("spark.sql.broadcastTimeout", "2000")
                .set("spark.executor.heartbeatInterval", "100");
    }

    @Override
    public PooledObject<SparkSession> makeObject() throws Exception {
        SparkSession sparkSession = null;
        if(Optional.ofNullable(this.sparkConf).isPresent()) {
            sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();
        }
        return new DefaultPooledObject<>(sparkSession);
    }

    @Override
    public void destroyObject(PooledObject<SparkSession> pooledObject) throws Exception {
        SparkSession sparkSession = pooledObject.getObject();
        sparkSession.close();
    }

    @Override
    public boolean validateObject(PooledObject<SparkSession> pooledObject) {
        System.out.println("spark validateObject");
        return false;
    }

    @Override
    public void activateObject(PooledObject<SparkSession> pooledObject) throws Exception {
        System.out.println("spark activateObject");
    }

    @Override
    public void passivateObject(PooledObject<SparkSession> pooledObject) throws Exception {
        System.out.println("spark passivateObject");
    }
}


