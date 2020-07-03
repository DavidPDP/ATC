package co.edu.icesi.metrocali.atc.api.evaluator;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvaluatedExpression;
import co.edu.icesi.metrocali.atc.evaluator.expression.FunctionInfo;
import co.edu.icesi.metrocali.atc.services.evaluator.ExpressionsService;
import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping("atc/evaluator/expressions")
@Log4j2
public class HTTPRestExpressionsAPI {

    @Autowired
    private ExpressionsService expressionsService;

    // Expressions
    @PostMapping
    public ResponseEntity<Object> getEvaluatedExpression(
            @RequestBody EvaluatedExpression expressionWrapper) {
        String expression = expressionWrapper.getExpression();
        try {
            Object result = expressionsService.evaluateExpression(expression);
            EvaluatedExpression expressionDTO = new EvaluatedExpression(expression, result);
            return ResponseEntity.ok(expressionDTO);
        } catch (ExpressionException exception) {
            EvaluatedExpression expressionErrorResult =
                    EvaluatedExpression.getEvalEvaluatedExpressionResult(expression, exception);
            return ResponseEntity.badRequest().body(expressionErrorResult);
        } catch (Exception e) {
            log.error("Error at POST /evaluator/evaluateExpression", e);
            throw e;
        }
    }

    @GetMapping(value = "/functions")
    public ResponseEntity<List<FunctionInfo>> getFunctionsInfo() {
        List<FunctionInfo> info = expressionsService.getFunctionInfo();
        return ResponseEntity.ok().body(info);
    }

}
