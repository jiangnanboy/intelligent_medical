package micro_service.utils;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import spark.ResponseTransformer;

/**
 * @author sy
 * @date 2022/7/21 20:22
 */
public class JsonUtil {
    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    public static ResponseTransformer json() {
        return JsonUtil::toJson;
    }

    public static String fastToJson(Object object) {
        return JSONArray.toJSONString(object);
    }

    public static ResponseTransformer fastJson() {
        return JsonUtil::fastToJson;
    }

}
