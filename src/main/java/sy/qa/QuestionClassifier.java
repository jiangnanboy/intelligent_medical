package sy.qa;

import org.apache.commons.lang3.StringUtils;
import utils.CollectionUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.ahocorasick.trie.Emit;

/**
 * @author sy
 * @date 2022/7/29 19:40
 */
public class QuestionClassifier {

    public Map<String, Object> classify(String sent) {
        Map<String, Object> dataMap = CollectionUtil.newHashMap();
        Map<String, List<String>> medicalDict = this.checkMedical(sent);

        if((null == medicalDict) || (0 == medicalDict.size())) {
            return dataMap;
        }
        dataMap.put("args", medicalDict);

        // 收集问句中的实体类型
        List<String> typeList = CollectionUtil.newArrayList();
        for(List<String> type : medicalDict.values()) {
            typeList.addAll(type);
        }

        String questionType = "others";
        List<String> questionTypeList = CollectionUtil.newArrayList();
        // symptom
        if(this.checkWords(LoadDict.symptomQuestionWordList, sent) && typeList.contains("disease")) {
            questionType = "disease_symptom";
            questionTypeList.add(questionType);
        }

        if(this.checkWords(LoadDict.symptomQuestionWordList, sent) && typeList.contains("symptom")) {
            questionType = "symptom_disease";
            questionTypeList.add(questionType);
        }
        //reason
        if(this.checkWords(LoadDict.causeQuestionWordList, sent) && typeList.contains("disease")) {
            questionType = "disease_cause";
            questionTypeList.add(questionType);
        }

        //acompany
        if(this.checkWords(LoadDict.acompanyQuestionWordList, sent) && typeList.contains("disease")) {
            questionType = "disease_acompany";
            questionTypeList.add(questionType);
        }

        // food
        if(this.checkWords(LoadDict.foodQuestionWordList, sent) && typeList.contains("disease")) {
            boolean denyStatus = this.checkWords(CollectionUtil.newArrayList(LoadDict.denySet), sent);
            if(denyStatus) {
                questionType = "disease_not_food";
            } else {
                questionType = "disease_do_food";
            }
            questionTypeList.add(questionType);
        }

        //food-disease
        List<String> foodCureList = CollectionUtil.newArrayList();
        foodCureList.addAll(LoadDict.foodQuestionWordList);
        foodCureList.addAll(LoadDict.cureQuestionWordList);
        if(this.checkWords(foodCureList, sent) && typeList.contains("food")) {
            boolean denyStatus = this.checkWords(CollectionUtil.newArrayList(LoadDict.denySet), sent);
            if(denyStatus) {
                questionType = "food_not_disease";
            } else {
                questionType = "food_do_disease";
            }
            questionTypeList.add(questionType);
        }

        // drug
        if(this.checkWords(LoadDict.drugQuestionWordList, sent) && typeList.contains("disease")) {
            questionType = "disease_drug";
            questionTypeList.add(questionType);
        }

        //drug-disease
        if(this.checkWords(LoadDict.cureQuestionWordList, sent) && typeList.contains("drug")) {
            questionType = "drug_disease";
            questionTypeList.add(questionType);
        }

        //disease-check
        if(this.checkWords(LoadDict.checkQuestionWordList, sent) && typeList.contains("disease")) {
            questionType = "disease_check";
            questionTypeList.add(questionType);
        }

        // check-disease
        List<String> checkCureList = CollectionUtil.newArrayList();
        checkCureList.addAll(LoadDict.checkQuestionWordList);
        checkCureList.addAll(LoadDict.cureQuestionWordList);
        if(this.checkWords(checkCureList, sent) && typeList.contains("check")) {
            questionType = "check_disease";
            questionTypeList.add(questionType);
        }

        //disease-prevent
        if(this.checkWords(LoadDict.preventQuestionWordList, sent) && typeList.contains("disease")) {
            questionType = "disease_prevent";
            questionTypeList.add(questionType);
        }

        //disease-curea-lasttime
        if(this.checkWords(LoadDict.lasttimeQuestionWordList, sent) && typeList.contains("disease")) {
            questionType = "disease_lasttime";
            questionTypeList.add(questionType);
        }

        // disease-curewaw
        if(this.checkWords(LoadDict.curewayQuestionWordList, sent) && typeList.contains("disease")) {
            questionType = "disease_cureway";
            questionTypeList.add(questionType);
        }

        //disease-cureprob
        if(this.checkWords(LoadDict.cureprobQuestionWordList, sent) && typeList.contains("disease")) {
            questionType = "disease_cureprob";
            questionTypeList.add(questionType);
        }

        //disease-easyget
        if(this.checkWords(LoadDict.easygetQuestionWordList, sent) && typeList.contains("disease")) {
            questionType = "disease_easyget";
            questionTypeList.add(questionType);
        }

        // questionTypeList.size = 0
        if((questionTypeList.size() == 0) && typeList.contains("disease")) {
            questionTypeList.add("disease_desc");
        }

        if((questionTypeList.size() == 0) && typeList.contains("symptom")) {
            questionTypeList.add("symptom_disease");
        }

        dataMap.put("question_types", questionTypeList);

        return dataMap;
    }

    private boolean checkWords(List<String> wordList, String sent) {
        for(String word : wordList) {
            if(sent.contains(word.trim())) {
                return true;
            }
        }
        return false;
    }

    public Map<String, List<String>> checkMedical(String sent) {
        List<String> regionWordList = LoadDict.maxMatch(sent, 25);
        Map<String, List<String>> checkResultMap = regionWordList.stream().map(word ->
            new Object[]{word, LoadDict.medicalFeatureWordsType.get(word)}
        ).collect(Collectors.toMap(data -> (String)data[0], data-> (List<String>)data[1], (oldValue, newValue) -> newValue));
        return checkResultMap;
    }


}


