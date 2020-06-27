package co.edu.icesi.metrocali.atc.evaluator.expression;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.entities.evaluator.Formula;
import co.edu.icesi.metrocali.atc.entities.evaluator.Measurement;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;
import co.edu.icesi.metrocali.atc.entities.events.Event;
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

    public Object isExpressionRigth(String expression) {
        Functions con = context.getRootObject();
        interpreter.setRootObject(con);
        try {
            Object result = interpreter.parseExpression(expression);
            return result;
        } catch (Exception e) {
            return false;
        }
    }

    public HashMap<String, Double> evaluateKPIs() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Date currentDate = Date.from(now.toInstant());
        List<Variable> kpis = variables.getKPIs();
        Functions con = context.getRootObject();
        interpreter.setRootObject(con);
        HashMap<String, Double> results = new HashMap<>();
        List<Measurement> measurements = new ArrayList<>();
        for (Variable variable : kpis) {

            Formula formula = variable.getLastFormula();
            double value = (double) interpreter.parseExpression(formula.getExpression());
            Measurement measurement = new Measurement();
            measurement.setValue(value);
            measurement.setVariable(variable);
            measurements.add(measurement);
            measurement.setStartDate(Date.from(LAST_EXECTUTION.toInstant()));
            measurement.setEndDate(currentDate);
            results.put(variable.getNameVariable(), value);
        }
        measurementsService.saveMeasurements(measurements);
        LAST_EXECTUTION = now;
        restarVariable();
        return results;
    }

    void restarVariable() {
        context.setValueForVar("eventsQHSs", new ArrayList<Integer>());
        context.setValueForVar("lastEvents", new ArrayList<Event>());
        context.setValueForVar("eventsDone", new HashMap<Integer, Integer>());
    }

    // TODO: remove method and see expressionService.calculateVariables
    public void temporalEvaluateKPI() {
        List<Variable> kpis = variables.getKPIs();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Date currentDate = Date.from(now.toInstant());
        List<Measurement> measurements = new ArrayList<>();
        for (Variable variable : kpis) {
            Measurement measurement = new Measurement();
            measurement.setValue(Math.random() * 100);
            measurement.setVariable(variable);
            measurements.add(measurement);
            measurement.setStartDate(Date.from(LAST_EXECTUTION.toInstant()));
            measurement.setEndDate(currentDate);
        }
        measurementsService.saveMeasurements(measurements);
        LAST_EXECTUTION = now;
    }

    public Object evaluateExpression(String expression) {
        return evaluateExpression(null, expression);
    }

}
