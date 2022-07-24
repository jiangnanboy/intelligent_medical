package sy.process.proc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import utils.CollectionUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class NeoProcessData {
    /**
     * 处理json数据
     * @param jsonPath
     */
    public static void process(String jsonPath, String[] nodePathStr, String[] relPathStr) {
        // node path
        String medical_check_path = nodePathStr[0];
        String medical_department_path = nodePathStr[1];
        String medical_disease_path = nodePathStr[2];
        String medical_drug_path = nodePathStr[3];
        String medical_food_path = nodePathStr[4];
        String medical_producer_path = nodePathStr[5];
        String medical_symptom_path = nodePathStr[6];

        // rel path
        String medical_recommand_eat_path = relPathStr[0];
        String medical_belongs_to_path = relPathStr[1];
        String medical_common_drug_path = relPathStr[2];
        String medical_do_eat_path = relPathStr[3];
        String medical_drugs_of_path = relPathStr[4];
        String medical_need_check_path = relPathStr[5];
        String medical_no_eat_path = relPathStr[6];
        String medical_recommand_drug_path = relPathStr[7];
        String medical_has_symptom_path = relPathStr[8];
        String medical_acompany_with_path = relPathStr[9];
        String medical_dept_path = relPathStr[10];

        // nodes, 7大类
        Set<String> drugNodeSet = CollectionUtil.newHashset();
        Set<String> foodNodeSet = CollectionUtil.newHashset();
        Set<String> checkNodeSet = CollectionUtil.newHashset();
        Set<String> departmentNodeSet = CollectionUtil.newHashset();
        Set<String> producerNodeSet = CollectionUtil.newHashset();
        Set<String> diseaseNodeSet = CollectionUtil.newHashset();
        Set<String> symptomNodeSet = CollectionUtil.newHashset();

        // rels, 10大关系
        Set<String> departmentRelDepartmentSet = CollectionUtil.newHashset();
        Set<String> diseaseNotEatRelSet = CollectionUtil.newHashset();
        Set<String> diseaseDoEatRelSet = CollectionUtil.newHashset();
        Set<String> diseaseRecommandEatRelSet = CollectionUtil.newHashset();
        Set<String> diseaseCommondDrugRelSet = CollectionUtil.newHashset();
        Set<String> diseaseRecommandDrugRelSet = CollectionUtil.newHashset();
        Set<String> diseaseCheckRelSet = CollectionUtil.newHashset();
        Set<String> drugProducerRelSet = CollectionUtil.newHashset();
        Set<String> diseaseSymptomRelSet = CollectionUtil.newHashset();
        Set<String> diseaseAcompanyRelSet = CollectionUtil.newHashset();
        Set<String> diseaseCategoryRelSet = CollectionUtil.newHashset();

        try(BufferedReader br = Files.newBufferedReader(Paths.get(jsonPath), StandardCharsets.UTF_8)) {
            String line = null;
            while (null != (line = br.readLine())) {
                JSONObject jsonObject = JSON.parseObject(line);

                Map<String, String> idMap = (Map<String, String>) jsonObject.get("_id");
                String id = idMap.get("$oid");
                String name = jsonObject.getString("name");
                diseaseNodeSet.add(id + "," + name);
                List<String> symptomList = (List<String>) jsonObject.get("symptom");
                List<String> acompanyList = (List<String>) jsonObject.get("acompany");
                String desc = jsonObject.getString("desc");
                String prevent = jsonObject.getString("prevent");
                String cause = jsonObject.getString("cause");
                String getProb = jsonObject.getString("get_prob");
                String easyGet = jsonObject.getString("easy_get");
                List<String> cureDepartmentList = (List<String>) jsonObject.get("cure_department");
                List<String> cureWayList = (List<String>) jsonObject.get("cure_way");
                String cureLasttime = jsonObject.getString("cure_lasttime");
                String curedProb = jsonObject.getString("cured_prob");
                List<String> commonDrugList = (List<String>) jsonObject.get("common_drug");
                List<String> recommandDrugList = (List<String>) jsonObject.get("recommand_drug");
                List<String> notEatList = (List<String>) jsonObject.get("not_eat");
                List<String> doEatList = (List<String>) jsonObject.get("do_eat");
                List<String> recommandEatList = (List<String>) jsonObject.get("recommand_eat");
                List<String> checkList = (List<String>) jsonObject.get("check");
                List<String> drugDetailList = (List<String>) jsonObject.get("drug_detail");

                if(Optional.ofNullable(symptomList).isPresent() && (symptomList.size() != 0)) {
                    symptomNodeSet.addAll(symptomList);
                    for(String symptom : symptomList) {
                        diseaseSymptomRelSet.add(name + "," + symptom);
                    }
                }

                if(Optional.ofNullable(acompanyList).isPresent() && (acompanyList.size() != 0)) {
                    for(String acompany : acompanyList) {
                        diseaseAcompanyRelSet.add(name + "," + acompany);
                    }
                }

                if(Optional.ofNullable(cureDepartmentList).isPresent() && (cureDepartmentList.size() != 0)) {
                    if(cureDepartmentList.size() == 1) {
                        diseaseCategoryRelSet.add(name + "," + cureDepartmentList.get(0));
                    }
                    if(cureDepartmentList.size() == 2) {
                        String big = cureDepartmentList.get(0);
                        String small = cureDepartmentList.get(1);
                        departmentRelDepartmentSet.add(small + "," + big);
                        diseaseCategoryRelSet.add(name + "," + small);
                    }
                    departmentNodeSet.addAll(cureDepartmentList);
                }

                if(Optional.ofNullable(commonDrugList).isPresent() && (commonDrugList.size() != 0)) {
                    for(String drug : commonDrugList) {
                        diseaseCommondDrugRelSet.add(name + "," + drug);
                    }
                    drugNodeSet.addAll(commonDrugList);
                }

                if(Optional.ofNullable(recommandDrugList).isPresent() && (recommandDrugList.size() != 0)) {
                    for(String drug : recommandDrugList) {
                        diseaseRecommandDrugRelSet.add(name + "," + drug);
                    }
                    drugNodeSet.addAll(recommandDrugList);
                }

                if(Optional.ofNullable(notEatList).isPresent() && (notEatList.size() != 0)) {
                    for(String notEat : notEatList) {
                        diseaseNotEatRelSet.add(name + "," + notEat);
                    }
                    foodNodeSet.addAll(notEatList);
                }

                if(Optional.ofNullable(doEatList).isPresent() && (doEatList.size() != 0)) {
                    for(String doEat : doEatList) {
                        diseaseDoEatRelSet.add(name + "," + doEat);
                    }
                    foodNodeSet.addAll(doEatList);
                }

                if(Optional.ofNullable(recommandEatList).isPresent() && (recommandEatList.size() != 0)) {
                    for(String recommandEat : recommandEatList) {
                        diseaseRecommandEatRelSet.add(name + "," + recommandEat);
                    }
                    foodNodeSet.addAll(recommandEatList);
                }

                if(Optional.ofNullable(checkList).isPresent() && (checkList.size() != 0)) {
                    for(String check : checkList) {
                        diseaseCheckRelSet.add(name + "," + check);
                    }
                    checkNodeSet.addAll(checkList);
                }

                if(Optional.ofNullable(drugDetailList).isPresent() && (drugDetailList.size() != 0)) {
                    List<String> producerList = CollectionUtil.newArrayList();
                    for(String drugDetail : drugDetailList) {
                        producerList.add(drugDetail.split("\\(")[0]);
                    }
                    producerNodeSet.addAll(producerList);
                    for(String drugDetail : drugDetailList) {
                        String[] drugStr = drugDetail.split("\\(");
                        drugProducerRelSet.add(drugStr[0] + "," + drugStr[drugStr.length - 1].replace(")", ""));
                    }
                }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 写入node
        writeCsvToPath(medical_check_path, checkNodeSet, Arrays.asList(new String[]{"name"}));
        writeCsvToPath(medical_department_path, departmentNodeSet, Arrays.asList(new String[]{"name"}));
        writeCsvToPath(medical_disease_path, diseaseNodeSet, Arrays.asList(new String[]{"id", "name"}));
        writeCsvToPath(medical_drug_path, drugNodeSet, Arrays.asList(new String[]{"name"}));
        writeCsvToPath(medical_food_path, foodNodeSet, Arrays.asList(new String[]{"name"}));
        writeCsvToPath(medical_producer_path, producerNodeSet, Arrays.asList(new String[]{"name"}));
        writeCsvToPath(medical_symptom_path, symptomNodeSet, Arrays.asList(new String[]{"name"}));

        // 写入rel
        writeCsvToPath(medical_recommand_eat_path, diseaseRecommandEatRelSet, Arrays.asList(new String[]{"start", "end"}));
        writeCsvToPath(medical_belongs_to_path, departmentRelDepartmentSet, Arrays.asList(new String[]{"start", "end"}));
        writeCsvToPath(medical_common_drug_path, diseaseCommondDrugRelSet, Arrays.asList(new String[]{"start", "end"}));
        writeCsvToPath(medical_do_eat_path, diseaseDoEatRelSet, Arrays.asList(new String[]{"start", "end"}));
        writeCsvToPath(medical_drugs_of_path, drugProducerRelSet, Arrays.asList(new String[]{"start", "end"}));
        writeCsvToPath(medical_need_check_path, diseaseCheckRelSet, Arrays.asList(new String[]{"start", "end"}));
        writeCsvToPath(medical_no_eat_path, diseaseNotEatRelSet, Arrays.asList(new String[]{"start", "end"}));
        writeCsvToPath(medical_recommand_drug_path, diseaseRecommandDrugRelSet, Arrays.asList(new String[]{"start", "end"}));
        writeCsvToPath(medical_has_symptom_path, diseaseSymptomRelSet, Arrays.asList(new String[]{"start", "end"}));
        writeCsvToPath(medical_acompany_with_path, diseaseAcompanyRelSet, Arrays.asList(new String[]{"start", "end"}));
        writeCsvToPath(medical_dept_path, diseaseCategoryRelSet, Arrays.asList(new String[]{"start", "end"}));
    }

    /**
     * 处理后的数据写入csv文件中
     * @param csvPath
     * @param dataSet
     * @param head
     */
    public static void writeCsvToPath(String csvPath, Set<String> dataSet, List<String> head) {
        try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(csvPath), StandardCharsets.UTF_8)) {
            bw.write(String.join(",", head));
            bw.newLine();
            int lineCount = 0;
            for(String line : dataSet) {
                bw.write(line);
                bw.newLine();
                lineCount += 1;
                System.out.println("processsed line count " + lineCount);
            }
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


