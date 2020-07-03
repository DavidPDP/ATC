package co.edu.icesi.metrocali.atc.repositories.evaluator;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import co.edu.icesi.metrocali.atc.entities.evaluator.Formula;
import org.springframework.stereotype.Repository;

@Repository
public class FormulasRepository extends EvaluatorRepository {

        private static final String FORMULAS_URL = "/formulas";
        private static final String KPI_URL = "/kpi";
        private static final String ACTIVE_URL = "/active";


        public FormulasRepository(@Qualifier("blackboxApi") RestTemplate blackboxApi,
                        @Value("${blackbox.apis.evaluator}") String blackboxEvaluatorApiURL) {
                super(blackboxApi, blackboxEvaluatorApiURL);
        }

        public List<Formula> retrieveActives() {
                List<Formula> formulas = blackboxApi.exchange(
                                blackboxEvaluatorApiURL + FORMULAS_URL + ACTIVE_URL, HttpMethod.GET,
                                null, new ParameterizedTypeReference<List<Formula>>() {
                                }).getBody();
                return formulas;

        }

        public Optional<Formula> retriveByVariable(String name) {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + FORMULAS_URL);
                uriBuilder.pathSegment(name);
                Formula formula = blackboxApi.exchange(uriBuilder.toUriString(), HttpMethod.GET,
                                null, Formula.class).getBody();
                return Optional.ofNullable(formula);

        }

        public List<Formula> retrieveActivesByKPI() {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + FORMULAS_URL);
                uriBuilder.path(KPI_URL);
                uriBuilder.path(ACTIVE_URL);
                List<Formula> formulas =
                                blackboxApi.exchange(uriBuilder.toUriString(), HttpMethod.GET, null,
                                                new ParameterizedTypeReference<List<Formula>>() {
                                                }).getBody();
                return formulas;
        }

        public Optional<Formula> save(Formula formula) {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + FORMULAS_URL);
                uriBuilder.pathSegment(formula.getVariable().getNameVariable());
                Formula newFormula = blackboxApi
                                .postForEntity(uriBuilder.toUriString(), formula, Formula.class)
                                .getBody();
                return Optional.ofNullable(newFormula);
        }

        public Optional<Formula> retrieveActivesByVariable(String name) {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + FORMULAS_URL);
                uriBuilder.pathSegment(name);
                uriBuilder.path(ACTIVE_URL);
                Formula formula = blackboxApi.exchange(uriBuilder.toUriString(), HttpMethod.GET,
                                null, Formula.class).getBody();
                return Optional.ofNullable(formula);
        }

        public Optional<Formula> update(String variableName, Formula formula) {
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                                .fromHttpUrl(blackboxEvaluatorApiURL + FORMULAS_URL);
                uriBuilder.pathSegment(variableName);
                Formula newFormula = blackboxApi
                                .exchange(uriBuilder.toUriString(), HttpMethod.PUT,
                                                new HttpEntity<Formula>(formula), Formula.class)
                                .getBody();
                return Optional.ofNullable(newFormula);
        }

}
