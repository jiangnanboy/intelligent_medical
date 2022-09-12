package micro_service.service;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import sy.es.EsSearch;
import sy.es.EsUtil;
import sy.init.es.ESPoolUtil;
import utils.CollectionUtil;
import utils.PropertiesReader;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author YanShi
 * @date 2022/9/12 12:28
 */
public class DiagnosisService {

    String INDEX = PropertiesReader.get("medical_index");

    public List<Map<String, Object>> getDiagnoseResult(String symptom , String sex, int age) {
       return getDiagnoseResult(symptom, sex, age, 10);
    }

    /**
     *    用户输入症状、性别、年龄后会输出相应的诊断结果：
     *
     *     1.根据用户输入的信息后，首先根据症状获取对应的疾病列表以及疾病对应的科室信息。
     *     2.根据用户的年龄做一些排除（比如如果是年轻人，则排除老年科以及儿科疾病）
     *     3.根据用户的性别做一些排除（比如是男人，则排除妇科相关的疾病）
     *     4.对剩余的疾病列表根据症状词的匹配度得出最后的诊断疾病列表。
     *
     *      old > 60
     *      child < 14
     *
     * @param symptom 症状
     * @param sex 性别
     * @param age 年龄
     * @param size 返回结果数
     * @returnD
     */
    public List<Map<String, Object>> getDiagnoseResult(String symptom, String sex, int age, int size) {
        RestHighLevelClient client = null;
        List<Map<String, Object>> resultList = CollectionUtil.newArrayList();

        try {
            client = ESPoolUtil.borrowClient();
            if (EsUtil.existIndex(client, INDEX)) {
                List<Map<String, Object>> queryList = EsSearch.searchBySymptom(client, INDEX, symptom, 2 * size);
                for(Map<String, Object> entry : queryList) {
                    String department = entry.get("cure_department").toString();

                    if((age >= 14) && (age <= 60)) {
                        if(department.contains("儿科") || department.contains("老年科")) {
                            continue;
                        }
                    }

                    if(StringUtils.equals(sex, "男")) {
                        if(department.contains("妇科") || department.contains("产科") || department.contains("妇产科") || department.contains("乳腺外科")) {
                            continue;
                        }
                    }

                    if(StringUtils.equals(sex, "女")) {
                        if(department.contains("男科")) {
                            continue;
                        }
                    }

                    Map<String, Object> result = CollectionUtil.newLinkedHashMap();
                    result.put("疾病名", entry.get("name"));
                    result.put("疾病描述", entry.get("desc"));
                    result.put("症状", entry.get("symptom"));
                    result.put("易感人群", entry.get("easyGet"));
                    result.put("建议检查", entry.get("check"));

                    result.put("所属科室", entry.get("cure_department"));
                    result.put("造成原因", entry.get("cause"));
                    result.put("伴随症状", entry.get("acompany"));
                    result.put("治疗手段", entry.get("cure_way"));

                    result.put("预防", entry.get("prevent"));
                    result.put("推荐药物", entry.get("recommand_drug"));
                    result.put("易吃", entry.get("recommand_eat"));
                    result.put("不易吃", entry.get("not_eat"));
                    result.put("推荐食物", entry.get("recommand_eat"));

                    resultList.add(result);
                }
            }
        } finally {
            if (Optional.ofNullable(client).isPresent()) {
                ESPoolUtil.returnClient(client);
            }
        }

        if(resultList.size() > size) {
            return resultList.subList(0, size);
        } else {
            return resultList;
        }
    }
}
