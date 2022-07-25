package micro_service.controller;

import micro_service.service.SearchService;
import micro_service.utils.ResponseError;
import spark.servlet.SparkApplication;

import static micro_service.utils.JsonUtil.*;
import static spark.Spark.*;

/**
 * @author sy
 * @date 2022/7/21 20:22
 */
public class Controller implements SparkApplication {
    private SearchService searchService;


    public Controller(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public void init() {

        // access interface

        // 1.get disease id by query
        get("/engine/search", (req, res) -> searchService.searchDiseaseFromEs(
                req.queryParams("query"),
                Integer.parseInt(req.queryParams("currentPage")),
                Integer.parseInt(req.queryParams("size"))
        ), json());

        // 2.get disease triples by id
        get("/engine/kg", (req, res) -> searchService.getDiseaseKgFromNeo(
                req.queryParams("id")
        ), json());


        after((req, res) -> res.type("application/json"));

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });

    }

}

