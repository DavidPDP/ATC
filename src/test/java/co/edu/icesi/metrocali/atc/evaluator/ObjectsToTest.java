package co.edu.icesi.metrocali.atc.evaluator;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import co.edu.icesi.metrocali.atc.constants.NotificationType;
import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalParameter;
import co.edu.icesi.metrocali.atc.entities.evaluator.Variable;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.events.UserTrack;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.vos.StateNotification;
import lombok.Getter;

@Getter
public class ObjectsToTest {
    private static ObjectsToTest instance;
    private List<Event> events;
    private List<Controller> users;
    private List<Category> subCategories;
    private HashMap<StateValue,State> states;
    private List<StateNotification> notificationsIncrease;
    private List<StateNotification> notificationsDecrease;
    private HashMap<Integer,EvalParameter> parameters;
    private List<Variable> variables;
    private HashMap<Integer,Double> thresholds;

    private ObjectsToTest(){
        createSubCategories();
        createStates();
        createUsers();
        createEvents();
        createNotifications();
        createParameters();
        createVariables();
    }

    private void createVariables() {
        variables=new ArrayList<>();
        variables.add(createVariable("var1", "#thresholds[1000]"));//1000
        variables.add(createVariable("var2", "sum(#priorities)"));//3930
        variables.add(createVariable("var3", "len(#priorities)"));//4
        variables.add(createVariable("var4", "sum(timesInPendingState(#lastEvents))"));//
        variables.add(createVariable("var5", "sum(map({1,2,3},'#value*2'))"));//>15
    }

    private Variable createVariable(String name,String expression){
        Variable variable=new Variable();
        variable.setNameVariable(name);
        variable.setLastFormulaExpression(expression);
        variable.setIsKPI(true);
        return variable;
    }

    private void createParameters() {
        parameters=new HashMap<>();
        for (Category category: subCategories) {
            int key=category.getBasePriority();
            parameters.put(key, createParameter("threshold"+key, key));
        }
    }

    private EvalParameter createParameter(String name,double value){
        EvalParameter parameter=new EvalParameter();
        parameter.setName(name);
        parameter.setValue(value);
        return parameter;
    }

    private void createNotifications() {
        notificationsIncrease=new ArrayList<>();
        notificationsDecrease=new ArrayList<>();
        for (Event event : events) {
            notificationsIncrease.add(createNotification(event,NotificationType.New_Event_Entity));
            notificationsDecrease.add(createNotification(event,NotificationType.New_Event_Assignment));
        }
    }
    private StateNotification createNotification(Event event,NotificationType type){
        Object[] elementsInvolved = {event};
		StateNotification stateEvent = 
			new StateNotification(
				type, 
				Optional.ofNullable(null),
				elementsInvolved
            );
        return stateEvent;
    }

    private UserTrack createUserState(User user, State state, Timestamp start, Timestamp end) {
        UserTrack userState=new UserTrack();
        userState.setEndTime(end);
        userState.setStartTime(start);
        userState.setState(state);
        userState.setUser(user);
        return userState;
    }

    private EventTrack createEventState(long id,State state,Timestamp start,Timestamp end,Controller user){
        EventTrack eventState=new EventTrack();
        eventState.setEndTime(end);
        eventState.setStartTime(start);
        eventState.setState(state);
        eventState.setId(id);
        eventState.setUser(user);
        return eventState;
    }

    private void createEvents() {
        events=new ArrayList<>();
        Event event1=createEvent(1, 450, new Timestamp(System.currentTimeMillis()-1000*60*7), subCategories.get(0), users.get(0));//0
        Event event2=createEvent(2, 900, new Timestamp(System.currentTimeMillis()-1000*60*10), subCategories.get(1), users.get(1));//1
        Event event3=createEvent(3, 1000, new Timestamp(System.currentTimeMillis()-1000*60*6), subCategories.get(2), users.get(2));//2
        Event event4=createEvent(4, 450, new Timestamp(System.currentTimeMillis()-1000*60*4), subCategories.get(3), users.get(3));//3
        Event event5=createEvent(5, 450, new Timestamp(System.currentTimeMillis()-1000*60*10), subCategories.get(3), users.get(3));//4
        Event event6=createEvent(6, 400, new Timestamp(System.currentTimeMillis()-1000*60*7), subCategories.get(2), users.get(3));//5
        Event event7=createEvent(7, 450, new Timestamp(System.currentTimeMillis()-1000*60*10), subCategories.get(1), users.get(2));//6
        Event event8=createEvent(8, 450, new Timestamp(System.currentTimeMillis()-1000*60*2), subCategories.get(1), users.get(2));//7
       
        event1.addEventTrack(createEventState(1, states.get(StateValue.Pending), new Timestamp(System.currentTimeMillis()-1000*60*7), new Timestamp(System.currentTimeMillis()-1000*60*4),users.get(0)));//3mn
        event1.addEventTrack(createEventState(2, states.get(StateValue.Assigned), new Timestamp(System.currentTimeMillis()-1000*60*4), new Timestamp(System.currentTimeMillis()-1000*60*2),users.get(0)));//2mn
        event1.addEventTrack(createEventState(3, states.get(StateValue.In_Proccess), new Timestamp(System.currentTimeMillis()-1000*60*2),null,users.get(0)));//>2mn

        event2.addEventTrack(createEventState(4, states.get(StateValue.Pending), new Timestamp(System.currentTimeMillis()-1000*60*10), new Timestamp(System.currentTimeMillis()-1000*60*8),users.get(1)));//2mn
        event2.addEventTrack(createEventState(5, states.get(StateValue.Assigned), new Timestamp(System.currentTimeMillis()-1000*60*8), new Timestamp(System.currentTimeMillis()-1000*60*6),users.get(1)));//2mn
        event2.addEventTrack(createEventState(6, states.get(StateValue.In_Proccess), new Timestamp(System.currentTimeMillis()-1000*60*6), new Timestamp(System.currentTimeMillis()-1000*60*2),users.get(1)));//4mn
        event2.addEventTrack(createEventState(7, states.get(StateValue.Verification), new Timestamp(System.currentTimeMillis()-1000*60*2),null,users.get(1)));//>2mn

        event3.addEventTrack(createEventState(8, states.get(StateValue.Pending), new Timestamp(System.currentTimeMillis()-1000*60*6), new Timestamp(System.currentTimeMillis()-1000*60*5),users.get(2)));//1mn
        event3.addEventTrack(createEventState(9, states.get(StateValue.Assigned), new Timestamp(System.currentTimeMillis()-1000*60*5), new Timestamp(System.currentTimeMillis()-1000*60*2),users.get(2)));//3mn
        event3.addEventTrack(createEventState(10, states.get(StateValue.In_Proccess), new Timestamp(System.currentTimeMillis()-1000*60*2), null,users.get(2)));//>2mn

        event4.addEventTrack(createEventState(11, states.get(StateValue.Pending), new Timestamp(System.currentTimeMillis()-1000*60*4), new Timestamp(System.currentTimeMillis()-1000*60*3),users.get(3)));//1mn
        event4.addEventTrack(createEventState(12, states.get(StateValue.Assigned), new Timestamp(System.currentTimeMillis()-1000*60*3), new Timestamp(System.currentTimeMillis()-1000*60*1),users.get(3)));//2mn
        event4.addEventTrack(createEventState(13, states.get(StateValue.In_Proccess), new Timestamp(System.currentTimeMillis()-1000*60*1), null,users.get(3)));//1mn

        event5.addEventTrack(createEventState(14, states.get(StateValue.Pending), new Timestamp(System.currentTimeMillis()-1000*60*10), new Timestamp(System.currentTimeMillis()-1000*60*8),users.get(0)));//2mn
        event5.addEventTrack(createEventState(15, states.get(StateValue.Assigned), new Timestamp(System.currentTimeMillis()-1000*60*8), new Timestamp(System.currentTimeMillis()-1000*60*7),users.get(0)));//1mn
        event5.addEventTrack(createEventState(16, states.get(StateValue.In_Proccess), new Timestamp(System.currentTimeMillis()-1000*60*7), new Timestamp(System.currentTimeMillis()-1000*60*3),users.get(0)));//4mn
        event5.addEventTrack(createEventState(17, states.get(StateValue.On_Hold), new Timestamp(System.currentTimeMillis()-1000*60*3), new Timestamp(System.currentTimeMillis()-1000*60*1),users.get(0)));//2mn
        event5.addEventTrack(createEventState(18, states.get(StateValue.In_Proccess), new Timestamp(System.currentTimeMillis()-1000*60*1), null,users.get(0)));//>1

        event6.addEventTrack(createEventState(19, states.get(StateValue.Pending), new Timestamp(System.currentTimeMillis()-1000*60*7), new Timestamp(System.currentTimeMillis()-1000*60*5),users.get(1)));//2mn
        event6.addEventTrack(createEventState(20, states.get(StateValue.Assigned), new Timestamp(System.currentTimeMillis()-1000*60*5),null,users.get(1)));//>5mn

        event7.addEventTrack(createEventState(21, states.get(StateValue.Pending), new Timestamp(System.currentTimeMillis()-1000*60*10), new Timestamp(System.currentTimeMillis()-1000*60*8),users.get(2)));//2mn
        event7.addEventTrack(createEventState(22, states.get(StateValue.Assigned), new Timestamp(System.currentTimeMillis()-1000*60*8), new Timestamp(System.currentTimeMillis()-1000*60*7),users.get(2)));//1mn
        event7.addEventTrack(createEventState(23, states.get(StateValue.In_Proccess), new Timestamp(System.currentTimeMillis()-1000*60*7), new Timestamp(System.currentTimeMillis()-1000*60*2),users.get(2)));//5mn
        event7.addEventTrack(createEventState(24, states.get(StateValue.Verification), new Timestamp(System.currentTimeMillis()-1000*60*2), null,users.get(2)));//>2

        event8.addEventTrack(createEventState(21, states.get(StateValue.Pending), new Timestamp(System.currentTimeMillis()-1000*60*2), null,users.get(3)));//>2

        events.add(event1);
        events.add(event2);
        events.add(event3);
        events.add(event4);
        events.add(event5);
        events.add(event6);
        events.add(event7);
        events.add(event8);

    }   
    private Event createEvent(long id,int priority,Timestamp creation,Category sub,User user){
        Event event=new Event();
        event.setCreation(creation);
        event.setId(id);
        event.setCategory(sub);
        return event;
    }

    private void createUsers() {
        users=new ArrayList<>();
        Controller controller1=createUser(1,"con1",states.get(StateValue.Available));
        Controller controller2=createUser(2,"con2",states.get(StateValue.Available));
        Controller controller3=createUser(3,"con3",states.get(StateValue.Available));
        Controller controller4=createUser(4,"con4",states.get(StateValue.Available));

        controller1.addUserTrack(createUserState(controller1, states.get(StateValue.Available), new Timestamp(System.currentTimeMillis()-1000*60*10), new Timestamp(System.currentTimeMillis()-1000*60*7)));//3mn
        controller1.addUserTrack(createUserState(controller1, states.get(StateValue.Busy), new Timestamp(System.currentTimeMillis()-1000*60*7), new Timestamp(System.currentTimeMillis()-1000*60*2)));//5mn
        controller1.addUserTrack(createUserState(controller1, states.get(StateValue.Unavailable), new Timestamp(System.currentTimeMillis()-1000*60*2), null));//>2mn

        controller2.addUserTrack(createUserState(controller2, states.get(StateValue.Available), new Timestamp(System.currentTimeMillis()-1000*60*7), new Timestamp(System.currentTimeMillis()-1000*60*3)));//4mn
        controller2.addUserTrack(createUserState(controller2, states.get(StateValue.Busy), new Timestamp(System.currentTimeMillis()-1000*60*3), null));//>3mn

        controller3.addUserTrack(createUserState(controller3, states.get(StateValue.Available), new Timestamp(System.currentTimeMillis()-1000*60*14), new Timestamp(System.currentTimeMillis()-1000*60*10)));//4mn
        controller3.addUserTrack(createUserState(controller3, states.get(StateValue.Busy), new Timestamp(System.currentTimeMillis()-1000*60*10), new Timestamp(System.currentTimeMillis()-1000*60*3)));//7mn
        controller3.addUserTrack(createUserState(controller3, states.get(StateValue.Unavailable), new Timestamp(System.currentTimeMillis()-1000*60*3), null));//>3mn

        controller4.addUserTrack(createUserState(controller4, states.get(StateValue.Available), new Timestamp(System.currentTimeMillis()-1000*60*4), null));//>4mn

        users.add(controller1);
        users.add(controller2);
        users.add(controller3);
        users.add(controller4);

        
    }
    private Controller createUser(int id,String name,State state){
        Controller user=new Controller();
        user.setId(id);
        user.setName(name);
        return user;
    }

    private void createStates() {
        states=new HashMap<>();

        states.put(StateValue.Available,createState(StateValue.Available.name()));//0
        states.put(StateValue.Busy,createState(StateValue.Busy.name()));//1
        states.put(StateValue.Unavailable,createState(StateValue.Unavailable.name()));//2
        states.put(StateValue.Pending,createState(StateValue.Pending.name()));//3
        states.put(StateValue.Assigned,createState(StateValue.Assigned.name()));//4
        states.put(StateValue.In_Proccess,createState(StateValue.In_Proccess.name()));//5
        states.put(StateValue.On_Hold,createState(StateValue.On_Hold.name()));//6
        states.put(StateValue.Verification,createState(StateValue.Verification.name()));//7

    }

    private State createState(String name) {
        State state=new State();
        state.setName(name);
        return state;
    }

    public static ObjectsToTest getInstance() {
        if(instance==null){
            instance=new ObjectsToTest();
        }
        return instance;
    }
    private void createSubCategories(){
        subCategories=new ArrayList<>();
        subCategories.add(createSubCategory(1,"Botón de pánico", 1000));
        subCategories.add(createSubCategory(2,"Situación urgente ", 990));
        subCategories.add(createSubCategory(3,"Vandalismo", 980));
        subCategories.add(createSubCategory(4,"Sospechoso a bordo", 960));

        thresholds=new HashMap<>();
        thresholds.put(1000,5.0);
        thresholds.put(990,7.0);
        thresholds.put(980,6.5);
        thresholds.put(960,5.0);
    }

    private Category createSubCategory(int id,String name,int priority){
        Category cat=new Category();
        cat.setId(id);
        cat.setName(name);
        cat.setBasePriority(priority);
        return cat;
    }
  
}