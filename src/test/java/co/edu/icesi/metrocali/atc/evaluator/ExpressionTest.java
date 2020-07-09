package co.edu.icesi.metrocali.atc.evaluator;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.evaluator.expression.Context;
import co.edu.icesi.metrocali.atc.repositories.evaluator.MeasurementsRepository;
import co.edu.icesi.metrocali.atc.repositories.evaluator.VariableRepository;
import co.edu.icesi.metrocali.atc.services.evaluator.ExpressionsService;
import co.edu.icesi.metrocali.atc.vos.StateNotification;

@RunWith(SpringJUnit4ClassRunner.class)
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
    
    @Before
    public void calculateKPIStage(){
        ObjectsToTest objects=ObjectsToTest.getInstance();
        List<StateNotification> increase=objects.getNotificationsIncrease();
        for (StateNotification stateNotification : increase) {
            context.update(stateNotification);
        }
        context.loadControllers(ObjectsToTest.getInstance().getUsers());
        context.fillVariables();
        
    }
    @After
    public void reset(){
        context.loadSystemVariables();
    }

    @Test
    public void evaluateExpression(){
        double value=(double)expresion.evaluateExpression("sum({1,2,3,4})");
        assertTrue(value==10);
    }

    @Test
    public void addVariableTest(){

    }
    @Test
    public void lqTest(){
        double lq=(double)expresion.evaluateExpression("average(#eventsQHSs)");
        assertTrue(lq==4.5);
    
    }
    @Test
    public void wqTest(){
        //general
        double wq=(double)expresion.evaluateExpression("average(vectorSum(timesInPendingState(sortE(#lastEvents)),timesInAssignedState(sortE(#lastEvents))))");
        assertTrue(wq>=3.875,wq+">="+3.875);
        double time=0.1;
        assertTrue(wq<=3.875+time,wq+"<="+3.875+time);

        //por prioridad
        List<Category> categories=ObjectsToTest.getInstance().getSubCategories();
        HashMap<Integer,Double> map=new HashMap<>();
        map.put(1000, 5.0);
        map.put(990, 3.0);
        map.put(980, 5.5);
        map.put(960, 3.0);
        for (Category category : categories) {
            int priority=category.getBasePriority();
            double res=map.get(priority);
            String expressionE="map(groupByPriority(#lastEvents),'average(vectorSum(timesInPendingState(sortE(#value)), timesInAssignedState(sortE(#value))))').get("+priority+")";
            double wqP=(double)expresion.evaluateExpression(expressionE);
            assertTrue(wqP>=res,wqP+">="+res);
            assertTrue(wqP<=res+time,wqP+"<="+res+time);
        }
    }
    @Test
    public void lqMaxTest(){
        String expre="max(#eventsQHSsDay)";
        double result=(double)expresion.evaluateExpression(expre);
        assertTrue(result==8.0);
    }
    @Test
    public void wqMax(){
        context.addVar("wqMax", 4, "máximo tiempo promedio de espera");
        context.addVar("wq", 2, "tiempo promedio de espera");
        String expre="max({#wqMax , #wq})";
        double result=(double)expresion.evaluateExpression(expre);
        assertTrue(result==4.0,result+"="+4);
    }
    @Test
    public void wsTest(){
        //general
        double ws=(double)expresion.evaluateExpression("average(vectorSum(inProcessTime(sortE(#lastEvents)),inHold(sortE(#lastEvents))))");
        assertTrue(ws>=2.625,ws+">="+2.625);
        double time=0.1;
        assertTrue(ws<=2.625+time,ws+"<="+2.625+time);

        //por prioridad
        List<Category> categories=ObjectsToTest.getInstance().getSubCategories();
        HashMap<Integer,Double> map=new HashMap<>();
        map.put(1000, 2.0);
        map.put(990, 3.0);
        map.put(980, 1.0);
        map.put(960, 4.0);
        for (Category category : categories) {
            int priority=category.getBasePriority();
            double res=map.get(priority);
            String expressionE="map( groupByPriority( #lastEvents ), 'average( vectorSum( inProcessTime ( sortE( #value ) ), inHold( sortE ( #value ) ) ) )' ).get("+priority+")";
            double wsP=(double)expresion.evaluateExpression(expressionE);
            assertTrue(wsP>=res,wsP+">="+res);
            assertTrue(wsP<=res+time,wsP+"<="+res+time);
        }
    }
    @Test
    public void aboveThresholdTest(){
        List<Category> categories=ObjectsToTest.getInstance().getSubCategories();
        context.addVar(Context.THRESHOLDS, ObjectsToTest.getInstance().getThresholds(), "desc");
        DecimalFormat format=new DecimalFormat("0.00");
        List<Integer> priorities=new ArrayList<>();
        HashMap<Integer,String> answers=new HashMap<>();
        answers.put(1000, "100,00");
        answers.put(990, "66,67");
        answers.put(980, "50,00");
        answers.put(960, "50,00");
        for (Category category : categories) {
            int prio=category.getBasePriority();
            priorities.add(prio);
            String expre="percentageAboveThreshold(lenghtOfStay(groupByPriority(#lastEvents)["+prio+"] ), #thresholds["+prio+"])*100";
            double result=(double)expresion.evaluateExpression(expre);
            String resFormat=format.format(result);
            boolean eq=resFormat.equals(answers.get(prio));
            assertTrue(eq,resFormat+"=="+answers.get(prio));            
        }
        context.addVar(Context.PRIORITIES, priorities, "desc");
        String expression="average(map(#priorities,'percentageAboveThreshold(lenghtOfStay(groupByPriority(#lastEvents)[#value]),#thresholds[#value])*100'))";
        double result=(double)expresion.evaluateExpression(expression);
        boolean eq=format.format(result).equals("66,67");
        assertTrue(eq,""+result);
        
    }
    @Test
    public void uTest(){
        String expre="average(toList(#eventsDone))";
        double result=(double)expresion.evaluateExpression(expre);
        assertTrue(result==0.5,result+"");
    }
    @Test
    public void ucTest(){
        List<Controller> controllers=ObjectsToTest.getInstance().getUsers();
        context.loadControllers(controllers);
        HashMap<Integer,Double> answers=new HashMap<>();
        answers.put(1, 0.0);
        answers.put(2, 1.0);
        answers.put(3, 1.0);
        answers.put(4, 0.0);
        for (Controller controller : controllers) {
            int id=controller.getId();
            String expre="map(#controllers, 'average(#eventsController.get(#value.getId()))')['"+id+"']";
            double result=(double)expresion.evaluateExpression(expre);
            assertTrue(result==answers.get(id),result+"=="+answers.get(id));
        }
    }
    @Test
    public void varUcTest(){
        String expre="stddev(toList(map(#controllers,'average(#eventsController.get(#value.getId()))')))";
        double result=(double)expresion.evaluateExpression(expre);
        assertTrue(result==0.5,result+"");
    }
    @Test
    public void pTest(){
        String expre="average(toList(map(#controllers,'busyTime(#value)/controllerStay(#value)')))*100";
        double result=(double)expresion.evaluateExpression(expre);
        DecimalFormat format=new DecimalFormat("0.00");
        boolean bool=format.format(result).equals("35,71");
        assertTrue(bool,result+"");
    }
    @Test
    public void pcTest(){
        DecimalFormat format=new DecimalFormat("0.000");
        List<Controller> controllers=ObjectsToTest.getInstance().getUsers();
        HashMap<Integer,Double> answers=new HashMap<>();
        answers.put(1, 5.0);
        answers.put(2, 0.427);
        answers.put(3, 0.499);
        answers.put(4, 0.000);
        for (Controller controller : controllers) {
            String expre="map(#controllers,'busyTime(#value)/controllerStay(#value)')['"+controller.getId()+"']";
            double result=(double)expresion.evaluateExpression(expre);
            double form=Double.parseDouble(format.format(result).replace(',', '.'));
            boolean bool=form<=answers.get(controller.getId());
            bool|=form>=answers.get(controller.getId())-0.0001;
            assertTrue(bool,form+"=="+answers.get(controller.getId()));            
        }
    }
    
    
}