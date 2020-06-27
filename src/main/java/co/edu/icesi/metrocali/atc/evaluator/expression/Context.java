package co.edu.icesi.metrocali.atc.evaluator.expression;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.repositories.evaluator.EvalParametersRepository;



@Service
public class Context {

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

    // TODO: Integration:
    // @Autowired
    // private UserService userService;
    // @Autowired
    // private SchedulingService schedulingService;
    // @Autowired
    // private EventService eventService;

    @Autowired
    private EvalParametersRepository parameters;
    private HashMap<String, Object> variables;
    private HashMap<String, String> variablesDesc;

    @PostConstruct
    public void loadSystemVariables() {
        variables = new HashMap<>();
        variablesDesc = new HashMap<>();
        addVar(EVENTSQHSS, new ArrayList<Integer>(), "");
        addVar(EVENTSQHSS_Day, new ArrayList<Integer>(), "");
        addVar(LAST_EVENTS, new ArrayList<Event>(), "");
        addVar(EVENTS_DONE, new HashMap<Long, Integer>(), "");
        addVar(EVENTS_CONTROLLER, new HashMap<Long, List<Integer>>(), "");
        addVar(CONTROLLERS, new HashMap<String, User>(), "");
         //TODO: integración
        // loadPrioritiesAndThreshold();
        loadVariableDesc();
    }

    private void loadVariableDesc() {
        variablesDesc.put(EVENTSQHSS,
                "Contiene el histórico de los tamaños de la cola de eventos desde la última vez que se realizó el cálculo de los KPI.");
        variablesDesc.put(EVENTSQHSS_Day,
                "Contiene el histórico de los tamaños de la cola de eventos en todo el día.");
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

    private void fillVariables(Timestamp lastDate) {
         //TODO: integración
        // loadLastEventsAndEventsDone(lastDate);
        loadEventsController();
        // TODO: integración
        // loadControllersAviable();
        Iterator<String> keys = variables.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            interpreter.setVariable(key, variables.get(key));
        }
    }
    // TODO: integración
    // private void loadControllersAviable() {
    // //TODO: integración
    // List<User> controllers=userService.getOnlineControllers();
    // HashMap<String,User> cMap=new HashMap<String,User>();
    // for (User user : controllers) {
    // cMap.put(user.getId()+"", user);
    // }
    // setValueForVar(CONTROLLERS, cMap);

    // }

    // TODO: integración
    // private void loadPrioritiesAndThreshold() {
    // Iterable<SubCategory> subs = eventService.getSubcategories();
    // HashSet<Integer> priorities = new HashSet<>();
    // HashMap<Integer, Double> threshold = new HashMap<Integer, Double>();

    // for (SubCategory sCategory : subs) {
    // int priority = sCategory.getPriority();
    // priorities.add(priority);
    // }
    // for (Integer integer : priorities) {
    // EvalParameter parameter =
    // parameters.findByNameAndEnableEndIsNull("threshold" + integer);
    // if (parameter != null) {
    // threshold.put(integer, parameter.getValue());
    // }
    // }
    // addVar(THRESHOLDS, threshold, "");
    // addVar(PRIORITIES, priorities, "");
    // }

    // TODO: integración
    // private void loadLastEventsAndEventsDone(Timestamp lastDate) {
    // Iterable<Event> events = eventService.getEvents();
    // HashMap<Long, Integer> eventsDone = (HashMap<Long, Integer>) variables.get("eventsDone");
    // List<Event> lEvents = (List<Event>) variables.get("lastEvents");
    // for (Event event : events) {
    // if (event.getCreation().compareTo(lastDate) >= 0
    // || (event.getState().getId() != EventService.VERIFICATION
    // && event.getState().getId() != EventService.ARCHIVE)) {
    // lEvents.add(event);
    // if (isEventDone(event)) {
    // long idController = event.getUser().getId();
    // Integer val = eventsDone.get(idController);
    // if (val == null) {
    // eventsDone.put(idController, 1);
    // }
    // val++;
    // eventsDone.put(idController, val);
    // }
    // }
    // }
    // variables.put(LAST_EVENTS, lEvents);
    // variables.put(EVENTS_DONE, eventsDone);
    // }

    // TODO: integración
    // private boolean isEventDone(Event event) {
    // Long idVer = EventService.VERIFICATION;
    // Long idArc = EventService.ARCHIVE;
    // boolean ret = event.getState().getId() == idVer;
    // ret |= event.getState().getId() == idArc;
    // return ret;
    // }

    private void loadEventsController() {
        HashMap<Long, Integer> eventsDone = (HashMap<Long, Integer>) variables.get("eventsDone");
        HashMap<Long, List<Integer>> eventsController =
                (HashMap<Long, List<Integer>>) variables.get("eventsController");
        Iterator<Long> keys = eventsDone.keySet().iterator();
        while (keys.hasNext()) {
            long key = keys.next();
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
        variables.put(EVENTS_CONTROLLER, eventsController);
    }

    public Functions getRootObject() {
        fillVariables(Executor.LAST_EXECTUTION);
        return functions;
    }
    // TODO: integración
    // public void notifyChangesQueue() {
    // List<Integer> eventsQHSs = (List<Integer>) variables.get("eventsQHSs");
    // List<Integer> eventsQHSsDay = (List<Integer>) variables.get("eventsQHSsDay");
    // int size = schedulingService.getQueueSize();
    // eventsQHSs.add(size);
    // eventsQHSsDay.add(size);
    // setValueForVar(EVENTSQHSS, eventsQHSs);
    // setValueForVar(EVENTSQHSS_Day, eventsQHSsDay);
    // }

    public void addVar(String name, Object val, String desc) {
        variables.put(name, val);
        variablesDesc.put(name, desc);
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
    }

    public void setValueForVar(HashMap<String, ?> map) {
        Iterator<String> keys = map.keySet().iterator();
        while (keys.hasNext()) {
            String key = keys.next();
            variables.put(key, map.get(key));
        }
    }

    // TODO: Faltan las descripciones de cada variable ¿donde están?
    public HashMap<String, String> getVariablesDesc() {
        return variablesDesc;
    }
}
