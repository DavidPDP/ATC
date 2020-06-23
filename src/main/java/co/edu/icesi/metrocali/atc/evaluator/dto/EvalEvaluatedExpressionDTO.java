package co.edu.icesi.metrocali.atc.evaluator.dto;

import org.springframework.expression.ExpressionException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvalEvaluatedExpressionDTO {

    private String expression;
    private String valueType;
    private Object value;

    public EvalEvaluatedExpressionDTO(String expression, Object value) {
        this.expression = expression;
        this.value = value;
        this.valueType = value.getClass().getName();
    }

    public EvalEvaluatedExpressionDTO(String expression, ExpressionException exception) {
        this.expression = expression;
        this.value = exception.toDetailedString();
        this.valueType = exception.getClass().getName();
    }

    public static EvalEvaluatedExpressionDTO getEvalEvaluatedExpressionResult(String expression,
            ExpressionException e) {

        return new EvalEvaluatedExpressionDTO(expression, e);
    }

}
