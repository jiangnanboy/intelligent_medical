package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class PropertiesReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(PropertiesReader.class);

    private static Properties properties = new Properties();

    static {
        try {
            properties.load(new InputStreamReader(PropertiesReader.class.getClassLoader().getResourceAsStream("properties.properties"), "UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String keyName) {
        return properties.getProperty(keyName, "");
    }

}
