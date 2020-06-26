package co.edu.icesi.metrocali.atc.entities.evaluator;

import org.springframework.expression.ExpressionException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluatedExpression {

    private String expression;
    private String valueType;
    private Object value;

    public EvaluatedExpression(String expression, Object value) {
        this.expression = expression;
        this.value = value;
        this.valueType = value.getClass().getName();
    }

    public EvaluatedExpression(String expression, ExpressionException exception) {
        this.expression = expression;
        this.value = exception.toDetailedString();
        this.valueType = exception.getClass().getName();
    }

    public static EvaluatedExpression getEvalEvaluatedExpressionResult(String expression,
            ExpressionException e) {

        return new EvaluatedExpression(expression, e);
    }

}
