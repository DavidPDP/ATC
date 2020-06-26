package co.edu.icesi.metrocali.atc.repositories.evaluator;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class EvalParameterRepository extends EvaluatorRepository {


    public EvalParameterRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
            @Value("${blackbox.apis.evaluator}") String blackboxEvaluatorApiURL) {
        super(blackboxApi, blackboxEvaluatorApiURL);
    }

    
}
