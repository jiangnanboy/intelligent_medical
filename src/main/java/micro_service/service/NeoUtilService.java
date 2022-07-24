package micro_service.service;

import sy.neo.NeoUtil;
import sy.neo.type.EMedicalLabel;
import sy.neo.type.EMedicalRel;
import utils.PropertiesReader;

import java.util.Optional;

/**
 * @author sy
 * @date 2022/7/21 20:22
 */
public class NeoUtilService {

    public static void main(String...args) {
        // node csv path
        String medical_check_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_check")).getPath().replaceFirst("/", "");
        String medical_department_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_department")).getPath().replaceFirst("/", "");
        String medical_disease_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_disease")).getPath().replaceFirst("/", "");
        String medical_drug_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_drug")).getPath().replaceFirst("/", "");
        String medical_food_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_food")).getPath().replaceFirst("/", "");
        String medical_producer_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_producer")).getPath().replaceFirst("/", "");
        String medical_symptom_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_symptom")).getPath().replaceFirst("/", "");

        // rel csv path
        String medical_recommand_eat_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_recommand_eat")).getPath().replaceFirst("/", "");
        String medical_belongs_to_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_belongs_to")).getPath().replaceFirst("/", "");
        String medical_common_drug_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_common_drug")).getPath().replaceFirst("/", "");
        String medical_do_eat_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_do_eat")).getPath().replaceFirst("/", "");
        String medical_drugs_of_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_drugs_of")).getPath().replaceFirst("/", "");
        String medical_need_check_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_need_check")).getPath().replaceFirst("/", "");
        String medical_no_eat_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_no_eat")).getPath().replaceFirst("/", "");
        String medical_recommand_drug_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_recommand_drug")).getPath().replaceFirst("/", "");
        String medical_has_symptom_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_has_symptom")).getPath().replaceFirst("/", "");
        String medical_acompany_with_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_acompany_with")).getPath().replaceFirst("/", "");
        String medical_dept_path = NeoUtilService.class.getClassLoader().getResource(PropertiesReader.get("medical_dept_path")).getPath().replaceFirst("/", "");

        NeoUtilService neoUtilService = new NeoUtilService();
        // 1.build nodes
        neoUtilService.buildNode(medical_check_path, EMedicalLabel.Check.name());
        neoUtilService.buildOnlyIndex(EMedicalLabel.Check.name(), "name");
        neoUtilService.buildNode(medical_department_path, EMedicalLabel.Department.name());
        neoUtilService.buildOnlyIndex(EMedicalLabel.Department.name(), "name");
        neoUtilService.buildNode(medical_disease_path, EMedicalLabel.Disease.name());
        neoUtilService.buildOnlyIndex(EMedicalLabel.Disease.name(), "name");
        neoUtilService.buildNode(medical_drug_path, EMedicalLabel.Drug.name());
        neoUtilService.buildOnlyIndex(EMedicalLabel.Drug.name(), "name");
        neoUtilService.buildNode(medical_food_path, EMedicalLabel.Food.name());
        neoUtilService.buildOnlyIndex(EMedicalLabel.Food.name(), "name");
        neoUtilService.buildNode(medical_producer_path, EMedicalLabel.Producer.name());
        neoUtilService.buildOnlyIndex(EMedicalLabel.Producer.name(), "name");
        neoUtilService.buildNode(medical_symptom_path, EMedicalLabel.Symptom.name());
        neoUtilService.buildOnlyIndex(EMedicalLabel.Symptom.name(), "name");

        // 2.build relations
        neoUtilService.buildRelation(medical_recommand_eat_path, EMedicalLabel.Disease.name(), EMedicalLabel.Food.name(), EMedicalRel.RECOMMAND_EAT.name()); //推荐食谱
        neoUtilService.buildRelation(medical_belongs_to_path, EMedicalLabel.Department.name(), EMedicalLabel.Department.name(), EMedicalRel.BELONGS_TO.name()); //属于
        neoUtilService.buildRelation(medical_common_drug_path, EMedicalLabel.Disease.name(), EMedicalLabel.Drug.name(), EMedicalRel.COMMON_DRUG.name()); //常用药品
        neoUtilService.buildRelation(medical_do_eat_path, EMedicalLabel.Disease.name(), EMedicalLabel.Food.name(), EMedicalRel.DO_EAT.name()); //宜吃
        neoUtilService.buildRelation(medical_drugs_of_path, EMedicalLabel.Drug.name(), EMedicalLabel.Producer.name(), EMedicalRel.DRUGS_OF.name()); //生产药品
        neoUtilService.buildRelation(medical_need_check_path, EMedicalLabel.Disease.name(), EMedicalLabel.Check.name(), EMedicalRel.NEED_CHECK.name()); //诊断检查
        neoUtilService.buildRelation(medical_no_eat_path, EMedicalLabel.Disease.name(), EMedicalLabel.Food.name(), EMedicalRel.NO_EAT.name()); //忌吃
        neoUtilService.buildRelation(medical_recommand_drug_path, EMedicalLabel.Disease.name(), EMedicalLabel.Drug.name(), EMedicalRel.RECOMMAND_DRUG.name()); //好评药品
        neoUtilService.buildRelation(medical_has_symptom_path, EMedicalLabel.Disease.name(), EMedicalLabel.Symptom.name(), EMedicalRel.HAS_SYMPTOM.name()); //症状
        neoUtilService.buildRelation(medical_acompany_with_path, EMedicalLabel.Disease.name(), EMedicalLabel.Disease.name(), EMedicalRel.ACOMPANY_WITH.name()); //并发症
        neoUtilService.buildRelation(medical_dept_path, EMedicalLabel.Disease.name(), EMedicalLabel.Department.name(), EMedicalRel.BELONGS_TO.name()); //所属科室
    }

    /**
     * delete all data of neo4j
     */
    public void deleteNeoAllData() {
        NeoUtil.deleteAllDataRel();
    }

    /**
     * build node in neo4j
     * @param nodeCsvPath
     * @param label
     */
    public void buildNode(String nodeCsvPath, String label) {
        NeoUtil.buildNode(nodeCsvPath, label);
    }

    /**
     * build only index
     * @param label
     * @param property
     */
    public void buildOnlyIndex(String label, String property) {
        NeoUtil.buildOnlyIdx(label, property);
    }

    /**
     * build relation and it's othername
     * @param relCsvPath
     * @param startNodeLabel
     * @param endNodeLabel
     * @param relType
     * @param relOtherName
     */
    public void buildRelation(String relCsvPath, String startNodeLabel, String endNodeLabel, String relType, String relOtherName) {
        if(Optional.ofNullable(relOtherName).isPresent()) {
            NeoUtil.buildRel(relCsvPath, startNodeLabel, endNodeLabel, relType, relOtherName);
        } else {
            NeoUtil.buildRel(relCsvPath, startNodeLabel, endNodeLabel, relType);
        }
    }

    /**
     * build relation
     * @param relCsvPath
     * @param startNodeLabel
     * @param endNodeLabel
     * @param relType
     */
    public void buildRelation(String relCsvPath, String startNodeLabel, String endNodeLabel, String relType) {
        this.buildRelation(relCsvPath, startNodeLabel, endNodeLabel, relType, null);
    }

}

