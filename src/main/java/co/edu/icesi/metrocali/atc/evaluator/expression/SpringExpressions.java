package co.edu.icesi.metrocali.atc.evaluator.expression;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringExpressions {
    private ExpressionParser parser = new SpelExpressionParser();
    private StandardEvaluationContext context=new StandardEvaluationContext();


    public void setVariable(String name, Object val){
        context.setVariable(name, val);
    }
    public Object parseExpression(String expression){
        return parser.parseExpression(expression).getValue(context);
    }
    public void setRootObject(Object root){
        context.setRootObject(root);
    }
}