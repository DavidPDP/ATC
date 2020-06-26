package co.edu.icesi.metrocali.atc.repositories.evaluator;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalParameter;

@Repository
public class EvalParameterRepository extends EvaluatorRepository {

    private static final String PARAMETERS_URL = "/parameters";
    private static final String NAME_PARAM = "name";
    private static final String ENABLE_FROM_PARAM = "enable_from";
    private static final String ENABLE_UNTIL_PARAM = "enable_until";
    private static final String ACTIVE_PARAM = "active";

    public EvalParameterRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
            @Value("${blackbox.apis.evaluator}") String blackboxEvaluatorApiURL) {
        super(blackboxApi, blackboxEvaluatorApiURL);
    }

    public EvalParameter retrieveByName(String name, boolean active) {
        HashMap<String, Object> uriParams = new HashMap<>();
        uriParams.put(NAME_PARAM, name);
        uriParams.put(ACTIVE_PARAM, active);
        EvalParameter parameter = blackboxApi.exchange(blackboxEvaluatorApiURL + PARAMETERS_URL,
                HttpMethod.GET, null, EvalParameter.class, uriParams).getBody();
        return parameter;
    }

    public List<EvalParameter> retrieveByName(String name, Date start, Date end) {

        HashMap<String, Object> uriParams = new HashMap<>();
        uriParams.put(NAME_PARAM, name);
        uriParams.put(ENABLE_FROM_PARAM, start);
        uriParams.put(ENABLE_UNTIL_PARAM, end);
        List<EvalParameter> parameters =
                blackboxApi.exchange(blackboxEvaluatorApiURL + PARAMETERS_URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<EvalParameter>>() {
                        }, uriParams).getBody();
        return parameters;
    }

    public List<EvalParameter> retrieveByName(String name) {
        HashMap<String, Object> uriParams = new HashMap<>();
        uriParams.put(NAME_PARAM, name);
        List<EvalParameter> parameters =
                blackboxApi.exchange(blackboxEvaluatorApiURL + PARAMETERS_URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<EvalParameter>>() {
                        }, uriParams).getBody();
        return parameters;
    }

    public List<EvalParameter> retrieveBetweenDates(Date start, Date end) {
        HashMap<String, Object> uriParams = new HashMap<>();
        uriParams.put(ENABLE_FROM_PARAM, start);
        uriParams.put(ENABLE_UNTIL_PARAM, end);
        List<EvalParameter> parameters =
                blackboxApi.exchange(blackboxEvaluatorApiURL + PARAMETERS_URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<EvalParameter>>() {
                        }, uriParams).getBody();
        return parameters;
    }

    public List<EvalParameter> retrieveAll() {
        List<EvalParameter> parameters =
                blackboxApi.exchange(blackboxEvaluatorApiURL + PARAMETERS_URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<EvalParameter>>() {
                        }).getBody();
        return parameters;
    }

    public List<EvalParameter> retrieveActives() {
        HashMap<String, Object> uriParams = new HashMap<>();
        uriParams.put(ACTIVE_PARAM, true);
        List<EvalParameter> parameters =
                blackboxApi.exchange(blackboxEvaluatorApiURL + PARAMETERS_URL, HttpMethod.GET, null,
                        new ParameterizedTypeReference<List<EvalParameter>>() {
                        }, uriParams).getBody();
        return parameters;
    }

    public EvalParameter update(EvalParameter parameter) {
        EvalParameter newParameter = blackboxApi.exchange(blackboxEvaluatorApiURL + PARAMETERS_URL,
                HttpMethod.PUT, null, EvalParameter.class).getBody();

        return newParameter;
    }

}
