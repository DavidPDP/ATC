package co.edu.icesi.metrocali.atc.evaluator.expression;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.icesi.metrocali.atc.constants.NotificationType;
import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalParameter;
import co.edu.icesi.metrocali.atc.entities.events.Category;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.State;
import co.edu.icesi.metrocali.atc.entities.operators.Controller;
import co.edu.icesi.metrocali.atc.entities.policies.Setting;
import co.edu.icesi.metrocali.atc.repositories.CategoriesRepository;
import co.edu.icesi.metrocali.atc.repositories.EventsRepository;
import co.edu.icesi.metrocali.atc.repositories.OperatorsRepository;
import co.edu.icesi.metrocali.atc.repositories.SettingsRepository;
import co.edu.icesi.metrocali.atc.repositories.evaluator.EvalParametersRepository;
import co.edu.icesi.metrocali.atc.services.notifications.events.EventStateChangeConcerner;
import co.edu.icesi.metrocali.atc.vos.StateNotification;

@Service
public class Context implements EventStateChangeConcerner {

    public static final String EVENTSQHSS = "eventsQHSs";
    public static final String EVENTSQHSS_Day = "eventsQHSsDay";
    public static final String LAST_EVENTS = "lastEvents";
    public static final String EVENTS_DONE = "eventsDone";
    public static final String EVENTS_CONTROLLER = "eventsController";
    public static final String CONTROLLERS = "controllers";
    public static final String THRESHOLDS = "thresholds";
    public static final String PRIORITIES = "priorities";

    @Autowired
    private Functions functions;

    @Autowired
    private SpringExpressions interpreter;

    @Autowired
    private EvalParametersRepository parameters;

    private HashMap<String, Object> variables;
    private HashMap<String, String> variablesDesc;

    // AVIOM repositories

    @Autowired
    private CategoriesRepository categories;
    @Autowired
    private OperatorsRepository operators;
    @Autowired
    private SettingsRepository settings;
    @Autowired
    private EventsRepository events;

    @PostConstruct
    public void loadSystemVariables() {
        variables = new HashMap<>();
        variablesDesc = new HashMap<>();
        addVar(EVENTSQHSS, new ArrayList<Integer>(), "");
        addVar(EVENTSQHSS_Day, new ArrayList<Integer>(), "");
        addVar(LAST_EVENTS, new ArrayList<Event>(), "");
        addVar(EVENTS_DONE, new HashMap<Integer, Integer>(), "");
        addVar(EVENTS_CONTROLLER, new HashMap<Integer, List<Integer>>(), "");
        addVar(CONTROLLERS, new HashMap<String, Controller>(), "");
        loadPrioritiesAndThreshold();
        loadVariableDesc();
        loadEvents();
        updateCotrollers();
    }
    private void loadEvents(){
        Setting interval = settings.retrieve(SettingKey.Recover_Time.name());
		List<Event> eventsResult = Collections.emptyList();			
        eventsResult = events.retrieveAll(interval.getValue());
        setValueForVar(LAST_EVENTS, eventsResult);
        updateLastEvent();
					
    }
    public void updateCotrollers(){
        loadControllers(operators.retrieveOnlineControllers());
    }


    private void loadVariableDesc() {
        variablesDesc.put(EVENTSQHSS,
                "Contiene el histórico de los tamaños de la cola de eventos desde la última vez que se realizó el cálculo de los KPI.");
        variablesDesc.put(EVENTSQHSS_Day, "Contiene el histórico de los tamaños de la cola de eventos en todo el día.");
        variablesDesc.put(LAST_EVENTS,
                "Lista con los últimos eventos generados desde la última vez que se realizó el cálculo de los KPI.");
        variablesDesc.put(EVENTS_DONE,
                "HashMap con los id de los controladores como claves. Los valores del hashmap son  la cantidad de eventos que fueron atendidos por el controlador desde la última vez que se realizó el cálculo de los KPI.");
        variablesDesc.put(EVENTS_CONTROLLER,
                "Hashmap donde key es el id del controlador y value es la lista que guarda la cantidad de eventos atendidos por cada controlador en cada periodicidad.");
        variablesDesc.put(CONTROLLERS,
                "HashMap donde la clave es el id del controlador y el valor es el controlador con ese id.");
        variablesDesc.put(THRESHOLDS,
                "Lista que contiene los umbrales definidos para cada tipo de evento. key representa el id del umbral y value representa el valor del mismo.");
        variablesDesc.put(PRIORITIES,
                "Hashmap que almacena todas las prioridades disponibles para cualquier tipo de evento.");
    }

    public void fillVariables() {
        loadEventsDone();
        loadEventsController();
        Iterator<String> keys = variables.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            interpreter.setVariable(key, variables.get(key));
        }
    }

    public void loadControllers(List<Controller> controllers) {
        if(controllers.isEmpty()){
            return;
        }
        HashMap<Integer, Integer> eventsDone = (HashMap<Integer, Integer>) variables.get(EVENTS_DONE);
        HashMap<Integer, List<Integer>> eventsController = (HashMap<Integer, List<Integer>>) variables.get(EVENTS_CONTROLLER);
        HashMap<Integer, Integer> eventsDoneB=new HashMap<>();
        HashMap<Integer, List<Integer>> eventsControllerB=new HashMap<>();
        HashMap<String, Controller> cMap = new HashMap<String, Controller>();
        for (Controller user : controllers) {
            cMap.put(user.getId() + "", user);
            if(eventsDone.containsKey(user.getId())){
                eventsDoneB.put(user.getId(), eventsDone.get(user.getId()));
            }else{
                eventsDoneB.put(user.getId(),0);
            }
            if(eventsController.containsKey(user.getId())){
                eventsControllerB.put(user.getId(), eventsController.get(user.getId()));
            }else{
                eventsControllerB.put(user.getId(), new ArrayList<>());
            }
        }
        setValueForVar(CONTROLLERS, cMap);
        setValueForVar(EVENTS_DONE, eventsDoneB);
        setValueForVar(EVENTS_CONTROLLER, eventsControllerB);

    }

    private void loadPrioritiesAndThreshold() {
        List<Category> categoriesList = categories.retrieveAll();
        HashSet<Integer> priorities = new HashSet<>();
        HashMap<Integer, Double> threshold = new HashMap<Integer, Double>();

        for (Category category : categoriesList) {
            Integer priority = category.getBasePriority();
            if(priority!=null){
                priorities.add(priority);
            }
        }
        for (Integer integer : priorities) {
            Optional<EvalParameter> parameter = parameters.retrieveActiveByName("threshold" + integer);
            if (parameter.isPresent()) {
                threshold.put(integer, parameter.get().getValue());
            }
        }
        setValueForVar(THRESHOLDS, threshold);
        setValueForVar(PRIORITIES, new ArrayList<>(priorities));
    }

    private void loadEventsDone() {
        HashMap<Integer, Integer> eventsDone = (HashMap<Integer, Integer>) variables.get(EVENTS_DONE);
        List<Event> lEvents = (List<Event>) variables.get(LAST_EVENTS);
        for (Event event : lEvents) {
            if (isEventDone(event)) {
                List<EventTrack> tracks = event.getEventsTracks();
                int idController = -1;
                for (int i = tracks.size() - 1; i >= 0; i--) {
                    State state = tracks.get(i).getState();
                    if (state.getName().equals(StateValue.In_Proccess.name())) {
                        idController = tracks.get(i).getUser().getId();
                        break;
                    }
                }
                Integer val = eventsDone.get(idController);
                if (val == null) {
                    val=0;
                }
                val++;
                eventsDone.put(idController, val);

            }
        }
        setValueForVar(EVENTS_DONE, eventsDone);
    }

    public void updateLastEvent() {
        List<Event> lEvents = (List<Event>) variables.get(LAST_EVENTS);
        List<Event> events = new ArrayList<>();
        for (Event event : lEvents) {
            if (!isEventDone(event)) {
                events.add(event);
            }
        }
        setValueForVar(LAST_EVENTS, events);

    }

    private boolean isEventDone(Event event) {
        State lastState = event.getLastEventTrack().getState();
        boolean ret = lastState.getName().equals(StateValue.Verification.name());
        ret |= lastState.getName().equals(StateValue.Archived.name());
        return ret;
    }

    private void loadEventsController() {
        HashMap<Integer, Integer> eventsDone = (HashMap<Integer, Integer>) variables.get(EVENTS_DONE);
        HashMap<Integer, List<Integer>> eventsController = (HashMap<Integer, List<Integer>>) variables.get(EVENTS_CONTROLLER);
        HashMap<String, Controller> controllers= (HashMap<String, Controller>) variables.get(CONTROLLERS);
        Iterator<Controller> values=controllers.values().iterator();
        while(values.hasNext()){
            int key = values.next().getId();
            Integer lastElement = eventsDone.get(key);
            if (lastElement == null) {
                lastElement = 0;
            }
            List<Integer> elements = eventsController.get(key);
            if (elements == null) {
                elements = new ArrayList<>();
            }
            elements.add(lastElement);
            eventsController.put(key, elements);
        }
        setValueForVar(EVENTS_CONTROLLER, eventsController);
    }

    public Functions getRootObject() {
        return functions;
    }

    public void addVar(String name, Object val, String desc) {
        variables.put(name, val);
        variablesDesc.put(name, desc);
        interpreter.setVariable(name, val);
    }

    public Object getVar(String name) {
        return variables.get(name);
    }

    public List<FunctionInfo> getFunctionInf() {
        List<FunctionInfo> functionsInfo = new ArrayList<>();

        for (Method method : Functions.class.getMethods()) {
            if (method.isAnnotationPresent(EvalFunction.class)) {
                functionsInfo.add(new FunctionInfo(method));
            }
        }
        return functionsInfo;
    }

    public void setValueForVar(String name, Object value) {
        variables.put(name, value);
        interpreter.setVariable(name, value);
    }

    public void setValueForVar(HashMap<String, ?> map) {
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            variables.put(key, map.get(key));
        }
    }

    public HashMap<String,Object> getVariables(){
        return variables;
    }
    public HashMap<String, String> getVariablesDesc() {
        return variablesDesc;
    }

    @Override
    public void update(StateNotification notification) {
        List<Event> lastEvent=(List<Event>) variables.get(LAST_EVENTS);
        List<Integer> eventsQHSs = (List<Integer>) variables.get(EVENTSQHSS);
        List<Integer> eventsQHSsDay = (List<Integer>) variables.get(EVENTSQHSS_Day);
        int lastSize=eventsQHSs.isEmpty()?0:eventsQHSs.get(eventsQHSs.size()-1);
        if(notification.getType()==NotificationType.New_Event_Entity||notification.getType()==NotificationType.New_Event_Assignment){
            if(notification.getType()==NotificationType.New_Event_Assignment){
                lastSize+=lastSize>0?-1:0;
            }else{
                Event newEvent=(Event)notification.getElementsInvolved()[0];
                lastEvent.add(newEvent);
                lastSize++;
                setValueForVar(LAST_EVENTS,lastEvent);
            }
            eventsQHSs.add(lastSize);
            eventsQHSsDay.add(lastSize);
            setValueForVar(EVENTSQHSS, eventsQHSs);
            setValueForVar(EVENTSQHSS_Day, eventsQHSsDay);
        }else if(notification.getType()==NotificationType.New_Available_Controller){
            Controller cont=(Controller)notification.getElementsInvolved()[0];
            HashMap<String,Controller> controllers= (HashMap<String, Controller>) variables.get(CONTROLLERS);
            controllers.put(cont.getId()+"", cont);
            setValueForVar(CONTROLLERS, controllers);
        }
        
    }
}
