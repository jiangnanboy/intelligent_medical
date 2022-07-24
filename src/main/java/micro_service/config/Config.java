package micro_service.config;

import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

/**
 * @author sy
 * @date 2022/7/21 20:22
 */
public class Config {
    public static void initPort(int portName) {
        port(portName);
    }

    public static void initStaticFileLocation(String staticFilePath) {
        staticFileLocation(staticFilePath);
    }

}
