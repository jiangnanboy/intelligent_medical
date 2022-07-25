package sy.es;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sy.init.es.ESPoolUtil;
import sy.process.proc.EsProcessData;
import utils.CollectionUtil;
import utils.PropertiesReader;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class EsUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(EsUtil.class);

    /**
     * insert data to es
     * @param client
     * @param index
     * @param list
     * @return
     */
    public static boolean insertDocByBulk(RestHighLevelClient client, String index, List<Map<String, Object>> list) {
        LOGGER.info("insert data to es");
        if (existIndex(client, index)) {
            //insert
            BulkRequest bulkRequest = new BulkRequest();
            bulkRequest.timeout("10s");
            for (int i = 0; i < list.size(); i++) {
//                    bulkRequest.add(new IndexRequest(INDEX).source(list.get(i)));
                // or
                bulkRequest.add(new IndexRequest(index).source(JSON.toJSONString(list.get(i)), XContentType.JSON));
            }

            try {
                BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                // responses.hasFailures(); // 是否失败，false表示成功！
                if (RestStatus.CREATED == responses.status()) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        } else {
            LOGGER.warn("no this index -> " + index);
        }

        return false;
    }

    /**
     * index exists ?
     * @param client
     * @param index
     * @return
     */
    public static boolean existIndex(RestHighLevelClient client, String index) {
        GetIndexRequest getIndexRequest = new GetIndexRequest(index);
        boolean exists = false;
        try {
            exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            LOGGER.error("Index exception", index);
        }
        return exists;
    }

    /**
     * get all docs
     * @param client
     * @param index
     * @return
     */
    public static List<Map<String, Object>> searchAll(RestHighLevelClient client, String index) {
        List<Map<String, Object>> resultiMapList = CollectionUtil.newArrayList();
        if (existIndex(client, index)) {
            SearchRequest request = new SearchRequest(index);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.query(QueryBuilders.matchAllQuery());
//            searchSourceBuilder.fetchSource(include, exclude);
            // 需要唯一不重复的字段作为排序
            searchSourceBuilder.sort("_id", SortOrder.DESC);
            //searchSourceBuilder.sort("_score", SortOrder.DESC);
            //score相同，则按时间降序排序
            //searchSourceBuilder.sort("publish_date", SortOrder.DESC);
            //构造器添加到搜索请求
            request.source(searchSourceBuilder);
            //客户端返回
            SearchResponse response = null;
            try {
                response = client.search(request, RequestOptions.DEFAULT);
                //搜索结果
                SearchHit[] hits = response.getHits().getHits();
                while (hits.length > 0) {
                    for (SearchHit hit : hits) {
                        String id = hit.getId();
                        Map<String, Object> sourceMap = hit.getSourceAsMap();
                        sourceMap.put("id", id);
                        resultiMapList.add(sourceMap);
                    }
                    SearchHit last = hits[hits.length - 1];
                    searchSourceBuilder.searchAfter(last.getSortValues());
                    response = client.search(request, RequestOptions.DEFAULT);
                    hits = response.getHits().getHits();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultiMapList;
    }

    /**
     * insert one doc
     * @param client
     * @param index
     * @param id
     * @param docSource
     * @return
     */
    public static boolean insertDoc(RestHighLevelClient client, String index, String id, Map<String, Object> docSource) {
        IndexRequest request = null;
        if(Optional.ofNullable(id).isPresent()) {
            request = new IndexRequest(index).id(id).source(docSource);
        } else {
            request = new IndexRequest(index).source(docSource);
        }

        boolean flag = false;
        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            if(response.status() == RestStatus.CREATED) {
                flag = true;
            }
        } catch (IOException e) {
            LOGGER.error("Insert one doc error!", index, id, docSource, e);
        }
        LOGGER.info("insert one doc -> " + flag);

        return flag;
    }

    public static boolean insertDoc(RestHighLevelClient client, String index, Map<String, Object> docSource) {
        return insertDoc(client, index, null, docSource);
    }

    /**
     * bulk insert docs
     * @param client
     * @param index
     * @param sources
     * @return
     */
    public static boolean insertBulkDoc(RestHighLevelClient client, String index, List<Map<String, Object>> sources) {
        boolean flag = false;
        try {
            BulkRequest bulkRequest = new BulkRequest();
            int count = 0;
            for(Map<String, Object> map : sources) {
                count++;
                IndexRequest indexRequest = new IndexRequest(index).source(map);
                bulkRequest.add(indexRequest);
                if(count % 100 == 0) {
                    BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                    if(responses.status() == RestStatus.CREATED) {
                        flag = true;
                    }
                    bulkRequest.requests().clear();
                    LOGGER.info("insert docs-> " + count);
                    count = 0;
                }
            }
            if(bulkRequest.numberOfActions() > 0) {
                BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                if(!responses.hasFailures()) {
                    flag = true;
                }
                LOGGER.info("insert docs-> " + count);
            }
        } catch (IOException e) {
            LOGGER.error("Batch create docs error!", index, e);
        }
        return flag;
    }

    /**
     * bulk insert docs
     * @param client
     * @param index
     * @param sources
     * @return
     */
    public static boolean insertBulkDoc(RestHighLevelClient client, String index, Map<String, Map<String, Object>> sources) {
        boolean flag = false;
        try {
            BulkRequest bulkRequest = new BulkRequest();
            Iterator<String> it = sources.keySet().iterator();
            int count = 0;
            while (it.hasNext()) {
                count ++;
                String next = it.next();
                IndexRequest indexRequest = new IndexRequest(index).id(next).source(sources.get(next));
                bulkRequest.add(indexRequest);
                if(count % 100 == 0) {
                    BulkResponse responses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
                    if(responses.status() == RestStatus.CREATED) {
                        flag = true;
                    }
                    bulkRequest.requests().clear();
                    count = 0;
                }
            }

            if(bulkRequest.numberOfActions() > 0) {
                client.bulk(bulkRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException e) {
            LOGGER.error("Batch create docs error!", index, e);
        }
        return flag;
    }

    /**
     * update one doc
     * @param client
     * @param index
     * @param id
     * @param docSource
     * @return
     */
    public static boolean updateOneDoc(RestHighLevelClient client, String index, String id, Map<String, Object> docSource) {
        UpdateRequest request = new UpdateRequest(index, id);
        request.doc(docSource);
        boolean flag = false;
        try {
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            if(response.status() == RestStatus.OK) {
                flag = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    /**
     * build index and mapping
     */
    public static void buildIndexMapping(String mappingJson) {
        LOGGER.info("build index and mapping...");
        if(StringUtils.isNotBlank(mappingJson)) {
            IndexMappingUtil indexMappingUtil = new IndexMappingUtil();
            RestHighLevelClient client = null;
            try {
                client = ESPoolUtil.borrowClient();
                indexMappingUtil.createIndices(client, mappingJson);
            } finally {
                if(null != client) {
                    ESPoolUtil.returnClient(client);
                }
            }
        }
    }

    /**
     * insert json data to es
     * @param jsonFilePath
     * @return
     */
    public static boolean insertData2Es(String jsonFilePath) {
        LOGGER.info("insert json data to es ...");
        String index = PropertiesReader.get("medical_index");
        boolean insertBoolean = false;
        if(StringUtils.isNotBlank(jsonFilePath) && StringUtils.isNotBlank(index)) {
            List<Map<String, Object>> infoMapList = null;
            LOGGER.info("total size -> " + infoMapList.size());
            RestHighLevelClient client = null;
            try {
                client = ESPoolUtil.borrowClient();
                insertBoolean = EsUtil.insertBulkDoc(client, index, infoMapList);
            } finally {
                if(null != client) {
                    ESPoolUtil.returnClient(client);
                }
            }
        }
        LOGGER.info("insert -> " +  insertBoolean);
        return insertBoolean;
    }

    /**
     * insert json data to es
     * @param jsonFilePath
     * @return
     */
    public static boolean insertData2Es(String index, String jsonFilePath) {
        LOGGER.info("insert json data to es ...");
        boolean insertBoolean = false;
        if(StringUtils.isNotBlank(jsonFilePath) && StringUtils.isNotBlank(index)) {
            List<Map<String, Object>> infoMapList = EsProcessData.getJsonData(jsonFilePath);
            LOGGER.info("total size -> " + infoMapList.size());
            RestHighLevelClient client = null;
            try {
                client = ESPoolUtil.borrowClient();
                insertBoolean = EsUtil.insertBulkDoc(client, index, infoMapList);
            } finally {
                if(null != client) {
                    ESPoolUtil.returnClient(client);
                }
            }
        }
        LOGGER.info("insert -> " +  insertBoolean);
        return insertBoolean;
    }

}

