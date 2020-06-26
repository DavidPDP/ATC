package co.edu.icesi.metrocali.atc.repositories.evaluator;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import co.edu.icesi.metrocali.atc.entities.evaluator.Formula;

public class FormulasRepository extends EvaluatorRepository {

    private static final String FORMULAS_URL = "/parameters";
    private static final String FILTERED_URL = "/filtered";
    private static final String ACTIVE_PARAM = "active";
    

    public FormulasRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
            @Value("${blackbox.apis.evaluator}") String blackboxEvaluatorApiURL) {
        super(blackboxApi, blackboxEvaluatorApiURL);
    }

    public Optional<Formula> save(Formula formula) {
        Formula newFormula = blackboxApi
                .postForEntity(blackboxEvaluatorApiURL + FORMULAS_URL, formula, Formula.class)
                .getBody();
        return Optional.ofNullable(newFormula);
    }

}
