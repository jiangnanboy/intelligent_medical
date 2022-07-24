package sy.process;

import sy.process.proc.NeoProcessData;
import utils.PropertiesReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class ProcessMain {

    /**
     * process source data
     */
    public static void neoProcessLabelRel() {

        // source data path
        String medicalPath = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical")).getPath().replaceFirst("/", "");
        // processed node data path
        String medical_check_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_check")).getPath().replaceFirst("/", "");
        String medical_department_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_department")).getPath().replaceFirst("/", "");
        String medical_disease_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_disease")).getPath().replaceFirst("/", "");
        String medical_drug_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_drug")).getPath().replaceFirst("/", "");
        String medical_food_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_food")).getPath().replaceFirst("/", "");
        String medical_producer_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_producer")).getPath().replaceFirst("/", "");
        String medical_symptom_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_symptom")).getPath().replaceFirst("/", "");

        try {
            if(!Files.exists(Paths.get(medical_check_path))) {
                Files.createFile(Paths.get(medical_check_path));
            }
            if(!Files.exists(Paths.get(medical_department_path))) {
                Files.createFile(Paths.get(medical_department_path));
            }
            if(!Files.exists(Paths.get(medical_disease_path))) {
                Files.createFile(Paths.get(medical_disease_path));
            }
            if(!Files.exists(Paths.get(medical_drug_path))) {
                Files.createFile(Paths.get(medical_drug_path));
            }
            if(!Files.exists(Paths.get(medical_food_path))) {
                Files.createFile(Paths.get(medical_food_path));
            }
            if(!Files.exists(Paths.get(medical_producer_path))) {
                Files.createFile(Paths.get(medical_producer_path));
            }
            if(!Files.exists(Paths.get(medical_symptom_path))) {
                Files.createFile(Paths.get(medical_symptom_path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // processed rel data path
        String medical_recommand_eat_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_recommand_eat")).getPath().replaceFirst("/", "");
        String medical_belongs_to_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_belongs_to")).getPath().replaceFirst("/", "");
        String medical_common_drug_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_common_drug")).getPath().replaceFirst("/", "");
        String medical_do_eat_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_do_eat")).getPath().replaceFirst("/", "");
        String medical_drugs_of_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_drugs_of")).getPath().replaceFirst("/", "");
        String medical_need_check_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_need_check")).getPath().replaceFirst("/", "");
        String medical_no_eat_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_no_eat")).getPath().replaceFirst("/", "");
        String medical_recommand_drug_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_recommand_drug")).getPath().replaceFirst("/", "");
        String medical_has_symptom_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_has_symptom")).getPath().replaceFirst("/", "");
        String medical_acompany_with_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_acompany_with")).getPath().replaceFirst("/", "");
        String medical_dept_path = ProcessMain.class.getClassLoader().getResource(PropertiesReader.get("medical_dept_path")).getPath().replaceFirst("/", "");

        try {
            if(!Files.exists(Paths.get(medical_recommand_eat_path))) {
                Files.createFile(Paths.get(medical_recommand_eat_path));
            }
            if(!Files.exists(Paths.get(medical_belongs_to_path))) {
                Files.createFile(Paths.get(medical_belongs_to_path));
            }
            if(!Files.exists(Paths.get(medical_common_drug_path))) {
                Files.createFile(Paths.get(medical_common_drug_path));
            }
            if(!Files.exists(Paths.get(medical_do_eat_path))) {
                Files.createFile(Paths.get(medical_do_eat_path));
            }
            if(!Files.exists(Paths.get(medical_drugs_of_path))) {
                Files.createFile(Paths.get(medical_drugs_of_path));
            }
            if(!Files.exists(Paths.get(medical_need_check_path))) {
                Files.createFile(Paths.get(medical_need_check_path));
            }
            if(!Files.exists(Paths.get(medical_no_eat_path))) {
                Files.createFile(Paths.get(medical_no_eat_path));
            }
            if(!Files.exists(Paths.get(medical_recommand_drug_path))) {
                Files.createFile(Paths.get(medical_recommand_drug_path));
            }
            if(!Files.exists(Paths.get(medical_has_symptom_path))) {
                Files.createFile(Paths.get(medical_has_symptom_path));
            }
            if(!Files.exists(Paths.get(medical_acompany_with_path))) {
                Files.createFile(Paths.get(medical_acompany_with_path));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // csv path
        String[] nodePathStr = new String[] {medical_check_path, medical_department_path, medical_disease_path, medical_drug_path,
                medical_food_path, medical_producer_path, medical_symptom_path};
        String[] relPathStr = new String[] {medical_recommand_eat_path, medical_belongs_to_path, medical_common_drug_path, medical_do_eat_path, medical_drugs_of_path,
                medical_need_check_path, medical_no_eat_path, medical_recommand_drug_path, medical_has_symptom_path, medical_acompany_with_path, medical_dept_path};
        // process data
        NeoProcessData.process(medicalPath, nodePathStr, relPathStr);
    }


}
