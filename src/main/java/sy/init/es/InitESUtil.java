package sy.init.es;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class InitESUtil {

    /**
     * client
     */
    public static RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
            new HttpHost("127.0.0.1", 9200, "http")
    ));

    /**
     * close
     */
    public static void shutDown() {
        if(null != client) {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * default type
     */
    public static final String DEFAULT_TYPE = "_doc";

    public static final String Set_METHOD_PREFIX = "set";

    /**
     * reture status -> created
     */
    public static final String RESPONSE_STATUS_CREATED = "CREATED";

    /**
     * return status -> ok
     */
    public static final String RESPONSE_STATUS_OK = "OK";

    /**
     * return status -> not_found
     */
    public static final String RESPONSE_STATUS_NOT_FOUND = "NOT_FOUND";

    /**
     * filter field
     */
    public static final String[] IGNORE_KEY = {"@timestamp", "@version", "type"};

    /**
     * timeout
     */
    public static final TimeValue TIME_VALUE_SECONDS = TimeValue.timeValueSeconds(1);

    /**
     *batch insert
     */
    public static final String BATCH_OP_TYPE_INSERT = "insert";

    /**
     * batch delete
     */
    public static final String BATCH_OP_TYPE_DELETE = "delete";

    /**
     * batch update
     */
    public static final String BATCH_OP_TYPE_UPDATE = "update";

    /**
     * filter field
     * @param map
     */
    public static void ignoreSource(Map<String, Object> map) {
        for(String key : IGNORE_KEY) {
            map.remove(key);
        }
    }

    /**
     * Converts document data to the specified object
     * @param sourceAsMap
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T dealObject(Map<String, Object> sourceAsMap, Class<T> clazz) {
        try {
            ignoreSource(sourceAsMap);
            Iterator<String> keyIterator = sourceAsMap.keySet().iterator();
            T t = clazz.getDeclaredConstructor().newInstance();
            while (keyIterator.hasNext()) {
                String key = keyIterator.next();
                String replaceKey = key.replaceFirst(key.substring(0, 1), key.substring(0, 1).toUpperCase());
                Method method = null;
                try {
                    method = clazz.getMethod(Set_METHOD_PREFIX + replaceKey, sourceAsMap.get(key).getClass());
                } catch (NoSuchMethodException e) {
                    continue;
                }
                method.invoke(t, sourceAsMap.get(key));
            }
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * create index
     * @param index
     * @return
     */
    public static boolean insertIndex(String index) {
        CreateIndexRequest reques = new CreateIndexRequest(index);
        try {
            CreateIndexResponse response = client.indices().create(reques, RequestOptions.DEFAULT);
            return null != response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * index exists
     * @param index
     * @return
     */
    public static boolean isExistsIndex(String index) {
        GetIndexRequest request = new GetIndexRequest(index);
        try {
            return client.indices().exists(request, RequestOptions.DEFAULT);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * delete index
     * @param index
     * @return
     */
    public static boolean deleteIndex(String index) {
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        try {
            AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
            return response.isAcknowledged();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * insert or update document
     * @param index
     * @param id
     * @param data
     * @return
     */
    public static boolean insertOrUpdateDoc(String index, String id, Object data) {
        try {
            IndexRequest request = new IndexRequest(index);
            request.timeout(TIME_VALUE_SECONDS);
            if(null != id && id.length() > 0) {
                request.id(id);
            }
            request.source(JSON.toJSONString(data), XContentType.JSON);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
            String status = response.status().toString();
            if(RESPONSE_STATUS_CREATED.equals(status) || RESPONSE_STATUS_OK.equals(status)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * update document
     * @param index
     * @param id
     * @param data
     * @return
     */
    public static boolean updateDoc(String index, String id, Object data) {
        try {
            UpdateRequest request = new UpdateRequest(index, id);
            request.doc(JSON.toJSONString(data), XContentType.JSON);
            UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
            String status = response.status().toString();
            if(RESPONSE_STATUS_OK.equals(status)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * delete document
     * @param index
     * @param id
     * @return
     */
    public static boolean deleteDoc(String index, String id) {
        try {
            DeleteRequest request = new DeleteRequest(index, id);
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            String status = response.status().toString();
            if(RESPONSE_STATUS_OK.equals(status)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * mini batch insert
     * @param index
     * @param dataList
     * @param timeout
     * @return
     */
    public static boolean miniBatchInsert(String index, List<Object> dataList, long timeout) {
        try {
            BulkRequest request = new BulkRequest();
            request.timeout(TimeValue.timeValueSeconds(timeout));
            if(null != dataList && dataList.size() > 0) {
                for(Object obj : dataList) {
                    request.add(new IndexRequest(index)
                    .source(JSON.toJSONString(obj), XContentType.JSON));
                }
                BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
                if(!responses.hasFailures()) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * batch delete
     * @param index
     * @param idList
     * @return
     */
    public static boolean batchDelete(String index, List<String> idList) {
        BulkRequest request = new BulkRequest();
        for(String id : idList) {
            request.add(new DeleteRequest().index(index).id(id));
        }
        try {
            BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
            return !responses.hasFailures();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * doc exists
     * @param index
     * @param id
     * @return
     */
    public static boolean isExistsDoc(String index, String id) {
        return isExistsDoc(index, DEFAULT_TYPE, id);
    }

    public static boolean isExistsDoc(String index, String type, String id) {
        GetRequest request = new GetRequest(index).id(id);
        try {
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            return response.isExists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * search doc by id
     * @param index
     * @param id
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T selectDocById(String index, String id, Class<T> clazz) {
        return selectDocById(index, DEFAULT_TYPE, id, clazz);
    }

    public static <T> T selectDocById(String index, String type, String id, Class<T> clazz) {
        try {
            type = type == null || type.equals("") ? DEFAULT_TYPE : type;
            GetRequest request = new GetRequest(index).id(id);
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            if(response.isExists()) {
                Map<String, Object> sourceAsMap = response.getSourceAsMap();
                return dealObject(sourceAsMap, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * serach doc
     * @param index
     * @param sourceBuilder
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> selectDocList(String index, SearchSourceBuilder sourceBuilder, Class<T> clazz) {
        try {
            SearchRequest request = new SearchRequest(index);
            if(null != sourceBuilder) {
                sourceBuilder.trackTotalHits(true);
                request.source(sourceBuilder);
            }
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            if(null != response.getHits()) {
                List<T> list = new ArrayList<>();
                SearchHit[] hits = response.getHits().getHits();
                for(SearchHit hit : hits) {
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    list.add(dealObject(sourceAsMap, clazz));
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * search doc
     * @param index
     * @param sourceBuilder
     * @return
     */
    public static SearchResponse selectDoc(String index, SearchSourceBuilder sourceBuilder) {
        try {
            SearchRequest request = new SearchRequest(index);
            if(null != sourceBuilder) {
                sourceBuilder.trackTotalHits(true);
                sourceBuilder.size(10000);
                request.source(sourceBuilder);
            }
            return client.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * search doc by highlight
     * @param index
     * @param sourceBuilder
     * @param clazz
     * @param highLight
     * @param <T>
     * @return
     */
    public static <T> List<T> selectDocListHighLight(String index, SearchSourceBuilder sourceBuilder, Class<T> clazz, String highLight) {
        try {
            SearchRequest request = new SearchRequest(index);
            if(null != sourceBuilder) {
                sourceBuilder.trackTotalHits(true);
                HighlightBuilder highlightBuilder = new HighlightBuilder();
                highlightBuilder.field(highLight);
                highlightBuilder.requireFieldMatch(false);
                highlightBuilder.preTags("<span style='color:red'>");
                highlightBuilder.postTags("</span>");
                sourceBuilder.highlighter(highlightBuilder);
                request.source(sourceBuilder);
            }

            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            if(null != response.getHits()) {
                List<T> list = new ArrayList<>();
                for(SearchHit hit : response.getHits().getHits()) {
                    Map<String, HighlightField> highlightFieldMap = hit.getHighlightFields();
                    HighlightField title = highlightFieldMap.get(highLight);
                    Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                    if(null != title) {
                        Text[] fragments = title.fragments();
                        String nTitle = "";
                        for(Text fragement : fragments) {
                            nTitle += fragement;
                        }
                        sourceAsMap.put(highLight, nTitle);
                    }
                    list.add(dealObject(sourceAsMap, clazz));
                }
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * search all data
     * @param index
     * @return
     */
    public static SearchResponse searchAllData(String index) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        request.source(sourceBuilder);
        try {
            return client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> searchAllData(String index, Class<T> clazz) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        request.source(sourceBuilder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            if(null != response.getHits()) {
                List<T> list = new ArrayList<>();
                SearchHit[] hits = response.getHits().getHits();
                for(SearchHit hit : hits) {
                    Map<String, Object> sorceAsMap = hit.getSourceAsMap();
                    list.add(dealObject(sorceAsMap, clazz));
                }
                return list;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}


