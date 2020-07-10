package co.edu.icesi.metrocali.atc.evaluator.expression;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.entities.evaluator.Measurement;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;
import co.edu.icesi.metrocali.atc.services.evaluator.MeasurementsService;
import co.edu.icesi.metrocali.atc.services.evaluator.VariableService;


@Service
public class Executor {
    public static Timestamp LAST_EXECTUTION = new Timestamp(System.currentTimeMillis());
    @Autowired
    private Context context;
    @Autowired
    private SpringExpressions interpreter;
    @Autowired
    private MeasurementsService measurementsService;
    @Autowired
    private VariableService variables;

    public Object evaluateExpression(String name, String expression) {
        Functions con = context.getRootObject();
        interpreter.setRootObject(con);
        try {
            Object result = interpreter.parseExpression(expression);
            if (name != null && !name.isEmpty()) {
                context.addVar(name, result, "variable temporal");
            }
            if (result == null) {
                throw new Exception();
            }
            return result;
        } catch (Exception e) {
            return "Error: la expresion '" + expression
                    + "' no es correcta, asegurese de cumplir con la sintaxis suministrada";
        }
    }

    public HashMap<String, Measurement> evaluateKPIs() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Date currentDate = Date.from(now.toInstant());
        List<Variable> kpis = variables.getKPIs();
        Functions con = context.getRootObject();
        context.fillVariables();
        interpreter.setRootObject(con);
        HashMap<String, Measurement> results = new HashMap<>();
        List<Measurement> measurements = new ArrayList<>();
        for (Variable variable : kpis) {

            String formulaExpression = variable.getLastFormulaExpression();
            double value = (double) interpreter.parseExpression(formulaExpression);
            Measurement measurement = new Measurement();
            measurement.setValue(value);
            measurement.setVariable(variable);
            measurements.add(measurement);
            measurement.setStartDate(Date.from(LAST_EXECTUTION.toInstant()));
            measurement.setEndDate(currentDate);
            results.put(variable.getNameVariable(), measurement);
        }
        measurementsService.saveMeasurements(measurements);
        LAST_EXECTUTION = now;
        restarVariable();
        return results;
    }

    void restarVariable() {
        context.setValueForVar(Context.EVENTSQHSS, new ArrayList<Integer>());
        context.setValueForVar(Context.EVENTS_DONE, new HashMap<Integer, Integer>());
        context.updateLastEvent();
    }

    // TODO: remove method and see expressionService.calculateVariables
    public HashMap<String, Measurement> temporalEvaluateKPI() {
        HashMap<String, Measurement> results = new HashMap<>();
        List<Variable> kpis = variables.getKPIs();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Date currentDate = Date.from(now.toInstant());
        List<Measurement> measurements = new ArrayList<>();
        for (Variable variable : kpis) {
            Measurement measurement = new Measurement();
            double value = Math.random() * 100;
            measurement.setValue(value);
            measurement.setVariable(variable);
            measurements.add(measurement);
            measurement.setStartDate(Date.from(LAST_EXECTUTION.toInstant()));
            measurement.setEndDate(currentDate);
        }
        List<Measurement> addedMeasurements = measurementsService.saveMeasurements(measurements);
        for(Measurement addedMeasurement : addedMeasurements){
            results.put(addedMeasurement.getVariable().getNameVariable(), addedMeasurement);
        }        
        LAST_EXECTUTION = now;
        return results;
    }

    public Object evaluateExpression(String expression) {
        return evaluateExpression(null, expression);
    }

}
