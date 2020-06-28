package co.edu.icesi.metrocali.atc.repositories.evaluator;

import java.util.Date;

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
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalParameter;

@Repository
public class EvalParametersRepository extends EvaluatorRepository {

        private static final String PARAMETERS_URL = "/parameters";
        private static final String ACTIVE_URL_PATH = "/active";
        private static final String FILTERED_URL_PATH = "/filtered";

        private static final String ENABLE_FROM_PARAM = "enable_from";
        private static final String ENABLE_UNTIL_PARAM = "enable_until";


        public EvalParametersRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
                        @Value("${blackbox.apis.evaluator}") String blackboxEvaluatorApiURL) {
                super(blackboxApi, blackboxEvaluatorApiURL);
        }

        public Optional<EvalParameter> retrieveActiveByName(String name) {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + PARAMETERS_URL);
                uriBuilder.pathSegment(name);
                uriBuilder.path(ACTIVE_URL_PATH);
                EvalParameter parameter = blackboxApi.exchange(uriBuilder.toUriString(),
                                HttpMethod.GET, null, EvalParameter.class).getBody();
                return Optional.ofNullable(parameter);
        }

        public List<EvalParameter> retrieveByName(String name, Date start, Date end) {

                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + PARAMETERS_URL);
                uriBuilder.pathSegment(name);
                uriBuilder.path(FILTERED_URL_PATH);
                uriBuilder.queryParam(ENABLE_FROM_PARAM, start);
                uriBuilder.queryParam(ENABLE_UNTIL_PARAM, end);
                List<EvalParameter> parameters = blackboxApi.exchange(uriBuilder.toUriString(),
                                HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<EvalParameter>>() {
                                }).getBody();
                return parameters;
        }

        public List<EvalParameter> retrieveByName(String name) {

                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + PARAMETERS_URL);
                uriBuilder.pathSegment(name);

                List<EvalParameter> parameters = blackboxApi.exchange(uriBuilder.toUriString(),
                                HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<EvalParameter>>() {
                                }).getBody();
                return parameters;
        }

        public List<EvalParameter> retrieveBetweenDates(Date start, Date end) {

                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + PARAMETERS_URL);
                uriBuilder.queryParam(ENABLE_FROM_PARAM, start);
                uriBuilder.queryParam(ENABLE_UNTIL_PARAM, end);
                List<EvalParameter> parameters = blackboxApi.exchange(uriBuilder.toUriString(),
                                HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<EvalParameter>>() {
                                }).getBody();
                return parameters;
        }

        public List<EvalParameter> retrieveAll() {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + PARAMETERS_URL);
                List<EvalParameter> parameters = blackboxApi.exchange(uriBuilder.toUriString(),
                                HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<EvalParameter>>() {
                                }).getBody();
                return parameters;
        }

        public List<EvalParameter> retrieveActives() {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + PARAMETERS_URL);
                uriBuilder.path(ACTIVE_URL_PATH);

                List<EvalParameter> parameters = blackboxApi.exchange(uriBuilder.toUriString(),
                                HttpMethod.GET, null,
                                new ParameterizedTypeReference<List<EvalParameter>>() {
                                }).getBody();
                return parameters;
        }

        public Optional<EvalParameter> update(EvalParameter parameter) {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + PARAMETERS_URL);
                uriBuilder.pathSegment(parameter.getName());
                HttpEntity<EvalParameter> parameterWrapper = new HttpEntity<>(parameter);
                EvalParameter newParameter = blackboxApi.exchange(uriBuilder.toUriString(),
                                HttpMethod.PUT, parameterWrapper, EvalParameter.class).getBody();

                return Optional.ofNullable(newParameter);
        }

}
