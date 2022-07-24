package micro_service.service;

import sy.es.EsUtil;
import utils.PropertiesReader;

/**
 * @author sy
 * @date 2022/7/21 20:22
 */
public class EsUtilService {

    public static void main(String...args) {
        // build mapping and insert data
//        buildMapping();
        String medicalJson = EsUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical")).getPath().replaceFirst("/", "");
        insertData(PropertiesReader.get("medical_index"), medicalJson);
    }

    /**
     * build es mapping
     */
    public static void buildMapping() {
        String mappingFile = PropertiesReader.get("medical_mapping");
        mappingFile = EsUtilService.class.getClassLoader().getResource(mappingFile).getPath().replaceFirst("/", "");
        EsUtil.buildIndexMapping(mappingFile);
    }

    /**
     * build es mapping
     * @param mapJsonPath
     */
    public static void buildMapping(String mapJsonPath) {
        EsUtil.buildIndexMapping(mapJsonPath);
    }

    /**
     * insert json data to es
     * @param index
     * @param jsonFilePath
     */
    public static void insertData(String index, String jsonFilePath) {
        EsUtil.insertData2Es(index, jsonFilePath);
    }

}
