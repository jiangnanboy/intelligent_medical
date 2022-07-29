package sy.qa;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.neo4j.driver.v1.Record;
import sy.es.EsSearch;
import sy.es.EsUtil;
import sy.init.es.ESPoolUtil;
import sy.neo.NeoSearch;
import utils.CollectionUtil;
import utils.PropertiesReader;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author sy
 * @date 2022/7/29 19:40
 */
public class AnswerSearcher {

    public List<String> search(List<Map<String, Object>> cypherMapList) {
        List<String> answerResults = CollectionUtil.newArrayList();
        for(Map<String, Object> map : cypherMapList) {
            String questionType = (String) map.get("question_type");
            List<String> cypherList = (List<String>) map.get("cypher");
            List<Record> answersList = CollectionUtil.newArrayList();
            for(String cypher : cypherList) {
                List<Record> recordList = NeoSearch.runGetCypher(cypher);
                answersList.addAll(recordList);
            }
            String answerResult = this.answerTemplate(questionType, answersList);
            if(StringUtils.isNotBlank(answerResult)) {
                answerResults.add(answerResult);
            }
        }
        return answerResults;
    }

    private String answerTemplate(String questionType, List<Record> recordList) {
        String answerResult = "";
        if((null == recordList) || (0 == recordList.size())) {
            return answerResult;
        }
        if(StringUtils.equals(questionType, "disease_symptom")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                descSet.add(recordMap.get("n.name").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject + "的症状包括：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "symptom_disease")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                descSet.add(recordMap.get("m.name").toString());
            }
            String subject = recordList.get(0).asMap().get("n.name").toString();
            answerResult = "症状" + subject +"可能染上的疾病有：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "disease_cause")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                String id = recordMap.get("m.id").toString();
                // 利用id从es中获取cause
                Map<String, Object> mapResult = this.getOneDocMap(id);
                if(null == mapResult) {
                    return answerResult;
                }
                descSet.add(mapResult.get("cause").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject +"可能的成因有：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "disease_prevent")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                String id = recordMap.get("m.id").toString();
                // 利用id从es中获取prevent
                Map<String, Object> mapResult = this.getOneDocMap(id);
                if(null == mapResult) {
                    return answerResult;
                }
                descSet.add(mapResult.get("prevent").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject +"的预防措施包括：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "disease_lasttime")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                String id = recordMap.get("m.id").toString();
                // 利用id从es中获取lasttime
                Map<String, Object> mapResult = this.getOneDocMap(id);
                if(null == mapResult) {
                    return answerResult;
                }
                descSet.add(mapResult.get("cure_lasttime").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject +"治疗可能持续的周期为：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "disease_cureway")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                String id = recordMap.get("m.id").toString();
                // 利用id从es中获取cureway
                Map<String, Object> mapResult = this.getOneDocMap(id);
                if(null == mapResult) {
                    return answerResult;
                }
                descSet.add(mapResult.get("cure_way").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject +"可以尝试如下治疗：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "disease_cureprob")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                String id = recordMap.get("m.id").toString();
                // 利用id从es中获取cure prob
                Map<String, Object> mapResult = this.getOneDocMap(id);
                if(null == mapResult) {
                    return answerResult;
                }
                descSet.add(mapResult.get("cured_prob").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject +"治愈的概率为（仅供参考）：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "disease_easyget")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                String id = recordMap.get("m.id").toString();
                // 利用id从es中获取easy get
                Map<String, Object> mapResult = this.getOneDocMap(id);
                if(null == mapResult) {
                    return answerResult;
                }
                descSet.add(mapResult.get("easyGet").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject +"的易感人群包括：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "disease_desc")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                String id = recordMap.get("m.id").toString();
                // 利用id从es中获取desc
                Map<String, Object> mapResult = this.getOneDocMap(id);
                if(null == mapResult) {
                    return answerResult;
                }
                descSet.add(mapResult.get("desc").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject + "，熟悉一下：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "disease_acompany")) {
            Set<String> descSet1 = CollectionUtil.newHashset();
            Set<String> descSet2 = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                descSet1.add(recordMap.get("n.name").toString());
                descSet2.add(recordMap.get("m.name").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            Set<String> descSet = CollectionUtil.newHashset();
            descSet.addAll(descSet1);
            descSet.addAll(descSet2);
            if(descSet.contains(subject)) {
                descSet.remove(subject);
            }
            answerResult = subject + "的症状包括：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "disease_not_food")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                descSet.add(recordMap.get("n.name").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject + "忌食的食物包括有：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "disease_do_food")) {
            Set<String> descSet = CollectionUtil.newHashset();
            Set<String> recommandSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                if(StringUtils.equals(recordMap.get("r.name").toString(), "宜吃")) {
                    descSet.add(recordMap.get("n.name").toString());
                }
                if(StringUtils.equals(recordMap.get("r.name").toString(), "推荐食谱")) {
                    recommandSet.add(recordMap.get("n.name").toString());
                }
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject + "宜食的食物包括有：" + String.join(";", descSet);
            answerResult += "\n推荐食谱包括有:" + String.join(";", recommandSet);
        } else if(StringUtils.equals(questionType, "food_not_disease")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                descSet.add(recordMap.get("m.name").toString());
            }
            String subject = recordList.get(0).asMap().get("n.name").toString();
            answerResult = "患有" +  String.join(";", descSet) + "的人最好不要吃：" + subject;
        } else if(StringUtils.equals(questionType, "food_do_disease")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                descSet.add(recordMap.get("m.name").toString());
            }
            String subject = recordList.get(0).asMap().get("n.name").toString();
            answerResult = "患有" +  String.join(";", descSet) + "的人建议多试试：" + subject;
        } else if(StringUtils.equals(questionType, "disease_drug")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                descSet.add(recordMap.get("n.name").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject + "通常的使用的药品包括：" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "drug_disease")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                descSet.add(recordMap.get("m.name").toString());
            }
            String subject = recordList.get(0).asMap().get("n.name").toString();
            answerResult = subject + "主治的疾病有" + String.join(";", descSet) + "，可以试试";
        } else if(StringUtils.equals(questionType, "disease_check")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                descSet.add(recordMap.get("n.name").toString());
            }
            String subject = recordList.get(0).asMap().get("m.name").toString();
            answerResult = subject + "通常可以通过以下方式检查出来:" + String.join(";", descSet);
        } else if(StringUtils.equals(questionType, "check_disease")) {
            Set<String> descSet = CollectionUtil.newHashset();
            for(Record record : recordList) {
                Map<String, Object> recordMap = record.asMap();
                descSet.add(recordMap.get("m.name").toString());
            }
            String subject = recordList.get(0).asMap().get("n.name").toString();
            answerResult = "通常可以通过" + subject + "检查出来的疾病有:" + String.join(";", descSet);
        }
        return answerResult;
    }

    /**
     * get one doc map
     * @param id
     * @return
     */
    private Map<String, Object> getOneDocMap(String id) {
        Map<String, Object> mapResult = null;
        if (StringUtils.isNotBlank(id)) {
            RestHighLevelClient client = null;
            String INDEX = PropertiesReader.get("medical_index");
            try {
                client = ESPoolUtil.borrowClient();
                if (EsUtil.existIndex(client, INDEX)) {
                    mapResult = EsSearch.getOneDocById(client, INDEX, id);
                }
            } finally {
                if (Optional.ofNullable(client).isPresent()) {
                    ESPoolUtil.returnClient(client);
                }
            }
        }
        return mapResult;
    }

}


