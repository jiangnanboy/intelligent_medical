package sy.process.proc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sy.process.ProcessMain;
import utils.CollectionUtil;
import utils.PropertiesReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author sy
 * @date 2022/7/21 20:22
 */
public class EsProcessData {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsProcessData.class);

    /**
     * process data from json file
     * @param jsonFilePath
     * @return
     */
    public static List<Map<String, Object>> getJsonData(String jsonFilePath) {
        LOGGER.info("get json data form -> " + jsonFilePath);
        if(Files.notExists(Paths.get(jsonFilePath))) {
            LOGGER.error("File not exists -> " + jsonFilePath);
        }
        List<Map<String, Object>> infoMapList = CollectionUtil.newArrayList();
        try(Stream<String> stream = Files.lines(Paths.get(jsonFilePath))) {
            infoMapList = stream.map(line -> (Map<String, Object>) JSON.parse(line)).collect(Collectors.toList());
        } catch(IOException e) {
            e.printStackTrace();
        }
        return infoMapList;
    }

}
