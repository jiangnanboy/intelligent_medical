package micro_service.service;

import sy.qa.MedicalQA;

import java.util.Scanner;

/**
 * @author YanShi
 * @date 2022/7/29 21:20
 */
public class QAService {
    public String qa(String question) {
        String answer = MedicalQA.qa(question);
        return answer;
    }
}
