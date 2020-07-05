package co.edu.icesi.metrocali.atc.evaluator;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import co.edu.icesi.metrocali.atc.entities.evaluator.EvalParameter;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.evaluator.expression.Context;
import co.edu.icesi.metrocali.atc.evaluator.expression.Functions;
import co.edu.icesi.metrocali.atc.evaluator.expression.SpringExpressions;
import co.edu.icesi.metrocali.atc.repositories.CategoriesRepository;
import co.edu.icesi.metrocali.atc.repositories.OperatorsRepository;
import co.edu.icesi.metrocali.atc.repositories.evaluator.EvalParametersRepository;
import co.edu.icesi.metrocali.atc.vos.StateNotification;


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
    @Mock
    private CategoriesRepository categories;
    @Mock
    private OperatorsRepository operators;

    private void queueIncreaseStage(){
        ObjectsToTest objectsToTest=ObjectsToTest.getInstance();
        List<StateNotification> increNotifications=objectsToTest.getNotificationsIncrease();
        for (StateNotification stateNotification : increNotifications) {
            context.update(stateNotification);
        }
    }
    private void queueDecreaseStage(){
        ObjectsToTest objectsToTest=ObjectsToTest.getInstance();
        List<StateNotification> decreNotifications=objectsToTest.getNotificationsDecrease();
        for (StateNotification stateNotification : decreNotifications) {
            context.update(stateNotification);
        }
    }
    private void eventsDoneStage() {
        ObjectsToTest objectsToTest=ObjectsToTest.getInstance();
        context.loadSystemVariables();
        context.setValueForVar(Context.LAST_EVENTS, objectsToTest.getEvents());
    }
    private void fillVarStage(){
        ObjectsToTest objectsToTest=ObjectsToTest.getInstance();
        List<Controller> controllers=objectsToTest.getUsers();
        HashMap<Integer,EvalParameter> parametersTest=objectsToTest.getParameters();
        when(operators.retrieveOnlineControllers()).thenReturn(controllers);
        when(categories.retrieveAll()).thenReturn(objectsToTest.getSubCategories());
        when(parameters.retrieveActiveByName("threshold" + 1000)).thenReturn(Optional.of(parametersTest.get(1000)));
        when(parameters.retrieveActiveByName("threshold" + 990)).thenReturn(Optional.of(parametersTest.get(990)));
        when(parameters.retrieveActiveByName("threshold" + 980)).thenReturn(Optional.of(parametersTest.get(980)));
        when(parameters.retrieveActiveByName("threshold" + 960)).thenReturn(Optional.of(parametersTest.get(960)));
        context.loadSystemVariables();
        queueIncreaseStage();
    }
    @Test
    public void queueSizeTest(){
        context.loadSystemVariables();
        queueIncreaseStage();
        List<Event> lastEve=(List<Event>)context.getVar(Context.LAST_EVENTS);
        List<Integer> sizes=(List<Integer>)context.getVar(Context.EVENTSQHSS);
        List<Integer> sizesDay=(List<Integer>)context.getVar(Context.EVENTSQHSS_Day);
        int size=ObjectsToTest.getInstance().getEvents().size();
        List<Integer> right=new ArrayList<>();
        right.add(1);
        assertTrue(lastEve.size()==size);
        queueDecreaseStage();
        for (int i = 1; i < size; i++) {
            right.add(right.get(i-1)+1);
        }
        for (int i = size; i < 2*size; i++) {
            right.add(right.get(i-1)-1);
        }
        for (int i = 0; i < sizes.size(); i++) {
            assertTrue(right.get(i)==sizes.get(i));
            assertTrue(right.get(i)==sizesDay.get(i));
        }
        assertTrue(lastEve.size()==size);
    }
    @Test
    public void updateEventsDoneTest(){
        eventsDoneStage();
        context.updateLastEvent();
        List<Event> events= (List<Event>) context.getVar(Context.LAST_EVENTS);
        for (Event event : events) {
            long id=event.getId();
            if(id==2||id==7){
                fail();
            }
        }    
    }
    @Test
    public void fillVarTest(){
        ObjectsToTest objectsToTest=ObjectsToTest.getInstance();
        fillVarStage();
        context.getRootObject();
        ArrayList<Integer> eventsQss= (ArrayList<Integer>) context.getVar(Context.EVENTSQHSS);
        ArrayList<Integer> eventsQssDay= (ArrayList<Integer>) context.getVar(Context.EVENTSQHSS_Day);
        ArrayList<Event> lastEvents= (ArrayList<Event>) context.getVar(Context.LAST_EVENTS);
        HashMap<Integer, Integer> eventsDone= (HashMap<Integer, Integer>) context.getVar(Context.EVENTS_DONE);
        HashMap<Integer, List<Integer>> eventsController= (HashMap<Integer, List<Integer>>) context.getVar(Context.EVENTS_CONTROLLER);
        HashMap<String, Controller> controllers= (HashMap<String, Controller>) context.getVar(Context.CONTROLLERS);
        HashSet<Integer> priorities = (HashSet<Integer>) context.getVar(context.PRIORITIES);
        HashMap<Integer, Double> threshold = (HashMap<Integer, Double>) context.getVar(context.THRESHOLDS);

        int size=objectsToTest.getEvents().size();
        assertTrue(size==lastEvents.size());
        assertTrue(controllers.size()==objectsToTest.getUsers().size());
        int tmp=1;
        for (int i = 0; i < lastEvents.size(); i++) {
            assertTrue(tmp==eventsQss.get(i));
            assertTrue(tmp==eventsQssDay.get(i));
            tmp++;
        }
        Iterator<Integer> keys=eventsDone.keySet().iterator();
        assertTrue(eventsDone.size()==controllers.size());
        while(keys.hasNext()){
            int key=keys.next();
            if(key==2||key==3){
                assertTrue(eventsDone.get(key)==1);
            }else{
                assertTrue(eventsDone.get(key)==0);
            }
        }
        keys=eventsController.keySet().iterator();
        assertTrue(eventsController.size()==controllers.size());
        while(keys.hasNext()){
            int key=keys.next();
            List<Integer> value=eventsController.get(key);
            assertTrue(value.size()==1);
            if(key==2||key==3){
                assertTrue(value.get(0)==1);
            }else{
                assertTrue(value.get(0)==0);
            }
        }
        List<Category> categories=objectsToTest.getSubCategories();
        assertTrue(categories.size()==threshold.size());
        assertTrue(categories.size()==priorities.size());
        for (Category category : categories) {
            int priority=category.getBasePriority();
            assertTrue(threshold.containsKey(priority));
            assertTrue(threshold.get(priority)==priority);
            assertTrue(priorities.contains(priority));
        }
    }

   

}