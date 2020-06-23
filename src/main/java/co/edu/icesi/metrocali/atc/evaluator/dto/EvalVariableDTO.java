package co.edu.icesi.metrocali.atc.evaluator.dto;


import java.util.List;
import co.edu.icesi.metrocali.atc.evaluator.entities.Formula;
import co.edu.icesi.metrocali.atc.evaluator.entities.Variable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EvalVariableDTO {


    public static final String IS_SYSTEM_VAR = "system";
    public static final String IS_KPI = "kpi";
    public static final String NO_KPI = "no_kpi";

    private String name;
    private String classification;
    private String description;
    private String formulaExpression;
    private String type;

    public EvalVariableDTO(Variable variable) {
        this.name = variable.getNameVariable();
        this.classification = variable.getClassification();
        this.description = variable.getDescriptionVar();


        this.formulaExpression = getCurrentFormulaExpression(variable);

        this.type = variable.getIsKPI() ? IS_KPI : NO_KPI;
    }

    private String getCurrentFormulaExpression(Variable variable) {
        List<Formula> formulas = variable.getFormulas();
        String currentFormulaExpression = null;
        for (int i = 0; i < formulas.size() && currentFormulaExpression == null; i++) {
            Formula formula = formulas.get(i);
            if (formula.getEndDate() == null) {
                currentFormulaExpression = formula.getFormulaExpression();
            }
        }
        return currentFormulaExpression;

    }
}
