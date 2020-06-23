package co.edu.icesi.metrocali.atc.evaluator.services;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.evaluator.entities.Formula;
import co.edu.icesi.metrocali.atc.evaluator.entities.Variable;
import co.edu.icesi.metrocali.atc.evaluator.repositories.FormulasRepository;
import co.edu.icesi.metrocali.atc.evaluator.repositories.VariableRepository;




@Service
public class VariableService {

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private FormulasRepository formulasRepository;


    public Variable getVariable(String name) {
        return variableRepository.findByNameVariable(name);
    }

    public List<Variable> getVariables() {
        return variableRepository.findAll();
    }

    public List<Variable> getKPIs() {
        return variableRepository.findByIsKPI(true);
    }

    public Variable saveVariable(String name, String classification, String desc, String expression,
            boolean isKpi) {
        Variable newVariable = new Variable();
        newVariable.setNameVariable(name);
        boolean variableExists = existsVariable(name);
        if (variableExists) {
            newVariable = variableRepository.findByNameVariable(name);
        }
        newVariable.setClassification(classification);
        newVariable.setDescriptionVar(desc);
        newVariable.setIsKPI(isKpi);
        if (!variableExists) {
            // If variable exists then save method throw exception. Because, when new variable is
            // retrieved from DB, is in application transaction
            newVariable = variableRepository.save(newVariable);
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Formula formula = new Formula();
        formula.setFormulaExpression(expression);
        formula.setSartDate(now);
        // add formula already set formula's variable as newVariable and updates the date of last
        // formula
        newVariable.addFormula(formula);

        // get last available formula
        formulasRepository.findByVariableAndEndDateIsNull(newVariable)
                .forEach(element -> element.setEndDate(now));
        formulasRepository.save(formula);


        return newVariable;

    }

    public boolean existsVariable(String name) {
        return variableRepository.existsById(name);
    }

}
