package co.edu.icesi.metrocali.atc.api.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;
import co.edu.icesi.metrocali.atc.services.evaluator.ExpressionsService;
import co.edu.icesi.metrocali.atc.services.evaluator.VariableService;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("atc/evaluator/variables")
@Log4j2
public class HTTPRestVariablesAPI {

    @Autowired
    private VariableService variableService;

    @Autowired
    private ExpressionsService expressionsService;

    @GetMapping
    public ResponseEntity<Object> getVariables() {

        List<Variable> variables = new ArrayList<>();
        try {
            variables = variableService.getVariables();

            HashMap<String, String> systemVariables = expressionsService.getVariables();
            if (systemVariables != null) {
                for (String key : systemVariables.keySet()) {
                    String description = systemVariables.get(key);
                    Variable variable = new Variable();
                    variable.setNameVariable(key);
                    variable.setClassification("Variable del sistema");
                    variable.setIsKPI(false);
                    variable.setDescriptionVar(description);
                    variables.add(variable);
                }
            }

            return ResponseEntity.ok().body(variables);
        } catch (Exception e) {
            log.error("error at GET /evaluator/variables", e);
            throw e;
        }

    }

    @PostMapping()
    public ResponseEntity<Variable> saveVariable(@RequestBody Variable variable) throws Exception {

        try {

            Object result =
                    expressionsService.evaluateExpression(variable.getLastFormulaExpression());
            if (result instanceof String) {
                if (((String) result).startsWith("Error:")) {
                    throw new Exception((String) result);
                }
            }

            Optional<Variable> variableWrapper =
                    variableService.saveVariable(variable.getNameVariable(),
                            variable.getClassification(), variable.getDescriptionVar(),
                            variable.getLastFormulaExpression(), variable.getIsKPI());
            Variable newVariable = null;
            if (variableWrapper.isPresent()) {
                newVariable = variableWrapper.get();
            } else {
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok().body(newVariable);
        } catch (Exception e) {
            log.error("Error at POST /variables", e);
            throw e;
        }

    }

    @PutMapping(value = "/{variable_name}")
    public ResponseEntity<Variable> updateVariable(
            @PathVariable(name = "variable_name", required = true) String variableName,
            @RequestBody Variable variable) throws Exception {

        try {
            Object result =
                    expressionsService.evaluateExpression(variable.getLastFormulaExpression());
            if (result instanceof String) {
                if (((String) result).startsWith("Error:")) {
                    throw new Exception((String) result);
                }
            }
            Optional<Variable> variableWrapper = variableService.updateVariable(variableName,
                    variable.getClassification(), variable.getDescriptionVar(),
                    variable.getLastFormulaExpression(), variable.getIsKPI());

            Variable newVariable = null;
            if (variableWrapper.isPresent()) {
                newVariable = variableWrapper.get();
            } else {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().body(newVariable);
        } catch (Exception e) {
            log.error("Error at POST /variables", e);
            throw e;
        }

    }
}
