package sy.qa;

import java.util.List;
import java.util.Map;

/**
 * @author sy
 * @date 2022/7/29 19:40
 */
public class MedicalQA {
    static QuestionClassifier questionClassifier;
    static QuestionParser questionParser;
    static AnswerSearcher answerSearcher;

    static {
        questionClassifier = new QuestionClassifier();
        questionParser = new QuestionParser();
        answerSearcher = new AnswerSearcher();
    }

    public static String qa(String sent) {
        String answer = "您好，我是小嘉医药智能助理，如果有问题，可联系https://jiangnanboy.github.io/。";
        Map<String, Object> classifiyMap = questionClassifier.classify(sent);
        if((null == classifiyMap) || (0 == classifiyMap.size())) {
            return answer;
        }
        List<Map<String, Object>> cypherMapList = questionParser.parser(classifiyMap);
        List<String> answerResults = answerSearcher.search(cypherMapList);
        if((null == answerResults) || (0 == answerResults.size())) {
            return answer;
        } else {
            return String.join("\n", answerResults);
        }
    }

}

