package co.edu.icesi.metrocali.atc.evaluator.api;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import co.edu.icesi.metrocali.atc.evaluator.dto.EvalMeasurementDTO;
import co.edu.icesi.metrocali.atc.evaluator.dto.EvalParameterDTO;
import co.edu.icesi.metrocali.atc.evaluator.dto.EvalVariableDTO;
import co.edu.icesi.metrocali.atc.evaluator.services.EvalParametersService;
import co.edu.icesi.metrocali.atc.evaluator.services.MeasurementsService;
import co.edu.icesi.metrocali.atc.evaluator.services.VariableService;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * EvaluatorRest
 */
@RestController()
@RequestMapping(path = "/evaluator")
@Log4j2
public class HTTPRestEvaluatorAPI {

    // @Autowired
    // private ExpressionsService expressionsService;

    @Autowired
    private EvalParametersService parametersService;

    @Autowired
    private VariableService variableService;

    @Autowired
    private MeasurementsService measurementsService;

    private HashMap<String, List<EvalMeasurementDTO>> getLastVariableMeasurements(
            List<String> variablesNames) throws Exception {
        HashMap<String, List<EvalMeasurementDTO>> measurementsByVariable = new HashMap<>();


        if (variablesNames != null && variablesNames.size() > 0) {
            for (String name : variablesNames) {
                List<EvalMeasurementDTO> measurements = new ArrayList<>();
                measurementsService.getFiveLastMeasurements(variableService.getVariable(name))
                        .forEach(measurement -> measurements
                                .add(new EvalMeasurementDTO(measurement)));
                measurementsByVariable.put(name, measurements);
            }

            return measurementsByVariable;
        } else {
            throw new Exception("Se debe especificar, al menos, el nombre de una variable");
        }

    }

    // dashboard
    /**
     * Returns a map with measurements for the indicated variables. Each measurements group is
     * ordered in an descending way
     * 
     * @param names     a list of variable names which measurements will be obtained
     * @param startDate a date which represents the 'from' date from measurements (for each
     *                  variable) will be obtained. Is an optional parameter
     * @param endDate   a date which represents the 'until' date until measurements (for each
     *                  variable) will be obtained. Is an optional parameter.
     * @param lasts     a boolean which specify if the values to calculate will be the last five
     *                  values. It overrides the dates parameters. if its value, the service will
     *                  return the last five measurements for each variable.
     * @return a hashmap (json) with measurements for each variable. key is variable name and value
     *         is a list with measurements for that variable.
     * @see #getLastVariableMeasurements
     */
    @GetMapping(value = "/measurements")
    public ResponseEntity<HashMap<String, List<EvalMeasurementDTO>>> getVariableMeasurements(
            @RequestParam(required = true, value = "names") List<String> variablesNames,
            @RequestParam(required = false, name = "s_date") Date startDate,
            @RequestParam(required = false, name = "e_date") Date endDate,
            @RequestParam(required = false, name = "lasts") boolean lasts) throws Exception {

        HashMap<String, List<EvalMeasurementDTO>> measurementsByVariable = new HashMap<>();

        if (variablesNames != null && variablesNames.size() > 0) {
            if (lasts) {
                measurementsByVariable = getLastVariableMeasurements(variablesNames);
            } else {
                // NOTE: Start timestamp: today
                Date start =
                        Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
                // NOTE: End timestamp: tomorrow
                Date end = Date.from(LocalDate.now().plusDays(1)
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());

                if (startDate != null) {
                    start = startDate;
                }
                if (endDate != null) {
                    end = endDate;
                }


                for (String name : variablesNames) {
                    List<EvalMeasurementDTO> measurements = new ArrayList<>();
                    measurementsService
                            .getSortedMeasurementsByEndDate(variableService.getVariable(name),
                                    start, end)
                            .forEach(measurement -> measurements
                                    .add(new EvalMeasurementDTO(measurement))

                            );
                    measurementsByVariable.put(name, measurements);
                }
            }
            return ResponseEntity.ok().body(measurementsByVariable);
        } else {
            throw new Exception("Se debe especificar, al menos, el nombre de una variable.");
        }


    }

    // Expressions
    //TODO: Uncomment when expressiones had been integrated
    // @PostMapping(value = "/evaluateExpression")
    // public ResponseEntity<Object> getEvaluatedExpression(
    //         @RequestBody EvalEvaluatedExpressionDTO expressionWrapper) {
    //     String expression = expressionWrapper.getExpression();
    //     System.out.println(expression);
    //     try {
    //         Object result = expressionsService.evaluateExpression(expression);
    //         EvalEvaluatedExpressionDTO expressionDTO =
    //                 new EvalEvaluatedExpressionDTO(expression, result);
    //         return ResponseEntity.ok(expressionDTO);
    //     } catch (ExpressionException exception) {
    //         EvalEvaluatedExpressionDTO expressionErrorResult = EvalEvaluatedExpressionDTO
    //                 .getEvalEvaluatedExpressionResult(expression, exception);
    //         return ResponseEntity.badRequest().body(expressionErrorResult);
    //     } catch (Exception e) {
    //         log.error("Error at GET /evaluator/evaluateExpression", e);
    //         throw e;
    //     }
    // }

    // @GetMapping(value = "/functions")
    // public ResponseEntity<List<FunctionInfo>> getFunctionsInfo() {
    //     List<FunctionInfo> info = expressionsService.getFunctionInfo();
    //     return ResponseEntity.ok().body(info);
    // }

    @GetMapping(value = "/variables")
    public ResponseEntity<Object> getVariables() {

        List<EvalVariableDTO> variables = new ArrayList<>();
        try {
            variableService.getVariables()
                    .forEach(variable -> variables.add(new EvalVariableDTO(variable)));

            //TODO: Uncomment when expressiones had been integrated
            // HashMap<String, String> systemVariables = expressionsService.getVariables();
            // if (systemVariables != null) {
            //     for (String key : systemVariables.keySet()) {
            //         String description = systemVariables.get(key);
            //         EvalVariableDTO variableDTO = new EvalVariableDTO();
            //         variableDTO.setName(key);
            //         variableDTO.setClassification("Variable del sistema");
            //         variableDTO.setType(EvalVariableDTO.IS_SYSTEM_VAR);
            //         variableDTO.setFormulaExpression("N/A");
            //         variableDTO.setDescription(description);
            //         variables.add(variableDTO);
            //     }
            // }

            return ResponseEntity.ok().body(variables);
        } catch (Exception e) {
            log.error("error at GET /evaluator/variables", e);
            throw e;
        }

    }

    //TODO: Uncomment when expressiones had been integrated
    // @PostMapping(value = "/variables")
    // public ResponseEntity<EvalVariableDTO> saveVariable(@RequestBody EvalVariableDTO variableDTO)
    //         throws Exception {

    //     try {
    //         if (variableService.existsVariable(variableDTO.getName())) {
    //             throw new Exception("La variable: " + variableDTO.getName() + " ya existe.");
    //         }
    //         Object result =
    //                 expressionsService.evaluateExpression(variableDTO.getFormulaExpression());
    //         if (result instanceof String) {
    //             if (((String) result).startsWith("Error:")) {
    //                 throw new Exception((String) result);
    //             }
    //         }
    //         Variable newVariable = variableService.saveVariable(variableDTO.getName(),
    //                 variableDTO.getClassification(), variableDTO.getDescription(),
    //                 variableDTO.getFormulaExpression(),
    //                 variableDTO.getType() == EvalVariableDTO.IS_KPI);
    //         EvalVariableDTO newVariableDTO = new EvalVariableDTO(newVariable);
    //         return ResponseEntity.ok().body(newVariableDTO);
    //     } catch (Exception e) {
    //         log.error("Error at POST /variables", e);
    //         throw e;
    //     }

    // }

    //TODO: Uncomment when expressiones had been integrated
    // @PutMapping(value = "/variables/{name}")
    // public ResponseEntity<EvalVariableDTO> updateVariable(@RequestBody EvalVariableDTO variableDTO)
    //         throws Exception {

    //     try {
    //         if (!variableService.existsVariable(variableDTO.getName())) {
    //             throw new Exception("La variable: " + variableDTO.getName() + " no existe.");
    //         }
    //         Object result =
    //                 expressionsService.evaluateExpression(variableDTO.getFormulaExpression());
    //         if (result instanceof String) {
    //             if (((String) result).startsWith("Error:")) {
    //                 throw new Exception((String) result);
    //             }
    //         }
    //         EvalVariableDTO updatedVariable =
    //                 new EvalVariableDTO(variableService.saveVariable(variableDTO.getName(),
    //                         variableDTO.getClassification(), variableDTO.getDescription(),
    //                         variableDTO.getFormulaExpression(),
    //                         variableDTO.getType().equalsIgnoreCase(EvalVariableDTO.IS_KPI)));
    //         return ResponseEntity.ok().body(updatedVariable);
    //     } catch (Exception e) {
    //         log.error("Error at POST /variables", e);
    //         throw e;
    //     }

    // }

    // Parameters
    @GetMapping(value = "/parameters")
    public ResponseEntity<List<EvalParameterDTO>> getParameters(
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "enable_from") Date enableStart,
            @RequestParam(required = false, name = "enable_until") Date enableEnd)
            throws Exception {

        try {
            List<EvalParameterDTO> parameters = new ArrayList<>();

            parametersService.getParameters(name, enableStart, enableEnd)
                    .forEach(parameter -> parameters.add(new EvalParameterDTO(parameter)));

            return ResponseEntity.ok().body(parameters);
        } catch (Exception e) {
            log.error("Error at GET /parameters", e);
            throw e;
        }

    }

    @PutMapping(value = "/parameters/{parameterName}")
    public ResponseEntity<?> updateParameter(@PathVariable(required = true) String parameterName,
            @RequestBody(required = true) EvalParameterDTO parameter) throws Exception {

        try {
            parametersService.updateParameterValue(parameterName, parameter.getValue());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("at POST /parameters/{parameterName}", e);
            throw e;
        }
    }
}
