package co.edu.icesi.metrocali.atc.api.evaluator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalParameter;
import co.edu.icesi.metrocali.atc.services.evaluator.EvalParametersService;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("atc/evaluator/parameters")
@Log4j2
public class HTTPParametersAPI {

    @Autowired
    private EvalParametersService parametersService;

    @GetMapping
    public ResponseEntity<List<EvalParameter>> getParameters(
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "enable_from") Date enableStart,
            @RequestParam(required = false, name = "enable_until") Date enableEnd)
            throws Exception {

        try {
            List<EvalParameter> parameters = new ArrayList<>();

            parameters = parametersService.getParameters(name, enableStart, enableEnd);

            return ResponseEntity.ok().body(parameters);
        } catch (Exception e) {
            log.error("Error at GET /parameters", e);
            throw e;
        }

    }

    @GetMapping("/active")
    public ResponseEntity<List<EvalParameter>> getActiveParameters()
            throws Exception {

        try {
            List<EvalParameter> parameters = new ArrayList<>();

            parameters = parametersService.getActiveParameters();

            return ResponseEntity.ok().body(parameters);
        } catch (Exception e) {
            log.error("Error at GET /parameters", e);
            throw e;
        }

    }

    @PutMapping(value = "/{parameterName}")
    public ResponseEntity<?> updateParameter(@PathVariable(required = true) String parameterName,
            @RequestBody(required = true) EvalParameter parameter) throws Exception {

        try {
            parametersService.updateParameterValue(parameterName, parameter.getValue());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("at POST /parameters/{parameterName}", e);
            throw e;
        }
    }

}
