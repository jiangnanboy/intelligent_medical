package micro_service.controller;

import micro_service.service.QAService;
import micro_service.service.RelatedService;
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
    private QAService qaService;
    private RelatedService relatedService;

    public Controller(SearchService searchService, QAService qaService, RelatedService relatedService) {
        this.searchService = searchService;
        this.qaService = qaService;
        this.relatedService = relatedService;
    }

    @Override
    public void init() {

        // access interface

        // 1.search disease from es by query
        get("/engine/search", (req, res) -> searchService.searchDiseaseFromEs(
                req.queryParams("query"),
                Integer.parseInt(req.queryParams("currentPage")),
                Integer.parseInt(req.queryParams("size"))
        ), json());

        // 2.get disease triples by id
        get("/engine/kg", (req, res) -> searchService.getDiseaseKgFromNeo(
                req.queryParams("id")
        ), json());


        // 3.get answer by question
        get("/engine/qa", (req, res) -> qaService.qa(
                req.queryParams("question")
        ), json());

        // 4.get related disease node
        get("/engine/related", (req, res) -> relatedService.getRelatedDisease(
                req.queryParams("id"),
                req.queryParams("query"),
                Integer.parseInt(req.queryParams("size"))
        ), json());

        after((req, res) -> res.type("application/json"));

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });

    }

}

