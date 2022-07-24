package sy.init.es;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.MainResponse;

import java.io.IOException;
import java.util.List;


/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class ESPool implements PooledObjectFactory<RestHighLevelClient>{

    List<String> host;
    String http;
    int port;

    public ESPool(List<String> host, String http, int port) {
        this.host = host;
        this.http = http;
        this.port = port;
    }

    public ESPool() {}

    public void setHost(List<String> host) {
        this.host = host;
    }
    public void setHttp(String http) {
        this.http = http;
    }
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public PooledObject<RestHighLevelClient> makeObject() throws Exception {
        RestHighLevelClient client = null;

//        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", "12345"));

        HttpHost[] httpHosts = new HttpHost[host.size()];
        for(int i = 0; i < host.size(); i ++) {
            HttpHost node = new HttpHost(host.get(i), port, http);
            httpHosts[i] = node;
        }
        try {
//            client = new RestHighLevelClient(RestClient.builder(httpHosts).setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)));
            client = new RestHighLevelClient(RestClient.builder(httpHosts));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new DefaultPooledObject<>(client);
    }

    @Override
    public void destroyObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
        RestHighLevelClient highLevelClient = pooledObject.getObject();
        highLevelClient.close();
        System.out.println("es client closed!");
    }

    @Override
    public void activateObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
        System.out.println("es activateObject");
    }

    @Override
    public boolean validateObject(PooledObject<RestHighLevelClient> pooledObject) {
        System.out.println("es check client!");
        RestHighLevelClient client = pooledObject.getObject();
        MainResponse info = null;
        try {
            info = client.info(RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return StringUtils.isNotBlank(info.getClusterUuid());
    }

    @Override
    public void passivateObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
        System.out.println("es passivateObject");
    }

}

