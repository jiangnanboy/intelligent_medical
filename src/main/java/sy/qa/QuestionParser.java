package sy.qa;

import org.apache.commons.lang3.StringUtils;
import sy.neo.type.EMedicalLabel;
import sy.neo.type.EMedicalRel;
import utils.CollectionUtil;

import java.util.List;
import java.util.Map;

/**
 * @author sy
 * @date 2022/7/29 19:40
 */
public class QuestionParser {

    public List<Map<String, Object>> parser(Map<String, Object> classifiyMap) {
        Map<String, List<String>> medicalDict = (Map<String, List<String>>) classifiyMap.get("args");
        Map<String, List<String>> entityDict = this.buildEntityDict(medicalDict);
        List<String> questionTypeList = (List<String>) classifiyMap.get("question_types");
        List<Map<String, Object>> cypherMapList = CollectionUtil.newArrayList();
        for(String questionType : questionTypeList) {
            Map<String, Object> cypherMap = CollectionUtil.newHashMap();
            cypherMap.put("question_type", questionType);
            List<String> cypherList = CollectionUtil.newArrayList();
            if(StringUtils.equals(questionType, "disease_symptom")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "symptom_disease")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("symptom"));
            } else if(StringUtils.equals(questionType, "disease_cause")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "disease_acompany")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "disease_not_food")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "disease_do_food")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "food_not_disease")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("food"));
            } else if(StringUtils.equals(questionType, "food_do_disease")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("food"));
            } else if(StringUtils.equals(questionType, "disease_drug")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "drug_disease")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("drug"));
            } else if(StringUtils.equals(questionType, "disease_check")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "check_disease")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("check"));
            } else if(StringUtils.equals(questionType, "disease_prevent")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "disease_lasttime")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "disease_cureway")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "disease_cureprob")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "disease_easyget")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            } else if(StringUtils.equals(questionType, "disease_desc")) {
                cypherList = this.cypherBuild(questionType, entityDict.get("disease"));
            }
            if(0 != cypherList.size()) {
                cypherMap.put("cypher", cypherList);
                cypherMapList.add(cypherMap);
            }
        }

        return cypherMapList;
    }

    /**
     * build cypher
     * @param questionType
     * @param entityList
     * @return
     */
    private List<String> cypherBuild(String questionType, List<String> entityList) {
        if((null == entityList) || (0 == entityList.size())) {
            return CollectionUtil.newArrayList();
        }
        List<String> cypherList = CollectionUtil.newArrayList();
        // 查询疾病的原因
        if(StringUtils.equals(questionType, "disease_cause")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ") where m.name = '" + entity + "' return m.name, m.id";
                cypherList.add(cypher);
            }
        }

        // 查询疾病的防御措施
        if(StringUtils.equals(questionType, "disease_prevent")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ") where m.name = '" + entity + "' return m.name, m.id";
                cypherList.add(cypher);
            }
        }

        // 查询疾病的持续时间
        if(StringUtils.equals(questionType, "disease_lasttime")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ") where m.name = '" + entity + "' return m.name, m.id";
                cypherList.add(cypher);
            }
        }

        // 查询疾病的治愈概率
        if(StringUtils.equals(questionType, "disease_cureprob")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ") where m.name = '" + entity + "' return m.name, m.id";
                cypherList.add(cypher);
            }
        }

        // 查询疾病的治疗方式
        if(StringUtils.equals(questionType, "disease_cureway")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ") where m.name = '" + entity + "' return m.name, m.id";
                cypherList.add(cypher);
            }
        }

        // 查询疾病的易发人群
        if(StringUtils.equals(questionType, "disease_easyget")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ") where m.name = '" + entity + "' return m.name, m.id";
                cypherList.add(cypher);
            }
        }

        // 查询疾病的相关介绍
        if(StringUtils.equals(questionType, "disease_desc")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ") where m.name = '" + entity + "' return m.name, m.id";
                cypherList.add(cypher);
            }
        }

        // 查询疾病有哪些症状
        if(StringUtils.equals(questionType, "disease_symptom")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.HAS_SYMPTOM.name() + "]->(n:" + EMedicalLabel.Symptom.name() + ") where m.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }

        // 查询症状会导致哪些疾病
        if(StringUtils.equals(questionType, "symptom_disease")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.HAS_SYMPTOM.name() + "]->(n:" + EMedicalLabel.Symptom.name() + ") where n.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }

        // 查询疾病的并发症
        if(StringUtils.equals(questionType, "disease_acompany")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.ACOMPANY_WITH.name() + "]->(n:" + EMedicalLabel.Disease.name() + ") where m.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.ACOMPANY_WITH.name() + "]->(n:" + EMedicalLabel.Disease.name() + ") where n.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }

        // 查询疾病的忌口
        if(StringUtils.equals(questionType, "disease_not_food")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.NO_EAT.name() + "]->(n:" + EMedicalLabel.Food.name() + ") where m.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }


        // 查询疾病建议吃的东西
        if(StringUtils.equals(questionType, "disease_do_food")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.DO_EAT.name() + "]->(n:" + EMedicalLabel.Food.name() + ") where m.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.RECOMMAND_EAT.name() + "]->(n:" + EMedicalLabel.Food.name() + ") where m.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }

        // 已知忌口查疾病
        if(StringUtils.equals(questionType, "food_not_disease")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.NO_EAT.name() + "]->(n:" + EMedicalLabel.Food.name() + ") where n.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }

        // 已知推荐食谱查疾病
        if(StringUtils.equals(questionType, "food_do_disease")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.DO_EAT.name() + "]->(n:" + EMedicalLabel.Food.name() + ") where n.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.RECOMMAND_EAT.name() + "]->(n:" + EMedicalLabel.Food.name() + ") where n.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }

        // 查询疾病常用药品－药品别名记得扩充
        if(StringUtils.equals(questionType, "disease_drug")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.COMMON_DRUG.name() + "]->(n:" + EMedicalLabel.Drug.name() + ") where m.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.RECOMMAND_DRUG.name() + "]->(n:" + EMedicalLabel.Drug.name() + ") where m.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }

        // 已知药品查询能够治疗的疾病
        if(StringUtils.equals(questionType, "drug_disease")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.COMMON_DRUG.name() + "]->(n:" + EMedicalLabel.Drug.name() + ") where n.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.RECOMMAND_DRUG.name() + "]->(n:" + EMedicalLabel.Drug.name() + ") where n.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }


        // 查询疾病应该进行的检查
        if(StringUtils.equals(questionType, "disease_check")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.NEED_CHECK.name() + "]->(n:" + EMedicalLabel.Check.name() + ") where m.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }

        // 已知检查查询疾病
        if(StringUtils.equals(questionType, "check_disease")) {
            String cypher;
            for(String entity : entityList) {
                cypher = "match (m:" + EMedicalLabel.Disease.name() + ")-[r:" + EMedicalRel.NEED_CHECK.name() + "]->(n:" + EMedicalLabel.Check.name() + ") where n.name = '" + entity + "' return m.name, r.name, n.name";
                cypherList.add(cypher);
            }
        }

        return cypherList;
    }

    private Map<String, List<String>> buildEntityDict(Map<String, List<String>> medicalDict) {
        Map<String, List<String>> entityDict = CollectionUtil.newHashMap();
        for(Map.Entry<String, List<String>> entry : medicalDict.entrySet()) {
            for(String type : entry.getValue()) {
                if(!entityDict.containsKey(type)) {
                    List<String> types = CollectionUtil.newArrayList();
                    types.add(entry.getKey());
                    entityDict.put(type, types);
                } else {
                    entityDict.get(type).add(entry.getKey());
                }
            }
        }
        return entityDict;
    }

}

