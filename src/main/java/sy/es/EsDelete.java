package sy.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sy.init.es.ESPoolUtil;
import utils.PropertiesReader;

import java.io.IOException;
import java.util.List;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class EsDelete {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsDelete.class);

    /**
     * delete all data of index
     */
    public static void deleteAllData() {
        String index = PropertiesReader.get("medical_index");
        LOGGER.info("delete all data of index -> " + index);
        if(StringUtils.isNotBlank(index)) {
            RestHighLevelClient client = null;
            try {
                client = ESPoolUtil.borrowClient();
                deleteAll(client, index);
            } finally {
                if(null != client) {
                    ESPoolUtil.returnClient(client);
                }
            }
        }
    }

    /**
     * delete all data
     * @param client
     * @param index
     * @return
     */
    public static String deleteAll(RestHighLevelClient client, String index) {
        BulkByScrollResponse response = null;
        if (EsUtil.existIndex(client, index)) {
            DeleteByQueryRequest request = new DeleteByQueryRequest(index);
            request.setQuery(new MatchAllQueryBuilder());
            try {
                response = client.deleteByQuery(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LOGGER.info("delete all data from index -> " + index);
        LOGGER.info(JSON.toJSONString(response));
        return JSONObject.toJSONString(response);
    }

    /**
     * delete one doc
     * @param client
     * @param index
     * @param id
     * @return
     */
    public static boolean deleteOneDoc(RestHighLevelClient client, String index, String id) {
        DeleteRequest request = new DeleteRequest(index).id(id);
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.WAIT_UNTIL);
        boolean flag = false;
        try {
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            if(response.status() == RestStatus.OK) {
                flag = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * bulk delete docs
     * @param client
     * @param index
     * @param ids
     * @return
     */
    public static boolean deleteBulkDoc(RestHighLevelClient client, String index, List<String> ids) {
        boolean flag = false;
        try {
            BulkRequest bulkRequest = new BulkRequest();
            int count = 0;
            for (String id : ids) {
                count++;
                DeleteRequest deleteRequest = new DeleteRequest(index).id(id);
                bulkRequest.add(deleteRequest);
                if (count % 100 == 0) {
                    BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                    if (bulkResponse.status() == RestStatus.CREATED) {
                        flag = true;
                    }
                    bulkRequest.requests().clear();
                    count = 0;
                }
            }
            if (bulkRequest.numberOfActions() > 0) {
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            LOGGER.error("Batch delete docs error!", e);
        }
        return flag;
    }

}
