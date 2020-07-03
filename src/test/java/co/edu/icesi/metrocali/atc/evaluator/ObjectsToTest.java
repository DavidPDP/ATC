package co.edu.icesi.metrocali.atc.evaluator;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.events.UserTrack;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import lombok.Data;

@Data
public class ObjectsToTest {
    private static ObjectsToTest instance;
    private List<Event> events;
    private List<Controller> users;
    private List<Category> subCategories;
    private List<State> states;
    private List<EventTrack> eventStates;
    private List<UserTrack> userStates;

    private ObjectsToTest(){
        createSubCategories();
        createStates();
        createUsers();
        createEvents();
        createEventStates();
        createUserStates();
    }

    private void createUserStates() {
        userStates=new ArrayList<>();
        userStates.add(createUserState(users.get(0),states.get(0),new Timestamp(System.currentTimeMillis()+1000*60*-15),new Timestamp(System.currentTimeMillis()+1000*60*-10)));//5mn disponible
        userStates.add(createUserState(users.get(0),states.get(1),new Timestamp(System.currentTimeMillis()+1000*60*-10),new Timestamp(System.currentTimeMillis()+1000*60*-3)));//7mn ocupado
        userStates.add(createUserState(users.get(0),states.get(2),new Timestamp(System.currentTimeMillis()+1000*60*-3),new Timestamp(System.currentTimeMillis())));//3mn no disponible

        userStates.add(createUserState(users.get(1),states.get(0),new Timestamp(System.currentTimeMillis()+1000*60*-12),new Timestamp(System.currentTimeMillis()+1000*60*-8)));//4mn disponible
        userStates.add(createUserState(users.get(1),states.get(1),new Timestamp(System.currentTimeMillis()+1000*60*-8),new Timestamp(System.currentTimeMillis()+1000*60*-1)));//7mn ocupado
        userStates.add(createUserState(users.get(1),states.get(2),new Timestamp(System.currentTimeMillis()+1000*60*-1),new Timestamp(System.currentTimeMillis()+1000*60)));//2mn no disponible

        userStates.add(createUserState(users.get(2),states.get(0),new Timestamp(System.currentTimeMillis()+1000*60*-20),new Timestamp(System.currentTimeMillis()+1000*60*-8)));//12mn disponible
        userStates.add(createUserState(users.get(2),states.get(1),new Timestamp(System.currentTimeMillis()+1000*60*-8),new Timestamp(System.currentTimeMillis()+1000*60*-5)));//3mn ocupado
        userStates.add(createUserState(users.get(2),states.get(2),new Timestamp(System.currentTimeMillis()+1000*60*-5),new Timestamp(System.currentTimeMillis()+1000*60*-1)));//4mn no disponible
        userStates.add(createUserState(users.get(2),states.get(8),new Timestamp(System.currentTimeMillis()+1000*60*-1),new Timestamp(System.currentTimeMillis()+1000*60)));//2mn disponible

        userStates.add(createUserState(users.get(3),states.get(0),new Timestamp(System.currentTimeMillis()+1000*60*-10),new Timestamp(System.currentTimeMillis()+1000*60*-5)));//5mn disponible
        userStates.add(createUserState(users.get(3),states.get(1),new Timestamp(System.currentTimeMillis()+1000*60*-5),new Timestamp(System.currentTimeMillis()+1000*60*-1)));//4mn ocupado
        userStates.add(createUserState(users.get(3),states.get(2),new Timestamp(System.currentTimeMillis()+1000*60*-1),new Timestamp(System.currentTimeMillis()+1000*60)));//2mn no disponible
        userStates.add(createUserState(users.get(3),states.get(1),new Timestamp(System.currentTimeMillis()+1000*60),new Timestamp(System.currentTimeMillis()+1000*60*4)));//3mn ocupado

    }
    private UserTrack createUserState(User user,State state,Timestamp start,Timestamp end){
        UserTrack userState=new UserTrack();
        userState.setEndTime(end);
        userState.setStartTime(start);
        userState.setState(state);
        userState.setUser(user);
        return userState;
    }
    private void createEventStates() {
        eventStates=new ArrayList<>();// Estados para eventos >=3
        eventStates.add(createEventState(1,events.get(0), states.get(3), new Timestamp(System.currentTimeMillis()+1000*60*-10), new Timestamp(System.currentTimeMillis()+1000*60*-8)));//2mn pendiente 0
        eventStates.add(createEventState(2,events.get(0), states.get(4), new Timestamp(System.currentTimeMillis()+1000*60*-8), new Timestamp(System.currentTimeMillis()+1000*60*-5)));//3mn Asignado
        eventStates.add(createEventState(3,events.get(0), states.get(5), new Timestamp(System.currentTimeMillis()+1000*60*-5), new Timestamp(System.currentTimeMillis())));//5mn en proceso

        eventStates.add(createEventState(4,events.get(1), states.get(3), new Timestamp(System.currentTimeMillis()+1000*60*-10), new Timestamp(System.currentTimeMillis()+1000*60*-8)));//2mn pendiente 3
        eventStates.add(createEventState(5,events.get(1), states.get(4), new Timestamp(System.currentTimeMillis()+1000*60*-8), new Timestamp(System.currentTimeMillis()+1000*60*-5)));//3mn Asignado
        eventStates.add(createEventState(6,events.get(1), states.get(5), new Timestamp(System.currentTimeMillis()+1000*60*-5), new Timestamp(System.currentTimeMillis())));//5mn en proceso
        eventStates.add(createEventState(7,events.get(1), states.get(6), new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()+1000*60*2)));//2mn en espera
        eventStates.add(createEventState(8,events.get(1), states.get(5), new Timestamp(System.currentTimeMillis()+1000*60*2), new Timestamp(System.currentTimeMillis()+1000*60*5)));//3mn en proceso

        eventStates.add(createEventState(9,events.get(2), states.get(3), new Timestamp(System.currentTimeMillis()+1000*60*-12), new Timestamp(System.currentTimeMillis()+1000*60*-9)));//3mn pendiente 8
        eventStates.add(createEventState(10,events.get(2), states.get(4), new Timestamp(System.currentTimeMillis()+1000*60*-9), new Timestamp(System.currentTimeMillis()+1000*60*-5)));//4mn Asignado
        eventStates.add(createEventState(11,events.get(2), states.get(5), new Timestamp(System.currentTimeMillis()+1000*60*-5), new Timestamp(System.currentTimeMillis())));//5mn en proceso

        eventStates.add(createEventState(12,events.get(3), states.get(3), new Timestamp(System.currentTimeMillis()+1000*60*-10), new Timestamp(System.currentTimeMillis()+1000*60*-8)));//2mn pendiente 11
        eventStates.add(createEventState(13,events.get(3), states.get(4), new Timestamp(System.currentTimeMillis()+1000*60*-8), new Timestamp(System.currentTimeMillis()+1000*60*-5)));//3mn Asignado
        eventStates.add(createEventState(14,events.get(3), states.get(5), new Timestamp(System.currentTimeMillis()+1000*60*-5), new Timestamp(System.currentTimeMillis())));//5mn en proceso

        eventStates.add(createEventState(15,events.get(4), states.get(3), new Timestamp(System.currentTimeMillis()+1000*60*-10), new Timestamp(System.currentTimeMillis()+1000*60*-8)));//2mn pendiente 14
        eventStates.add(createEventState(16,events.get(4), states.get(4), new Timestamp(System.currentTimeMillis()+1000*60*-8), new Timestamp(System.currentTimeMillis()+1000*60*-5)));//3mn Asignado

        eventStates.add(createEventState(17,events.get(5), states.get(3), new Timestamp(System.currentTimeMillis()+1000*60*-10), new Timestamp(System.currentTimeMillis()+1000*60*-8)));//2mn pendiente 16
        eventStates.add(createEventState(18,events.get(5), states.get(4), new Timestamp(System.currentTimeMillis()+1000*60*-8), new Timestamp(System.currentTimeMillis()+1000*60*-5)));//3mn Asignado
        eventStates.add(createEventState(19,events.get(5), states.get(5), new Timestamp(System.currentTimeMillis()+1000*60*-5), new Timestamp(System.currentTimeMillis())));//5mn en proceso

        eventStates.add(createEventState(20,events.get(6), states.get(3), new Timestamp(System.currentTimeMillis()+1000*60*-10), new Timestamp(System.currentTimeMillis()+1000*60*-8)));//2mn pendiente 19
        eventStates.add(createEventState(21,events.get(6), states.get(4), new Timestamp(System.currentTimeMillis()+1000*60*-8), new Timestamp(System.currentTimeMillis()+1000*60*-5)));//3mn Asignado
        eventStates.add(createEventState(22,events.get(6), states.get(5), new Timestamp(System.currentTimeMillis()+1000*60*-5), new Timestamp(System.currentTimeMillis())));//5mn en proceso

        eventStates.add(createEventState(23,events.get(7), states.get(3), new Timestamp(System.currentTimeMillis()+1000*60*-10), new Timestamp(System.currentTimeMillis()+1000*60*-8)));//2mn pendiente 22
        eventStates.add(createEventState(24,events.get(7), states.get(4), new Timestamp(System.currentTimeMillis()+1000*60*-8), new Timestamp(System.currentTimeMillis()+1000*60*-5)));//3mn Asignado
        eventStates.add(createEventState(25,events.get(7), states.get(5), new Timestamp(System.currentTimeMillis()+1000*60*-5), new Timestamp(System.currentTimeMillis())));//5mn en proceso

        eventStates.add(createEventState(26,events.get(8), states.get(3), new Timestamp(System.currentTimeMillis()+1000*60*-10), new Timestamp(System.currentTimeMillis()+1000*60*-8)));//2mn pendiente 25
        eventStates.add(createEventState(27,events.get(8), states.get(4), new Timestamp(System.currentTimeMillis()+1000*60*-8), new Timestamp(System.currentTimeMillis()+1000*60*-5)));//3mn Asignado
        eventStates.add(createEventState(28,events.get(8), states.get(5), new Timestamp(System.currentTimeMillis()+1000*60*-5), new Timestamp(System.currentTimeMillis())));//5mn en proceso

    }
    private EventTrack createEventState(long id,Event event,State state,Timestamp start,Timestamp end){
        EventTrack eventState=new EventTrack();
        eventState.setEndTime(end);
        eventState.setStartTime(start);
        eventState.setState(state);
        eventState.setId(id);
        return eventState;
    }

    private void createEvents() {
        events=new ArrayList<>();
        events.add(createEvent(1, 450, new Timestamp(System.currentTimeMillis()+1000*60*5), subCategories.get(0), states.get(3), users.get(0)));
        events.add(createEvent(2, 900, new Timestamp(System.currentTimeMillis()+1000*60*2), subCategories.get(1), states.get(4), users.get(1)));
        events.add(createEvent(3, 1000, new Timestamp(System.currentTimeMillis()+1000*60), subCategories.get(2), states.get(5), users.get(2)));
        events.add(createEvent(4, 450, new Timestamp(System.currentTimeMillis()-1000*60*4), subCategories.get(3), states.get(6), users.get(3)));
        events.add(createEvent(5, 450, new Timestamp(System.currentTimeMillis()-1000*60*10), subCategories.get(3), states.get(7), users.get(3)));
        events.add(createEvent(6, 400, new Timestamp(System.currentTimeMillis()+1000*60*0), subCategories.get(2), states.get(5), users.get(3)));
        events.add(createEvent(7, 450, new Timestamp(System.currentTimeMillis()+1000*60*(-10)), subCategories.get(1), states.get(3), users.get(2)));
        events.add(createEvent(8, 450, new Timestamp(System.currentTimeMillis()+1000*60), subCategories.get(0), states.get(6), users.get(1)));
        events.add(createEvent(9, 450, new Timestamp(System.currentTimeMillis()+1000*60*0), subCategories.get(1), states.get(5), users.get(0)));
    }   
    private Event createEvent(long id,int priority,Timestamp creation,Category sub,State state,User user){
        Event event=new Event();
        event.setCreation(creation);
        event.setId(id);
        event.setCategory(sub);
        return event;
    }

    private void createUsers() {
        users=new ArrayList<>();
        users.add(createUser(1,"con1",states.get(0)));//2 eventos
        users.add(createUser(2,"con2",states.get(0)));//2 eventos
        users.add(createUser(3,"con3",states.get(0)));//2 eventos
        users.add(createUser(4,"con4",states.get(0)));//3 eventos
        
    }
    private Controller createUser(int id,String name,State state){
        Controller user=new Controller();
        user.setId(id);
        user.setName(name);
        return user;
    }

    private void createStates() {
        states=new ArrayList<>();

        states.add(createState(StateValue.Available.name()));//0
        states.add(createState(StateValue.Busy.name()));//1
        states.add(createState(StateValue.Unavailable.name()));//2

        states.add(createState(StateValue.Pending.name()));//3
        states.add(createState(StateValue.Assigned.name()));//4
        states.add(createState(StateValue.In_Proccess.name()));//5
        states.add(createState(StateValue.On_Hold.name()));//6
        states.add(createState(StateValue.Verification.name()));//7

        states.add(createState(StateValue.Assigned.name()));//8

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
    }

    private Category createSubCategory(int id,String name,int priority){
        Category cat=new Category();
        cat.setId(id);
        cat.setName(name);
        cat.setBasePriority(priority);
        return cat;
    }
  
}