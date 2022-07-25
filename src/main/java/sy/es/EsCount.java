package sy.es;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sy.init.es.ESPoolUtil;
import utils.PropertiesReader;

import java.io.IOException;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class EsCount {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsCount.class);

    /**
     * count docs by query
     * @param query
     * @param field
     * @return
     */
    public long countByQuery(String query, String field) {
        String index = PropertiesReader.get("medical_index");
        LOGGER.info("count docs -> " +index + " query -> " + query);
        if(StringUtils.isNotBlank(query)) {
            RestHighLevelClient client = null;
            long countSum = 0;
            try {
                client = ESPoolUtil.borrowClient();
                if(EsUtil.existIndex(client, index)) {
                    countSum = countByQuery(client, index, field, query);
                }
            } finally {
                if(null != client) {
                    ESPoolUtil.returnClient(client);
                }
            }
            return countSum;
        } else {
            return 0;
        }
    }

    /**
     * count docs by query
     * @param client
     * @param index
     * @param field
     * @param query
     * @return
     */
    public long countByQuery(RestHighLevelClient client, String index, String field, String query) {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery(field, query);
        CountRequest countRequest = new CountRequest(index);
        countRequest.query(queryBuilder);
        CountResponse response = null;
        try {
            response = client.count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response.getCount();
    }


}
