package micro_service.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Triple;
import org.elasticsearch.client.RestHighLevelClient;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Path;
import sy.es.EsSearch;
import sy.es.EsUtil;
import sy.init.es.ESPoolUtil;
import sy.neo.NeoSearch;
import sy.neo.type.EMedicalLabel;
import sy.util.KgNode;
import utils.CollectionUtil;
import utils.PropertiesReader;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sy
 * @date 2022/7/21 20:22
 */
public class SearchService {

    String INDEX = PropertiesReader.get("medical_index");

    /**
     * get disease name and id from es by query
     *
     * @param query
     * @param currentPage
     * @param size
     * @return
     */
    public List<Map<String, Object>> searchDiseaseFromEs(String query, int currentPage, int size) {
        List<Map<String, Object>> mapList = null;
        if (StringUtils.isNotBlank(query)) {
            RestHighLevelClient client = null;
            try {
                client = ESPoolUtil.borrowClient();
                if (EsUtil.existIndex(client, INDEX)) {
                    mapList = EsSearch.diseaseNameSearch(client, INDEX, query, currentPage, size);
                }
            } finally {
                if (Optional.ofNullable(client).isPresent()) {
                    ESPoolUtil.returnClient(client);
                }
            }
        }
        return mapList;
    }

    /**
     * get disease kg triples from neo4j
     *
     * @param diseaseId
     * @return
     */
    public List<Triple<KgNode, String, KgNode>> getDiseaseKgFromNeo(String diseaseId) {
        List<Triple<KgNode, String, KgNode>> diseaseTripleList = CollectionUtil.newArrayList();
        if (Optional.ofNullable(diseaseId).isPresent()) {
            List<Record> recordList = NeoSearch.getDiseaseKG(diseaseId);
            recordList.forEach(record -> {
                Value value = record.get("p");
                Path path = value.asPath();
                getNodeName(path, diseaseTripleList);
            });
        }
        return diseaseTripleList;
    }

    /**
     * get legal triples
     *
     * @param path
     * @param diseaseTripleList
     */
    private static void getNodeName(Path path, List<Triple<KgNode, String, KgNode>> diseaseTripleList) {
        String relationName = path.relationships().iterator().next().type();
        String startLabel = path.start().labels().iterator().next();
        String endLabel = path.end().labels().iterator().next();

        KgNode startNode = null;
        KgNode endNode = null;

        // start node
        Map<String, Object> startMap = path.start().asMap();
        String startId = null;
        String startName = null;

        if (StringUtils.equals(startLabel, EMedicalLabel.Disease.name())) {
            startId = startMap.get("id").toString();
            startName = startMap.get("name").toString();
            KgNode.Builder startBuilder = new KgNode.Builder(startName, startLabel).id(startId);
            startNode = startBuilder.build();
        }

        // end node
        Map<String, Object> endMap = path.end().asMap();
        String endId = null;
        String endName = null;

        if (StringUtils.equals(endLabel, EMedicalLabel.Disease.name())) {
            endId = endMap.get("id").toString();
            endName = endMap.get("name").toString();
        } else if (StringUtils.equals(endLabel, EMedicalLabel.Check.name())) {
            endName = endMap.get("name").toString();
        } else if (StringUtils.equals(endLabel, EMedicalLabel.Symptom.name())) {
            endName = endMap.get("name").toString();
        } else if (StringUtils.equals(endLabel, EMedicalLabel.Food.name())) {
            endName = endMap.get("name").toString();
        } else if (StringUtils.equals(endLabel, EMedicalLabel.Drug.name())) {
            endName = endMap.get("name").toString();
            List<Record> drugProducerRecordList = NeoSearch.getDrugProducerKG(endName);
            drugProducerRecordList.forEach(drugProducerRecord -> {
                getTriple(drugProducerRecord, diseaseTripleList);
            });
        } else if (StringUtils.equals(endLabel, EMedicalLabel.Department.name())) {
            endName = endMap.get("name").toString();
            List<Record> deptDeptRecordList = NeoSearch.getDeptDeptKG(endName);
            deptDeptRecordList.forEach(deptDeptRecord -> {
                getTriple(deptDeptRecord, diseaseTripleList);
            });
        }
        KgNode.Builder endBuilder = new KgNode.Builder(endName, endLabel);
        if(Optional.ofNullable(endId).isPresent()) {
            endBuilder.id(endId);
        }
        endNode = endBuilder.build();

        Triple<KgNode, String, KgNode> legalTriple = Triple.of(startNode, relationName, endNode);
        diseaseTripleList.add(legalTriple);
    }

    /**
     * get triple
     * @param record
     * @param tripleList
     */
    private static void getTriple(Record record, List<Triple<KgNode, String, KgNode>> tripleList) {
        Value value = record.get("p");
        Path path = value.asPath();
        String relationName = path.relationships().iterator().next().type();
        String startLabel = path.start().labels().iterator().next();
        String endLabel = path.end().labels().iterator().next();
        String startName = path.start().asMap().get("name").toString();
        String endName = path.end().asMap().get("name").toString();
        KgNode startNode = new KgNode.Builder(startName, startLabel).build();
        KgNode endNode = new KgNode.Builder(endName, endLabel).build();
        Triple<KgNode, String, KgNode> deptDeptTriple = Triple.of(startNode, relationName, endNode);
        tripleList.add(deptDeptTriple);
    }

}


