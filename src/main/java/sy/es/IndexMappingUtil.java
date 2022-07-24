package sy.es;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.CollectionUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author sy
 * @date 2022/3/31 21:14
 */
public class IndexMappingUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexMappingUtil.class);

    /**
     * build index
     * @param client
     * @param jsonPath
     */
    public void createIndices(RestHighLevelClient client, String jsonPath) {
        List<IndexMapping> indexMappings = parseIndicesConfFile(jsonPath);
        doCreateIndices(client, indexMappings);
    }

    /**
     * @param client
     * @param indexMappings
     */
    private void doCreateIndices(RestHighLevelClient client, List<IndexMapping> indexMappings) {
        if(indexMappings.isEmpty()) {
            return;
        }
        for(IndexMapping indexMapping : indexMappings) {
            if(!EsUtil.existIndex(client, indexMapping.getIndex())) {
                this.doCreateIndex(client, indexMapping.getIndex(), indexMapping.getxContentBuilder());
            }
        }
    }

    /**
     * build index
     * @param client
     * @param indexName
     * @param xContentBuilder
     */
    private boolean doCreateIndex(RestHighLevelClient client, String indexName, XContentBuilder xContentBuilder) {
        boolean flag = false;
        try {
            CreateIndexRequest request = new CreateIndexRequest(indexName);
            request.settings(Settings.builder().put("index.number_of_shards", 1).put("index.number_of_replicas", 0));
            request.mapping(xContentBuilder);
            flag = client.indices().create(request, RequestOptions.DEFAULT).isAcknowledged();
        } catch (IOException e) {
            LOGGER.error("Build index is failed!",indexName, e);
        }
        return flag;
    }

    /**
     * @param jsonPath
     * @return
     */
    private List<IndexMapping> parseIndicesConfFile(String jsonPath) {
        if(Files.notExists(Paths.get(jsonPath))) {
            LOGGER.error("Index mapping does't exist!", jsonPath);
        }
        File file = Paths.get(jsonPath).toFile();
        List<IndexMapping> indexMappings = null;
        try {
            String jsonStr = IOUtils.toString(file.toURI(), StandardCharsets.UTF_8);
            JSONArray indices = JSON.parseArray(jsonStr);
            indexMappings = doParseIndices(indices);
        } catch (IOException e) {
            LOGGER.error("Unable to build index!", file.getAbsoluteFile(), e);
        }
        return indexMappings;
    }

    /**
     * @param indices
     * @return
     */
    private List<IndexMapping> doParseIndices(JSONArray indices) {
        List<IndexMapping> indexMappings = CollectionUtil.newArrayList();
        if(indices.isEmpty()) {
            return indexMappings;
        }
        for(Object index : indices) {
            if(index instanceof JSONObject) {
                doParseIndex((JSONObject)index, indexMappings);
            } else {
                LOGGER.error("Unable to parse index, only json format content can be parsed!", index);
            }
        }
        return indexMappings;
    }

    /**
     * @param idx
     * @param indexMappings
     */
    private void doParseIndex(JSONObject idx, List<IndexMapping> indexMappings) {
        if(idx.isEmpty()) {
            return;
        }
        String indexName = idx.keySet().iterator().next(); // index name
        Object mappingsObj = ((Map<String, Object>)idx.get(indexName)).get("mappings");
        if(null == mappingsObj) {
            return;
        }
        Map<String, Object> mappings = (Map<String, Object>) mappingsObj;
        String typeName = mappings.keySet().iterator().next(); // type name

        IndexMapping indexMapping = new IndexMapping();
        indexMapping.setIndex(indexName);
        indexMappings.add(indexMapping);
        Object propertiesObj = mappings.get(typeName);
        Map<String, Object> properties = (Map<String, Object>)propertiesObj;
        if(null == properties || properties.isEmpty()) {
            return;
        }

        //build mapping
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder().map(mappings);
            indexMapping.setxContentBuilder(builder);
        } catch (IOException e) {
            LOGGER.error("Build index is failed!", e);
        }
    }

}

class IndexMapping {
    String index;
    XContentBuilder xContentBuilder;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public XContentBuilder getxContentBuilder() {
        return xContentBuilder;
    }

    public void setxContentBuilder(XContentBuilder xContentBuilder) {
        this.xContentBuilder = xContentBuilder;
    }
}

