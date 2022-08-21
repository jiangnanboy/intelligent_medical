package micro_service.service;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import sy.es.EsSearch;
import sy.es.EsUtil;
import sy.init.es.ESPoolUtil;
import utils.PropertiesReader;

import java.util.List;
import java.util.Optional;

/**
 * @author YanShi
 * @date 2022/8/21 17:28
 */
public class CompletionService {
    String INDEX = PropertiesReader.get("disea_comple_index");
    public List<String> queryCompletion(String query, int size) {
        List<String> queryList = null;
        if (StringUtils.isNotBlank(query)) {
            RestHighLevelClient client = null;
            try {
                client = ESPoolUtil.borrowClient();
                if (EsUtil.existIndex(client, INDEX)) {
                    queryList = EsSearch.getQueryCompletion(client, INDEX, query, size);
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
