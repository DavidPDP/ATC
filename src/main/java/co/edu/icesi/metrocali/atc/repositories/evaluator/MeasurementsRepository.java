package co.edu.icesi.metrocali.atc.repositories.evaluator;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import co.edu.icesi.metrocali.atc.entities.evaluator.Measurement;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;

public class MeasurementsRepository extends EvaluatorRepository {

    private static final String MEASUREMENTS_URL = "/measurements";
    private static final String NAMES_PARAM = "names";
    private static final String START_DATE_PARAM = "s_date";
    private static final String END_DATE_PARAM = "e_date";
    private static final String LAST_PARAM = "lasts";

    public MeasurementsRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
            @Value("${blackbox.apis.evaluator}") String blackboxEvaluatorApiURL) {
        super(blackboxApi, blackboxEvaluatorApiURL);
    }

    public Optional<Measurement> save(Measurement measurement) {
        List<Measurement> measurements = Arrays.asList(measurement);
        Measurement newMeasurement = null;
        List<Measurement> newMeasurements = saveAll(measurements);
        if (newMeasurements.size() > 0) {
            newMeasurement = newMeasurements.get(0);
        }
        return Optional.ofNullable(newMeasurement);
    }

    public List<Measurement> saveAll(List<Measurement> measurements) {
        HttpEntity<List<Measurement>> requestEntity =
                new HttpEntity<List<Measurement>>(measurements);
        List<Measurement> newMeasurements =
                blackboxApi.exchange(blackboxEvaluatorApiURL + MEASUREMENTS_URL, HttpMethod.POST,
                        requestEntity, new ParameterizedTypeReference<List<Measurement>>() {
                        }).getBody();
        return newMeasurements;
    }

    public List<Measurement> retrieveByVariableAndBetweenDates(Variable variable, Date start,
            Date end) {
        HashMap<String, Object> uriVariables = new HashMap<>();
        uriVariables.put(NAMES_PARAM, variable.getNameVariable());
        uriVariables.put(START_DATE_PARAM, start);
        uriVariables.put(END_DATE_PARAM, end);

        List<Measurement> measurements =
                blackboxApi.exchange(blackboxEvaluatorApiURL + MEASUREMENTS_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Measurement>>() {
                        }, uriVariables).getBody();

        return measurements;
    }

    public List<Measurement> retrieveLastMeasurements(Variable variable) {
        HashMap<String, Object> uriVariables = new HashMap<>();
        uriVariables.put(NAMES_PARAM, variable.getNameVariable());
        uriVariables.put(LAST_PARAM, true);

        List<Measurement> measurements =
                blackboxApi.exchange(blackboxEvaluatorApiURL + MEASUREMENTS_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Measurement>>() {
                        }, uriVariables).getBody();
        return measurements;
    }

    public List<Measurement> retrieveByVariableAndBetweenDates(List<String> variablesNames,
            Date start, Date end) {

        HashMap<String, Object> uriVariables = new HashMap<>();
        uriVariables.put(NAMES_PARAM, variablesNames);
        uriVariables.put(START_DATE_PARAM, start);
        uriVariables.put(END_DATE_PARAM, end);

        List<Measurement> measurements =
                blackboxApi.exchange(blackboxEvaluatorApiURL + MEASUREMENTS_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Measurement>>() {
                        }, uriVariables).getBody();

        return measurements;
    }

    public List<Measurement> retrieveLastMeasurements(List<String> variablesNames) {
        HashMap<String, Object> uriVariables = new HashMap<>();
        uriVariables.put(NAMES_PARAM, variablesNames);
        uriVariables.put(LAST_PARAM, true);

        List<Measurement> measurements =
                blackboxApi.exchange(blackboxEvaluatorApiURL + MEASUREMENTS_URL, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<Measurement>>() {
                        }, uriVariables).getBody();
        return measurements;
    }


}
