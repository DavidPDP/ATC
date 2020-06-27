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
import org.springframework.web.util.UriComponentsBuilder;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;

@Repository
public class VariableRepository extends EvaluatorRepository {

    private static final String VARIABLE_URL = "/variables";
    private static final String FILTERED_URL_PATH = "/filteredBy";
    private static final String IS_KPI_PARAM = "name";

    public VariableRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
            @Value("${blackbox.apis.evaluator}") String blackboxEvaluatorApiURL) {
        super(blackboxApi, blackboxEvaluatorApiURL);
    }

    public Optional<Variable> retrieveByName(String name) {
        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromHttpUrl(blackboxEvaluatorApiURL + VARIABLE_URL);
        uriBuilder.pathSegment(name);
        Variable variable = blackboxApi
                .exchange(uriBuilder.toUriString(), HttpMethod.GET, null, Variable.class).getBody();

        return Optional.ofNullable(variable);
    }

    public List<Variable> retrieveAll() {
        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromHttpUrl(blackboxEvaluatorApiURL + VARIABLE_URL);
        List<Variable> variables = blackboxApi.exchange(uriBuilder.toUriString(), HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Variable>>() {
                }).getBody();
        return variables;
    }

    public List<Variable> retrieveByIsKPI(boolean isKPI) {
        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromHttpUrl(blackboxEvaluatorApiURL + VARIABLE_URL);
        uriBuilder.path(FILTERED_URL_PATH);
        uriBuilder.queryParam(IS_KPI_PARAM, isKPI);
        HashMap<String, Object> uriVariables = new HashMap<>();
        List<Variable> variables = blackboxApi.exchange(uriBuilder.toUriString(), HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Variable>>() {
                }, uriVariables).getBody();
        return variables;
    }

    public Optional<Variable> save(Variable variable) {
        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromHttpUrl(blackboxEvaluatorApiURL + VARIABLE_URL);
        Variable newVariable = blackboxApi
                .postForEntity(uriBuilder.toUriString(), variable, Variable.class).getBody();
        return Optional.ofNullable(newVariable);
    }

    public Optional<Variable> update(Variable variable) {

        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromHttpUrl(blackboxEvaluatorApiURL + VARIABLE_URL);
        Variable newVariable = blackboxApi.exchange(uriBuilder.toUriString(), HttpMethod.PUT,
                new HttpEntity<>(variable), Variable.class).getBody();
        return Optional.ofNullable(newVariable);
    }



}
