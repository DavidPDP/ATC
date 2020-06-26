package co.edu.icesi.metrocali.atc.repositories.evaluator;


import org.springframework.web.client.RestTemplate;

public abstract class EvaluatorRepository {
    private RestTemplate blackboxApi;

    private String blackboxEvaluatorApiURL;

    public EvaluatorRepository(RestTemplate blackboxApi, String blackboxEvaluatorApiURL) {

        this.blackboxApi = blackboxApi;
        this.blackboxEvaluatorApiURL = blackboxEvaluatorApiURL;

    }
}
