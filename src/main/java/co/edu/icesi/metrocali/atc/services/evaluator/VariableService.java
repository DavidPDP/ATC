package co.edu.icesi.metrocali.atc.services.evaluator;

import java.util.ArrayList;
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


    // TODO: En todos los getters se debe hacer el attacg de last formula. Ya sea que se deba
    // consultar a back o que se configure el back para que la traiga tal cual.
    public Optional<Variable> getVariable(String name) {

        Optional<Formula> formulaWrapper = formulasRepository.retriveByVariable(name);
        Variable variable = null;
        if (formulaWrapper.isPresent()) {
            variable = formulaWrapper.get().getVariable();
        }
        return Optional.ofNullable(variable);
    }

    public List<Variable> getVariables() {
        List<Formula> formulas = formulasRepository.retrieveActives();
        List<Variable> variables = new ArrayList<>();
        for (Formula formula : formulas) {
            Variable variable = formula.getVariable();
            variable.setLastFormulaExpression(formula.getExpression());
            variables.add(variable);
        }
        return variables;
    }

    public List<Variable> getKPIs() {
        List<Formula> formulas = formulasRepository.retrieveActivesByKPI();
        List<Variable> variables = new ArrayList<>();
        for (Formula formula : formulas) {
            Variable variable = formula.getVariable();
            variable.setLastFormulaExpression(formula.getExpression());
            variables.add(variable);
        }
        return variables;
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
