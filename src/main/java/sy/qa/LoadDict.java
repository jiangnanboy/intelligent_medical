package sy.qa;

import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.apache.commons.lang3.StringUtils;
import utils.CollectionUtil;
import utils.PropertiesReader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author sy
 * @date 2022/7/29 19:40
 */
public class LoadDict {
    
    static Set<String> diseaseSet;
    static Set<String> departmentSet;
    static Set<String> checkSet;
    static Set<String> drugSet;
    static Set<String> foodSet;
    static Set<String> producerSet;
    static Set<String> symptomSet;
    static Set<String> denySet;
    static Set<String> regionSet;

    static Trie regionTrie;
    static Map<String, List<String>> medicalFeatureWordsType;
    
    // question words list
    static List<String> symptomQuestionWordList;
    static List<String> causeQuestionWordList;
    static List<String> acompanyQuestionWordList;
    static List<String> foodQuestionWordList;
    static List<String> drugQuestionWordList;
    static List<String> preventQuestionWordList;
    static List<String> lasttimeQuestionWordList;
    static List<String> curewayQuestionWordList;
    static List<String> cureprobQuestionWordList;
    static List<String> easygetQuestionWordList;
    static List<String> checkQuestionWordList;
    static List<String> belongQuestionWordList;
    static List<String> cureQuestionWordList;

    static {
        initLoad();
    }

    /**
     * init load
     */
    public static void initLoad() {
        System.out.println("load dict ...");
        String checkDictPath = QuestionClassifier.class.getClassLoader().getResource(PropertiesReader.get("medical_check_dict")).getPath().replaceFirst("/", "");
        String denyDcitPath = QuestionClassifier.class.getClassLoader().getResource(PropertiesReader.get("medical_deny_dcit")).getPath().replaceFirst("/", "");
        String departmentDictPath = QuestionClassifier.class.getClassLoader().getResource(PropertiesReader.get("medical_department_dict")).getPath().replaceFirst("/", "");
        String diseaseDictPath = QuestionClassifier.class.getClassLoader().getResource(PropertiesReader.get("medical_disease_dict")).getPath().replaceFirst("/", "");
        String drugDictPath = QuestionClassifier.class.getClassLoader().getResource(PropertiesReader.get("medical_drug_dict")).getPath().replaceFirst("/", "");
        String foodDictPath = QuestionClassifier.class.getClassLoader().getResource(PropertiesReader.get("medical_food_dict")).getPath().replaceFirst("/", "");
        String producerDictPath = QuestionClassifier.class.getClassLoader().getResource(PropertiesReader.get("medical_producer_dict")).getPath().replaceFirst("/", "");
        String symptomDictPath = QuestionClassifier.class.getClassLoader().getResource(PropertiesReader.get("medical_symptom_dict")).getPath().replaceFirst("/", "");

        // load medical feature words
        diseaseSet = loadDict(diseaseDictPath);
        departmentSet = loadDict(departmentDictPath);
        checkSet = loadDict(checkDictPath);
        drugSet = loadDict(drugDictPath);
        foodSet = loadDict(foodDictPath);
        producerSet = loadDict(producerDictPath);
        symptomSet = loadDict(symptomDictPath);
        denySet = loadDict(denyDcitPath);

        regionSet = CollectionUtil.newHashset();
        regionSet.addAll(diseaseSet);
        regionSet.addAll(departmentSet);
        regionSet.addAll(checkSet);
        regionSet.addAll(drugSet);
        regionSet.addAll(foodSet);
        regionSet.addAll(producerSet);
        regionSet.addAll(symptomSet);

        // build actree
        regionTrie = buildACTree();

        // build types of medical feature words
        medicalFeatureWordsType = buildMedicalFeatureWordsTypes();

        //  question words
        symptomQuestionWordList = Arrays.asList(new String[]{"症状", "表征", "现象", "症候", "表现"});
        causeQuestionWordList = Arrays.asList(new String[]{"原因","成因", "为什么", "怎么会", "怎样才", "咋样才", "怎样会", "如何会", "为啥", "为何", "如何才会", "怎么才会", "会导致", "会造成"});
        acompanyQuestionWordList = Arrays.asList(new String[]{"并发症", "并发", "一起发生", "一并发生", "一起出现", "一并出现", "一同发生", "一同出现", "伴随发生", "伴随", "共现"});
        foodQuestionWordList = Arrays.asList(new String[]{"饮食", "饮用", "吃", "食", "伙食", "膳食", "喝", "菜" ,"忌口", "补品", "保健品", "食谱", "菜谱", "食用", "食物","补品"});
        drugQuestionWordList = Arrays.asList(new String[]{"药", "药品", "用药", "胶囊", "口服液", "炎片"});
        preventQuestionWordList = Arrays.asList(new String[]{"预防", "防范", "抵制", "抵御", "防止","躲避","逃避","避开","免得","逃开","避开","避掉","躲开","躲掉","绕开",
                                                            "怎样才能不", "怎么才能不", "咋样才能不","咋才能不", "如何才能不",
                                                            "怎样才不", "怎么才不", "咋样才不","咋才不", "如何才不",
                                                            "怎样才可以不", "怎么才可以不", "咋样才可以不", "咋才可以不", "如何可以不",
                                                            "怎样才可不", "怎么才可不", "咋样才可不", "咋才可不", "如何可不"});
        lasttimeQuestionWordList = Arrays.asList(new String[]{"周期", "多久", "多长时间", "多少时间", "几天", "几年", "多少天", "多少小时", "几个小时", "多少年"});
        curewayQuestionWordList = Arrays.asList(new String[]{"怎么治疗", "如何医治", "怎么医治", "怎么治", "怎么医", "如何治", "医治方式", "疗法", "咋治", "怎么办", "咋办", "咋治"});
        cureprobQuestionWordList = Arrays.asList(new String[]{"多大概率能治好", "多大几率能治好", "治好希望大么", "几率", "几成", "比例", "可能性", "能治", "可治", "可以治", "可以医"});
        easygetQuestionWordList = Arrays.asList(new String[]{"易感人群", "容易感染", "易发人群", "什么人", "哪些人", "感染", "染上", "得上"});
        checkQuestionWordList = Arrays.asList(new String[]{"检查", "检查项目", "查出", "检查", "测出", "试出"});
        belongQuestionWordList = Arrays.asList(new String[]{"属于什么科", "属于", "什么科", "科室"});
        cureQuestionWordList = Arrays.asList(new String[]{"治疗什么", "治啥", "治疗啥", "医治啥", "治愈啥", "主治啥", "主治什么", "有什么用", "有何用", "用处", "用途",
                "有什么好处", "有什么益处", "有何益处", "用来", "用来做啥", "用来作甚", "需要", "要"});

    }

    /**
     * build types of medical feature words
     * @return
     */
    private static Map<String, List<String>> buildMedicalFeatureWordsTypes() {
        Map<String, List<String>> wordTypesDict = CollectionUtil.newHashMap();
        for(String word : regionSet) {
            List<String> wordTypeList = CollectionUtil.newArrayList();
            wordTypesDict.put(word, wordTypeList);
            if(diseaseSet.contains(word)) {
                wordTypesDict.get(word).add("disease");
            }
            if(departmentSet.contains(word)) {
                wordTypesDict.get(word).add("department");
            }
            if(checkSet.contains(word)) {
                wordTypesDict.get(word).add("check");
            }
            if(drugSet.contains(word)) {
                wordTypesDict.get(word).add("drug");
            }
            if(foodSet.contains(word)) {
                wordTypesDict.get(word).add("food");
            }
            if(symptomSet.contains(word)) {
                wordTypesDict.get(word).add("symptom");
            }
            if(producerSet.contains(word)) {
                wordTypesDict.get(word).add("producer");
            }
        }
        return wordTypesDict;
    }

    private static Trie buildACTree() {
        Trie.TrieBuilder trieBuilder = Trie.builder();
        trieBuilder.onlyWholeWords();
        List<String> list = CollectionUtil.newArrayList(regionSet);
        trieBuilder.addKeywords(list);
        return trieBuilder.build();
    }

    public static List<String> maxMatch(String sentence, int maxMatchLen) {

        List<String> wordsList = CollectionUtil.newArrayList();
        int start = 0;
        int sentLen = sentence.length();
        while(start < sentLen) {
            for(int i = maxMatchLen; i > 0; i--) {
                String maxStr;
                if((start + i) > sentLen) {
                    maxStr = sentence.substring(start);
                } else {
                    maxStr = sentence.substring(start, start + i);
                }

                Emit emit = LoadDict.regionTrie.firstMatch(maxStr);
                if(Optional.ofNullable(emit).isPresent()) {
                    wordsList.add(emit.getKeyword());
                    start += maxStr.length() - 1;
                    break;
                }
            }
            start += 1;
        }

        return wordsList;
    }

    /**
     * load medical dict
     * @param path
     * @return
     */
    private static Set<String> loadDict(String path) {
        Set<String> dictSet = CollectionUtil.newHashset();
        try(Stream<String> stream = Files.lines(Paths.get(path), StandardCharsets.UTF_8)) {
            dictSet = stream.filter(line -> StringUtils.isNotBlank(line)).map(line -> line.trim()).collect(Collectors.toSet());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dictSet;
    }
}

