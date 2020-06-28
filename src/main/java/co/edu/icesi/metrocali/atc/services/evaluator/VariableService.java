package co.edu.icesi.metrocali.atc.services.evaluator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public Optional<Variable> updateVariable(String name, String classification, String description,
            String expression, boolean isKPI) throws Exception {
        Date now = Date.from(new Timestamp(System.currentTimeMillis()).toInstant());
        Variable newVariable = new Variable(name, classification, description, isKPI, null);
        Optional<Variable> variableWrapper = variableRepository.retrieveByName(name);
        if (variableWrapper.isPresent()) {
            newVariable = variableWrapper.get();
            newVariable.setClassification(classification);
            newVariable.setDescriptionVar(description);
            newVariable.setIsKPI(isKPI);

            Formula newFormula = new Formula();
            newFormula.setStartDate(now);
            newFormula.setEndDate(null);
            newFormula.setExpression(expression);

            Optional<Formula> lastFormulaWrapper =
                    formulasRepository.retrieveActivesByVariable(name);

            if (!lastFormulaWrapper.isPresent()) {
                throw new Exception("Variable: " + name + " no tiene una última fórmula asignada");
            }

            Formula lastFormula = lastFormulaWrapper.get();

            lastFormula.setEndDate(now);
            formulasRepository.update(name, lastFormula);

            Optional<Formula> newFormulaWrapper = formulasRepository.save(newFormula);
            if (!newFormulaWrapper.isPresent()) {
                throw new Exception("Formula para: " + name + " no fue asignada");
            }
            newFormula = newFormulaWrapper.get();

            variableWrapper = variableRepository.update(newVariable);
            if (!variableWrapper.isPresent()) {
                throw new Exception("No se pudo actualizar la variable: " + name);
            }
            newVariable = variableWrapper.get();
            newVariable.setLastFormulaExpression(lastFormula.getExpression());

            return Optional.of(newVariable);

        } else {
            throw new Exception("Variable: " + name + " no existe");
        }
    }

    public Optional<Variable> saveVariable(String name, String classification, String description,
            String expression, boolean isKPI) {
        Optional<Variable> newVariable = Optional.empty();
        Variable variable = new Variable(name, classification, description, isKPI, expression);

        newVariable = variableRepository.save(variable);

        Formula formula = new Formula();
        formula.setExpression(expression);

        formulasRepository.save(formula);
        return newVariable;
    }

}
