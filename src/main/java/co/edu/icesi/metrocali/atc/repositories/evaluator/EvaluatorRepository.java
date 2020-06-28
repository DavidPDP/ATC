package co.edu.icesi.metrocali.atc.repositories.evaluator;


import java.text.SimpleDateFormat;
import org.springframework.web.client.RestTemplate;

public abstract class EvaluatorRepository {

    static final String DATE_FORMAT = "yyyy/MM/dd";

    RestTemplate blackboxApi;

    String blackboxEvaluatorApiURL;

    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

    public EvaluatorRepository(RestTemplate blackboxApi, String blackboxEvaluatorApiURL) {

        this.blackboxApi = blackboxApi;
        this.blackboxEvaluatorApiURL = blackboxEvaluatorApiURL;

    }
}
