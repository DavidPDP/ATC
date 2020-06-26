package co.edu.icesi.metrocali.atc.repositories.evaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;

@Repository
public class VariableRepository extends EvaluatorRepository {

    private static final String VARIABLE_URL = "/variables";
    private static final String FILTERED_BY_URL = "/filteredBy";
    private static final String IS_KPI_PARAM = "name";

    public VariableRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
            @Value("${blackbox.apis.evaluator}") String blackboxEvaluatorApiURL) {
        super(blackboxApi, blackboxEvaluatorApiURL);
    }

    public Optional<Variable> retrieveByName(String name) {
        Variable variable =
                blackboxApi.exchange(blackboxEvaluatorApiURL + VARIABLE_URL + "/" + name,
                        HttpMethod.GET, null, Variable.class).getBody();

        return Optional.ofNullable(variable);
    }

    public List<Variable> retrieveAll() {
        List<Variable> variables = blackboxApi.exchange(blackboxEvaluatorApiURL + VARIABLE_URL,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Variable>>() {
                }).getBody();
        return variables;
    }

    public List<Variable> retrieveByIsKPI(boolean isKPI) {
        HashMap<String, Object> uriVariables = new HashMap<>();
        uriVariables.put(IS_KPI_PARAM, isKPI);
        List<Variable> variables =
                blackboxApi.exchange(blackboxEvaluatorApiURL + VARIABLE_URL + FILTERED_BY_URL,
                        HttpMethod.GET, null, new ParameterizedTypeReference<List<Variable>>() {
                        }, uriVariables).getBody();
        return variables;
    }

    public Optional<Variable> save(Variable variable) {
        Variable newVariable = blackboxApi
                .postForEntity(blackboxEvaluatorApiURL + VARIABLE_URL, variable, Variable.class)
                .getBody();
        return Optional.ofNullable(newVariable);
    }

    public Optional<Variable> update(Variable variable) {

        Variable newVariable = blackboxApi.exchange(blackboxEvaluatorApiURL + VARIABLE_URL,
                HttpMethod.PUT, new HttpEntity<>(variable), Variable.class).getBody();
        return Optional.ofNullable(newVariable);
    }



}
