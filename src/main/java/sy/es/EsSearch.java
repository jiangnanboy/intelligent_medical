package sy.es;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class EsSearch {

    /**
     * search for disease name
     * @param client
     * @param index
     * @param query
     * @param currentPage
     * @param size
     * @return
     */
    public static List<Map<String, Object>> diseaseNameSearch(RestHighLevelClient client, String index, String query, int currentPage, int size) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("name", query));
        searchSourceBuilder.query(boolQueryBuilder);
        searchSourceBuilder.from((currentPage - 1) * size);
        searchSourceBuilder.size(size);
        searchSourceBuilder.fetchSource(new String[]{"name", "id"}, null);
        request.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Map<String, Object>> mapList = null;
        if(Optional.ofNullable(response).isPresent()) {
            SearchHit[] hits = response.getHits().getHits();
            mapList = Arrays.stream(hits).map(hit -> hit.getSourceAsMap()).collect(Collectors.toList());
        }
        return mapList;
    }

    /**
     * get one doc map
     * @param client
     * @param index
     * @param id
     * @return
     */
    public static Map<String, Object> getOneDocById(RestHighLevelClient client, String index, String id) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        searchSourceBuilder.query(boolQueryBuilder);
        request.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = null;
        if(Optional.ofNullable(response).isPresent()) {
            SearchHit[] hits = response.getHits().getHits();
            map = hits[0].getSourceAsMap();
        }
        return map;
    }

}

