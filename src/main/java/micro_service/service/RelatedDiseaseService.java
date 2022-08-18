package micro_service.service;

import org.neo4j.driver.v1.Record;
import sy.neo.NeoSearch;
import utils.CollectionUtil;

import java.util.List;
import java.util.Map;

/**
 * @author YanShi
 * @date 2022/8/13 14:45
 */
public class RelatedDiseaseService {
    public List<Map<String, Object>> getRelatedDisease(String id, String query, int size) {
        List<Record> recordList = NeoSearch.getRelatedDisease(id, query, size);
        List<Map<String, Object>> mapNode = CollectionUtil.newArrayList();
        for(Record record : recordList) {
            Map<String, Object> map = record.asMap();
            mapNode.add(map);
        }
        return mapNode;
    }
}
