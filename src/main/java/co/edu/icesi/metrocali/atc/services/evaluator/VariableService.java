package co.edu.icesi.metrocali.atc.services.evaluator;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.Null;
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
        Optional<Variable> variableWrapper = variableRepository.update(newVariable);

        if (!variableWrapper.isPresent()) {
            throw new Exception("No se pudo actualizar la variable: " + name);
        }

        Optional<Formula> formulaWrapper = formulasRepository.retrieveActivesByVariable(name);


        if (!formulaWrapper.isPresent()) {
            throw new Exception("Variable: " + name + " no existe");
        }
        Formula lastFormula = formulaWrapper.get();
        newVariable = lastFormula.getVariable();
        newVariable.setClassification(classification);
        newVariable.setDescriptionVar(description);
        newVariable.setIsKPI(isKPI);

        Formula newFormula = new Formula();
        newFormula.setStartDate(now);
        newFormula.setEndDate(null);
        newFormula.setExpression(expression);
        newFormula.setVariable(newVariable);

        if (!lastFormula.getExpression().equals(expression)) {

            lastFormula.setEndDate(now);
            if (!formulasRepository.update(name, lastFormula).isPresent()) {
                throw new Exception("No se actualizó la última fórmula de la variable: " + name);
            }
            try {
                newFormula = formulasRepository.save(newFormula).get();
            } catch (Exception e) {
                lastFormula.setEndDate(null);
                formulasRepository.update(name, lastFormula);
                throw new Exception("La formula para: " + name + " no fue asignada");
            }

        }


        newVariable = variableWrapper.get();
        newVariable.setLastFormulaExpression(newFormula.getExpression());

        return Optional.of(newVariable);
    }

    public Optional<Variable> saveVariable(String name, String classification, String description,
            String expression, boolean isKPI) throws Exception {

        Date now = Date.from(new Timestamp(System.currentTimeMillis()).toInstant());
        Optional<Variable> newVariable = Optional.empty();
        Variable variable = new Variable(name, classification, description, isKPI, expression);

        Formula formula = new Formula();
        formula.setExpression(expression);
        formula.setVariable(variable);
        formula.setStartDate(now);

        newVariable = variableRepository.save(variable);
        if (newVariable.isPresent()) {
            formula = formulasRepository.save(formula).get();
            variable = formula.getVariable();
            variable.setLastFormulaExpression(formula.getExpression());
        } else {
            throw new Exception("La variable no se agregó");
        }

        return Optional.of(variable);
    }

}
