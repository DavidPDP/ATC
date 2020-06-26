package co.edu.icesi.metrocali.atc.services.evaluator;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.entities.evaluator.Formula;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;
import co.edu.icesi.metrocali.atc.repositories.evaluator.FormulasRepository;
import co.edu.icesi.metrocali.atc.repositories.evaluator.VariableRepository;



@Service
public class VariableService {

    @Autowired
    private VariableRepository variableRepository;

    @Autowired
    private FormulasRepository formulasRepository;


    public Optional<Variable> getVariable(String name) {
        return variableRepository.retrieveByName(name);
    }

    public List<Variable> getVariables() {
        return variableRepository.retrieveAll();
    }

    public List<Variable> getKPIs() {
        return variableRepository.retrieveByIsKPI(true);
    }

    public Optional<Variable> saveVariable(String name, String classification, String description,
            String expression, boolean isKPI) {
        Optional<Variable> newVariable = Optional.empty();
        Optional<Variable> variableWrapper = variableRepository.retrieveByName(name);
        Variable variable = new Variable(name, classification, description, isKPI, null);
        if (variableWrapper.isPresent()) {
            variable = variableWrapper.get();
            variable.setClassification(classification);
            variable.setDescriptionVar(description);
            variable.setIsKPI(isKPI);
            newVariable = variableRepository.update(variable);
        } else {
            newVariable = variableRepository.save(variable);
        }

        Formula formula = new Formula();
        formula.setExpression(expression);

        formulasRepository.save(formula);
        return newVariable;

    }

}
