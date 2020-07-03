package co.edu.icesi.metrocali.atc.evaluator;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import co.edu.icesi.metrocali.atc.evaluator.expression.Context;
import co.edu.icesi.metrocali.atc.evaluator.expression.Functions;
import co.edu.icesi.metrocali.atc.evaluator.expression.SpringExpressions;
import co.edu.icesi.metrocali.atc.repositories.CategoriesRepository;
import co.edu.icesi.metrocali.atc.repositories.OperatorsRepository;
import co.edu.icesi.metrocali.atc.repositories.evaluator.EvalParametersRepository;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class ContextTest {
    
    @InjectMocks
    private Context context;
    @Mock
    private Functions functions;

    @Mock
    private SpringExpressions interpreter;

    @Mock
    private EvalParametersRepository parameters;

    // AVIOM repositories

    @Mock
    private CategoriesRepository categories;
    @Mock
    private OperatorsRepository operators;

    public void queueStage(){
       
    }
    @Test
    public void queueSizeTest(){
        // queueStage();
        // List<Integer> sizes=(List<Integer>)context.getVar(Context.EVENTSQHSS);
        // List<Integer> sizesDay=(List<Integer>)context.getVar(Context.EVENTSQHSS_Day);
        // int[] right={4,2,6};
        // for (int i = 0; i < sizes.size(); i++) {
        //     assertTrue(right[i]==sizes.get(i));
        //     assertTrue(right[i]==sizesDay.get(i));
        // }
    }
}