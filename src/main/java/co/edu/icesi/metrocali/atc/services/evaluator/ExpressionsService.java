package co.edu.icesi.metrocali.atc.services.evaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.constants.NotificationType;
import co.edu.icesi.metrocali.atc.entities.evaluator.Formula;
import co.edu.icesi.metrocali.atc.entities.evaluator.Measurement;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;
import co.edu.icesi.metrocali.atc.evaluator.expression.Context;
import co.edu.icesi.metrocali.atc.evaluator.expression.Executor;
import co.edu.icesi.metrocali.atc.evaluator.expression.FunctionInfo;
import co.edu.icesi.metrocali.atc.repositories.evaluator.FormulasRepository;
import co.edu.icesi.metrocali.atc.services.notifications.NotificationManager;
import co.edu.icesi.metrocali.atc.vos.StateNotification;


@Service
public class ExpressionsService {

    @Autowired
    private Context context;


    @Autowired
    private NotificationManager notificationService;

    @Autowired
    private Executor executor;

    @Autowired
    private VariableService variableService;

    @Autowired
    private FormulasRepository formulas;

    @Autowired
    private MeasurementsService measurementsService;

    @Value("${evaluator_ws_channels.base}")
    private String mainSubscriptionEvaluatorChannel;

    @Value("${evaluator_ws_channels.dashboard}")
    private String dashboardSubscriptionEvaluatorChannel;

    @PostConstruct
    public void postConstructor() {
        List<Variable> variables = variableService.getVariables();
        for (Variable variable : variables) {
            try {
                addVariable(variable);
            } catch (Exception e) {

            }
        }

    }


    public void calculateKPI() {

        StateNotification notification =
                new StateNotification(NotificationType.New_Measurements_Calculated,
                        Optional.empty(), new Object[] {executor.temporalEvaluateKPI()});
        notificationService.update(notification);

    }

    public void addVariable(Variable variable) throws Exception {
        List<Measurement> lastFive = measurementsService.getFiveLastMeasurements(variable);
        double last = 0;
        if (!lastFive.isEmpty()) {
            last = lastFive.get(0).getValue();
        }

        Optional<Formula> formulaWrapper =
                formulas.retrieveActivesByVariable(variable.getNameVariable());
        if (formulaWrapper.isPresent()) {
            Formula formula = formulaWrapper.get();
            Object result = executor.evaluateExpression(formula.getExpression());
            if (result instanceof String) {
                String string = (String) result;
                if (string.startsWith("Error:")) {
                    throw new Exception(string);
                }
            }

            context.setValueForVar(variable.getNameVariable(), last);
        } else {
            throw new Exception(
                    "No existe una fórmula para la variable: " + variable.getNameVariable());
        }

    }

    public Object evaluateExpression(String expression) {

        return executor.evaluateExpression(expression);
    }

    public List<FunctionInfo> getFunctionInfo() {
        return context.getFunctionInf();
    }


    public HashMap<String, String> getVariables() {
        return context.getVariablesDesc();
    }

}
