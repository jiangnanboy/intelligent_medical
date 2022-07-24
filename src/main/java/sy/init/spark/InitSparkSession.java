package sy.init.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.Optional;
import org.apache.spark.sql.SparkSession;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class InitSparkSession {

    private static SparkSession sparkSession = null;
    private static SparkConf sparkConf = null;

    private static void initSparkConf() {
        sparkConf = new SparkConf()
                .setAppName("ProcessData")
                .setMaster("local[*]")
                .set("spark.executor.memory", "4g")
                .set("spark.network.timeout", "1000")
                .set("spark.sql.broadcastTimeout", "2000")
                .set("spark.executor.heartbeatInterval", "100");
    }

    private static void initSparkSession() {
        if(null == sparkConf) {
            initSparkConf();
        }
        sparkSession = SparkSession.builder().config(sparkConf).getOrCreate();
//        sparkSession = SparkSession
//                .builder()
//                .appName("Test")
//                .master("local[*]")
//                .config("spark.executor.memory", "4g")
//                .getOrCreate();
    }

    public static SparkSession getSparkSession() {
        if(null == sparkSession) {
            initSparkSession();
        }
        return sparkSession;
    }

    public static void closeSparkSession() {
        if(Optional.ofNullable(sparkSession).isPresent()) {
            sparkSession.close();
        }
    }

}
