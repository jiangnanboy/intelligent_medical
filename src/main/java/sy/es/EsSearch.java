package sy.es;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MoreLikeThisQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import utils.CollectionUtil;

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
     * get related query
     * @param client
     * @param index
     * @param query
     * @param size
     * @return
     */
    public static List<String> getRelatedQuery(RestHighLevelClient client, String index, String query, int size) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        MoreLikeThisQueryBuilder moreLikeThisQueryBuilder = QueryBuilders.moreLikeThisQuery(new String[]{"name"}, new String[]{query}, null)
                .minTermFreq(1)
                .maxQueryTerms(24);

        searchSourceBuilder.query(moreLikeThisQueryBuilder);
        searchSourceBuilder.size(size + 1);
        searchSourceBuilder.fetchSource(new String[]{"name"}, null);
        request.source(searchSourceBuilder);
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> queryList = null;
        if(Optional.ofNullable(response).isPresent()) {
            SearchHit[] hits = response.getHits().getHits();
            queryList = Arrays.stream(hits).map(hit -> hit.getSourceAsMap().get("name").toString()).collect(Collectors.toList());
            queryList = queryList.stream().filter(q -> !StringUtils.equals(q, query)).collect(Collectors.toList());
        }
        return queryList.subList(0, size);
    }

    /**
     * get query completion
     * @param client
     * @param index
     * @param query
     * @param size
     * @return
     */
    public static List<String> getQueryCompletion(RestHighLevelClient client, String index, String query, int size) {
        SearchRequest request = new SearchRequest(index);
        request.source().suggest(new SuggestBuilder().addSuggestion("suggestion",
                SuggestBuilders.completionSuggestion("disease_completion")
        .prefix(query)
        .skipDuplicates(true)
        .size(size)
        ));
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> queryList = CollectionUtil.newArrayList();
        if(Optional.ofNullable(response).isPresent()) {
            Suggest suggest = response.getSuggest();
            CompletionSuggestion suggestion = suggest.getSuggestion("suggestion");
            List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();
            for(CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                queryList.add(text);
            }
        }
        return queryList;
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

