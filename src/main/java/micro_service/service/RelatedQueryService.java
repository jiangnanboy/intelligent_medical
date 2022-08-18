package micro_service.service;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.neo4j.driver.v1.Record;
import sy.es.EsSearch;
import sy.es.EsUtil;
import sy.init.es.ESPoolUtil;
import utils.CollectionUtil;
import utils.PropertiesReader;

import java.util.List;
import java.util.Optional;

/**
 * @author YanShi
 * @date 2022/8/18 20:26
 */
public class RelatedQueryService {
    String INDEX = PropertiesReader.get("medical_index");
    public List<String> getRelatedQuery(String query, int size) {
        List<String> queryList = null;
        if (StringUtils.isNotBlank(query)) {
            RestHighLevelClient client = null;
            try {
                client = ESPoolUtil.borrowClient();
                if (EsUtil.existIndex(client, INDEX)) {
                    queryList = EsSearch.getRelatedQuery(client, INDEX, query, size);
                }
            } finally {
                if (Optional.ofNullable(client).isPresent()) {
                    ESPoolUtil.returnClient(client);
                }
            }
        }
        return queryList;
    }
}
