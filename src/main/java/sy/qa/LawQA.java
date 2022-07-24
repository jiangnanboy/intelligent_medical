package sy.qa;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.sql.SparkSession;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import sy.init.neo.NeoPoolUtil;
import sy.init.spark.SparkPoolUtil;
import sy.util.Entry;
import sy.util.Segment;
import utils.CollectionUtil;
import utils.PropertiesReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class LawQA {
    static List<String> byeList;
    static List<String> hiList;

    static {
        byeList = CollectionUtil.newArrayList();
        byeList.add("exit");
        byeList.add("quit");
        byeList.add("stop");
        byeList.add("bye");
        byeList.add("再见");
        byeList.add("byebye");

        hiList = CollectionUtil.newArrayList();
        hiList.add("hello");
        hiList.add("hi");
        hiList.add("你好");
    }

    /**
     * law qa
     */
    public static void lawQA() {
        String qClfRawData = PropertiesReader.get("q_clf_rawdata");
        String qClfTrainData = PropertiesReader.get("q_clf_traindata");
        if(Files.notExists(Paths.get(qClfTrainData))) {
            trainDataToSegment(qClfRawData, qClfTrainData);
        }
        QuestionClassification classification;
        SparkSession sparkSession = null;
        try {
            sparkSession = SparkPoolUtil.borrowDriver();
            sparkSession.sparkContext().setLogLevel("ERROR");
            classification = new QuestionClassification(sparkSession);
        } finally {
            if(Optional.ofNullable(sparkSession).isPresent()) {
                SparkPoolUtil.returnDriver(sparkSession);
            }
        }

        String modelFile = PropertiesReader.get("model_file");
        PipelineModel model = null;
        if(Files.notExists(Paths.get(modelFile))) {
            model = classification.train(qClfTrainData, modelFile);
        }
        Scanner sc = new Scanner(System.in);
        String question;
        SecureRandom rand = new SecureRandom();
        Driver driver = null;
        try {
            driver = NeoPoolUtil.borrowDriver();
            littleFaSay("您好，我是小法，请问您想知道些什么呢？");
            while (true) {
                iSay();
                question = sc.nextLine();
                if(byeList.contains(question)) {
                    littleFaSay("byebye");
                    break;
                }
                if(hiList.contains(question)) {
                    littleFaSay(hiList.get(rand.nextInt(hiList.size())));
                    continue;
                }
                Pair<String, List<String>> datePairList = null;
                StringBuffer sb = new StringBuffer();
                String dateFrom = null;
                String dateTo = null;
                if(Optional.ofNullable(datePairList).isPresent()) {
                    question = datePairList.getLeft();
                    if( 1 == datePairList.getRight().size()) {
                        dateFrom = datePairList.getRight().get(0);
                    } else if(2 == datePairList.getRight().size()) {
                        dateFrom = datePairList.getRight().get(0);
                        dateTo = datePairList.getRight().get(1);
                    }
                }
                List<Entry> entryList = Segment.hanlpNer(question, false);

                if(Optional.ofNullable(dateFrom).isPresent() && Optional.ofNullable(dateTo).isPresent()) {
                    String finalDateFrom = dateFrom;
                    String finalDateTo = dateTo;
                    List<Entry> dateEntryList = CollectionUtil.newArrayList();
                    boolean flag = true;
                    for(Entry entry : entryList) {
                        if(StringUtils.equals(entry.getWord(), "date")) {
                            if(flag) {
                                dateEntryList.add(new Entry(finalDateFrom, "date"));
                                flag = false;
                            } else {
                                dateEntryList.add(new Entry(finalDateTo, "date"));
                            }
                        } else {
                            dateEntryList.add(entry);
                        }
                    }
                    entryList = dateEntryList;
                } else if(Optional.ofNullable(dateFrom).isPresent()) {
                    String finalDateFrom = dateFrom;
                    entryList = entryList.stream().map(entry -> {
                        if(StringUtils.equals(entry.getWord(), "date")) {
                            return new Entry(finalDateFrom, "date");
                        }
                        return entry;
                    }).collect(Collectors.toList());
                }

                List<Pair> predictPairList = CollectionUtil. newArrayList();
                entryList.stream().forEach(entry -> {
                    if(StringUtils.equals(entry.getPos(), "ns")) {
                        sb.append(entry.getPos()).append(" ");
                        Map<String, String> placeMappter = null;
                        if(Optional.ofNullable(placeMappter.get("county")).isPresent()) {
                            predictPairList.add(Pair.of(placeMappter.get("county"), entry.getPos()));
                        } else if(Optional.ofNullable(placeMappter.get("city")).isPresent()) {
                            predictPairList.add(Pair.of(placeMappter.get("city"), entry.getPos()));
                        } else if(Optional.ofNullable(placeMappter.get("province")).isPresent()) {
                            predictPairList.add(Pair.of(placeMappter.get("province"), entry.getPos()));
                        } else if(StringUtils.equals("中国", entry.getWord())) {
                            predictPairList.add(Pair.of("中华人民共和国", entry.getPos()));
                        }
                    } else if (StringUtils.equals(entry.getPos(), "date")) {
                        sb.append(entry.getPos()).append(" ");
                        predictPairList.add(Pair.of(entry.getWord(), entry.getPos()));
                    } else {
                        sb.append(entry.getWord()).append(" ");
                    }
                });

                double label = classification.predict(model, sb.toString().trim());
                QuestionAnswer questionAnswer = new QuestionAnswer(driver);
                List<Record> responseResult = questionAnswer.response(label, predictPairList);
                if(Optional.ofNullable(responseResult).isPresent()) {
                    List<Map<String, String>> listMapResult = responseResult.stream().map(record -> {
                        Map<String, String> recordMap = new HashMap<String, String>(){{
                            put("name", record.get("name").toString());
                            put("id", record.get("id").toString());
                            put("p_date", record.get("p_date").toString());
                        }};
                        return recordMap;
                    }).collect(Collectors.toList());
                    littleFaSay(JSON.toJSONString(listMapResult));
                } else {
                    littleFaSay("抱歉系统没能明白您的话！");
                }
            }
        } finally {
            if(Optional.ofNullable(driver).isPresent()) {
                NeoPoolUtil.returnDriver(driver);
            }
        }
    }

    private static void littleFaSay(String words) {
        System.out.println("小法：" + words);
    }

    private static void iSay() {
        System.out.print("you：");
    }

    /**
     * segment
     * @param trainDataPath
     * @param trainDataSavePath
     */
    private static void trainDataToSegment(String trainDataPath, String trainDataSavePath) {
        try(BufferedReader br = Files.newBufferedReader(Paths.get(trainDataPath));
            BufferedWriter bw = Files.newBufferedWriter(Paths.get(trainDataSavePath))) {
            String line;
            while ((line=br.readLine())!=null) {
                String[] str = line.split(",");
                String label = str[0];
                List<Entry> entryList = Segment.hanlpSegment(str[1], false);
                String features = String.join(" ", entryList.stream().map(entry -> entry.getWord()).filter(word -> StringUtils.isNotBlank(word)).collect(Collectors.toList()));
                bw.append(label).append(",").append(features);
                bw.newLine();
                bw.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}



