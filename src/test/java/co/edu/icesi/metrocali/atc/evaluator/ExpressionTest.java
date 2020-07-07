package co.edu.icesi.metrocali.atc.evaluator;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import co.edu.icesi.metrocali.atc.evaluator.expression.Context;
import co.edu.icesi.metrocali.atc.repositories.evaluator.MeasurementsRepository;
import co.edu.icesi.metrocali.atc.repositories.evaluator.VariableRepository;
import co.edu.icesi.metrocali.atc.services.evaluator.ExpressionsService;
import co.edu.icesi.metrocali.atc.vos.StateNotification;

@SpringBootTest
public class ExpressionTest {
    @Autowired
    private ExpressionsService expresion;
    @Autowired
    private VariableRepository variableRepository;
    @Autowired
    private Context context;
    @Autowired
    private MeasurementsRepository measurementsRepository;
    
    private void calculateKPIStage(){
        ObjectsToTest objects=ObjectsToTest.getInstance();
        List<StateNotification> increase=objects.getNotificationsIncrease();
        for (StateNotification stateNotification : increase) {
            context.update(stateNotification);
        }
        
    }

    @Test
    public void evaluateExpression(){
        assertNotNull(expresion);
        double value=(double)expresion.evaluateExpression("sum({1,2,3,4})");
        assertTrue(value==10);
    }
    @Test
    @Transactional
    public void calculateKPITest(){
        calculateKPIStage();
        expresion.calculateKPI();


    }
    @Test
    @Transactional
    public void addVariableTest(){

    }

}