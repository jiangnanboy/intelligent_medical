package micro_service.controller;

import micro_service.service.*;
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
    private RelatedDiseaseService relatedService;
    private RelatedQueryService relatedQueryService;
    private CompletionService completionService;
    private DiagnosisService diagnosisService;

    public Controller(SearchService searchService, QAService qaService, RelatedDiseaseService relatedService, RelatedQueryService relatedQueryService, CompletionService completionService, DiagnosisService diagnosisService) {
        this.searchService = searchService;
        this.qaService = qaService;
        this.relatedService = relatedService;
        this.relatedQueryService = relatedQueryService;
        this.completionService = completionService;
        this.diagnosisService = diagnosisService;
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

        // 5.get related query
        get("/engine/relatedQuery", (req, res) -> relatedQueryService.getRelatedQuery(
                req.queryParams("query"),
                Integer.parseInt(req.queryParams("size"))
        ), json());

        // 6.get query completion
        get("/engine/completion", (req, res) -> completionService.queryCompletion(
                req.queryParams("query"),
                Integer.parseInt(req.queryParams("size"))
        ), json());

        // 7.disease diagnosis
        get("/engine/diagnosis", (req, res) -> diagnosisService.getDiagnoseResult(
                req.queryParams("symptom"),
                req.queryParams("sex"),
                Integer.parseInt(req.queryParams("age")),
                Integer.parseInt(req.queryParams("size"))
        ), json());

        after((req, res) -> res.type("application/json"));

        exception(IllegalArgumentException.class, (e, req, res) -> {
            res.status(400);
            res.body(toJson(new ResponseError(e)));
        });

    }

}

