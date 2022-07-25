package micro_service;

import micro_service.config.Config;
import micro_service.controller.Controller;
import micro_service.service.SearchService;

/**
 * @author sy
 * @date 2022/7/21 22:22
 */
public class IntelligentServer {

    public static void main(String ... args) {
        Config.initPort(4567);
//        new Controller(new SearchService(), new QaService()).init();
        new Controller(new SearchService()).init();
    }

}

