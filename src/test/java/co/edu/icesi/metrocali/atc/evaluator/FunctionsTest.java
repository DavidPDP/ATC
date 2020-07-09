package co.edu.icesi.metrocali.atc.evaluator;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.evaluator.expression.Functions;


@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class FunctionsTest {
    @InjectMocks
    private Functions functions;

    @Test
    public void averageAndSumTest(){
        List<Double> numbers=new ArrayList<>();
        numbers.add(2.0);
        numbers.add(3.0);
        numbers.add(4.0);
        numbers.add(5.0);
        numbers.add(6.0);
        double average=functions.average(numbers);
        double sum=functions.sum(numbers);
        assertTrue( 4==average,"Average test "+average);
        assertTrue( 20==sum,"Sum test "+sum);
    }

    @Test
    public void timesInPendingStateTest(){
        ObjectsToTest objects=ObjectsToTest.getInstance();
        List<Event> events=objects.getEvents();
        List<Double> inPending=functions.timesInPendingState(events);
        double[] answers={3.0,2.0,1.0,1.0,2.0,2.0,2.0,2.0};
        for (int i = 0; i < answers.length-1; i++) {
            assertTrue(inPending.get(i)==answers[i]);    
        }
        assertTrue(inPending.get(7)>=2);    
    }
    @Test
    public void timesInAssignedStateTest(){
        ObjectsToTest objects=ObjectsToTest.getInstance();
        List<Event> events=objects.getEvents();
        List<Double> inAssigned=functions.timesInAssignedState(events);
        double[] answers={2.0,2.0,3.0,2.0,1.0,5.0,1.0,0};
        for (int i = 0; i < answers.length-1; i++) {
            if(i!=5){
                assertTrue(inAssigned.get(i)==answers[i]);    
            }
        }
        assertTrue(inAssigned.get(5)>=5); 
    }

    @Test
    public void vectorSumAndMaxTest(){
        List<Double> list1=Arrays.asList(2.0,1.0,3.0,2.5);
        List<Double> list2=Arrays.asList(3.0,4.0,2.0,2.5);
        List<Double> result=functions.vectorSum(list1, list2);
        for (Double double1 : result) {
            assertTrue( double1==5,"Sum value "+double1);            
        }
        double max=functions.max((Double[]) list1.toArray());
        assertTrue(max==3,"max to list "+max);
    }

    @Test
    public void inProcessTimeTest(){
        ObjectsToTest objects=ObjectsToTest.getInstance();
        List<Event> events=objects.getEvents();
        List<Double> inProcess=functions.inProcessTime(events);
        assertTrue(inProcess.get(0)>=2);
        assertTrue(inProcess.get(1)==4);
        assertTrue(inProcess.get(2)>=2);
        assertTrue(inProcess.get(3)>=1);
        assertTrue(inProcess.get(4)>=5);
        assertTrue(inProcess.get(5)==0);
        assertTrue(inProcess.get(6)==5);
        assertTrue(inProcess.get(7)==0);

    }
    @Test
    public void inHoldTest(){
        ObjectsToTest objects=ObjectsToTest.getInstance();
        List<Event> events=objects.getEvents();
        List<Double> inHold=functions.inHold(events);
        for (int i = 0; i < inHold.size(); i++) {
            if(i!=4){
                assertTrue(inHold.get(i)==0);
            }
        }
        assertTrue(inHold.get(4)==2);
    }
    @Test
    public void groupByPriorityTest(){
        List<Event> events=ObjectsToTest.getInstance().getEvents();
        HashMap<Integer, List<Event>> answer=new HashMap<>();
        List<Event> tmp=new ArrayList<>();
        tmp.add(events.get(0));
        answer.put(1000, tmp);//0

        tmp=new ArrayList<>();
        tmp.add(events.get(1));
        tmp.add(events.get(6));
        tmp.add(events.get(7));
        answer.put(990, tmp);//1

        tmp=new ArrayList<>();
        tmp.add(events.get(5));
        tmp.add(events.get(2));
        answer.put(980, tmp);//2

        tmp=new ArrayList<>();
        tmp.add(events.get(3));
        tmp.add(events.get(4));
        answer.put(960, tmp);//3

        HashMap<Integer, List<Event>> response=functions.groupByPriority(events);
        Iterator<Integer> keys=response.keySet().iterator();
        while(keys.hasNext()){
            int key=keys.next();
            List<Event> eventsRight=answer.get(key);
            List<Event> eventsTest=response.get(key);
            assertNotNull("right "+key,eventsRight);
            assertNotNull("test "+key,eventsTest);
            for (Event event : eventsTest) {
                boolean exist=false;
                for (Event event2 : eventsRight) {
                    exist|=event.getId()==event2.getId();
                    if(exist){
                        break;
                    }
                }
                assertTrue(exist);
            }
        }
    }

    @Test
    public void percentageAboveThresholdTest(){
        double result=functions.percentageAboveThreshold(Arrays.asList(2.2,3.1,6.0,4.1,1.5,7.2,6.5,3.4), 4);
        assertTrue(result==0.5,"percentage "+result);
    }

    @Test
    public void busyTimeTest(){
        List<Controller> users=ObjectsToTest.getInstance().getUsers();
        List<Double> busyTimes=new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            double busyTime=functions.busyTime(users.get(i));
            busyTimes.add(busyTime);
        }
        assertTrue(busyTimes.get(0)==5);
        assertTrue(busyTimes.get(1)>=3);
        assertTrue(busyTimes.get(2)==7);
        assertTrue(busyTimes.get(3)==0);

    }

    @Test
    public void mapTest(){
        List<Double> valuesTest=Arrays.asList(2.0,3.0,13.0,1.0);
        List<Double> resultsList= (List<Double>) functions.map(valuesTest, "#value*2");
        for (int i = 0; i < resultsList.size(); i++) {
            assertTrue(valuesTest.get(i)*2==resultsList.get(i),"List Test");
        }
        HashMap<Integer,Integer> mapTest=new HashMap<>();
        mapTest.put(1, 2);
        mapTest.put(2, 3);
        mapTest.put(3, 4);
        mapTest.put(4, 2);
        mapTest.put(5, 2);
        HashMap<Integer,Integer> resultMap= (HashMap<Integer, Integer>) functions.map(mapTest, "#value*2");
        Iterator<Integer> keys=resultMap.keySet().iterator();
        while(keys.hasNext()){
            int key=keys.next();
            assertTrue( resultMap.get(key)==mapTest.get(key)*2,"HashMap test");
        }
    }

    @Test
    public void toListTest(){
        HashMap<Integer,Integer> mapTest=new HashMap<>();
        mapTest.put(1, 2);
        mapTest.put(2, 2);
        mapTest.put(3, 2);
        mapTest.put(4, 2);
        mapTest.put(5, 2);
        List<Integer> result= (List<Integer>) functions.toList(mapTest);
        for (Integer integer : result) {
            assertTrue(integer==2);
        }
    }

    @Test
    public void sortETest(){
        List<Event> events=ObjectsToTest.getInstance().getEvents();
        Event tmp=events.get(0);
        events.set(0, events.get(5));
        events.set(5, events.get(1));
        events.set(1, events.get(4));
        events.set(4, tmp);
        List<Event> orden=functions.sortE(events);
        for (int i = 1; i < orden.size(); i++) {
            assertTrue(orden.get(i-1).getId()<=orden.get(i).getId());
        }
    }

    @Test
    public void controllerStayTest(){
        List<Controller> users=ObjectsToTest.getInstance().getUsers();
        List<Double> stayTimes=new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            double stayTime=functions.controllerStay(users.get(i));
            stayTimes.add(stayTime);
        }
        assertTrue(stayTimes.get(0)>=10);
        assertTrue(stayTimes.get(1)>=7);
        assertTrue(stayTimes.get(2)>=14);
        assertTrue(stayTimes.get(3)>=4);

        
    }
}