package sy.qa;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
//import sy.neo.NeoSearch;

import java.util.List;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class QuestionAnswer {

    Driver driver = null;
    public QuestionAnswer(Driver driver) {
        this.driver = driver;
    }

    /**
     * @param label
     * @param entryList
     */
    public List<Record> response(double label, List<Pair> entryList) {
        List<Record> recordList = null;
        String questionType = String.valueOf(label);
        if(StringUtils.equals(questionType, "0.0")) {
            if(1 == entryList.size()) {
                String placeName = (String)entryList.get(0).getLeft();
//                recordList = NeoSearch.getEffectivePlaceLawRegulation(placeName, 10);
            }
        } else if(StringUtils.equals(questionType, "1.0")) {
            if(2 == entryList.size()) {
                String dateFrom;
                String placeName;
                if(StringUtils.equals((String)entryList.get(0).getRight(), "ns")) {
                    placeName = (String)entryList.get(0).getLeft();
                    dateFrom = (String)entryList.get(1).getLeft();
                } else {
                    placeName = (String)entryList.get(1).getLeft();
                    dateFrom = (String)entryList.get(0).getLeft();
                }
//                recordList = NeoSearch.getPlaceLawFromDate(placeName, dateFrom, 10);
            }
        } else if(StringUtils.equals(questionType, "2.0")) {
            if(3 == entryList.size()) {
                String placeName;
                String dateFrom;
                String dateTo;
                if(StringUtils.equals((String) entryList.get(0).getRight(), "ns")) {
                    placeName = (String) entryList.get(0).getLeft();
                    dateFrom = (String) entryList.get(1).getLeft();
                    dateTo = (String) entryList.get(2).getLeft();
                } else {
                    placeName = (String) entryList.get(2).getLeft();
                    dateFrom = (String) entryList.get(0).getLeft();
                    dateTo = (String) entryList.get(1).getLeft();
                }
//                recordList = NeoSearch.getPlaceLawFromDate2Date(placeName, dateFrom, dateTo, 10);
            }
        }

        return recordList;
    }

}

