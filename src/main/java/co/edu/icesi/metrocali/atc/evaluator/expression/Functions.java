package co.edu.icesi.metrocali.atc.evaluator.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.constants.SettingKey;
import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.entities.events.Event;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.UserTrack;
import co.edu.icesi.metrocali.atc.entities.policies.User;
import co.edu.icesi.metrocali.atc.evaluator.expression.EvalFunction.Info;
import co.edu.icesi.metrocali.atc.services.entities.OperatorsService;

@Service
public class Functions {

    @Autowired
    private OperatorsService operatorsService;

    @EvalFunction(description = "Suma de los números del parámetro")
    public double sum(List<Double> values) {
        double val = 0;
        for (Double n : values) {
            val += n;
        }
        return val;
    }

    @EvalFunction(description = "tamaño de la lista.")
    public int len(List<?> list) {
        return list.size();
    }

    @EvalFunction(description = "promedio de los elementos en la lista.")
    public double average(List<Double> values) {
        double ret = sum(values);
        return ret / len(values);
    }

    @EvalFunction(
            description = "Lista de los tiempos en los que ha estado cada evento, en la lista del parámetro, en estado pendiente. En esta función se filtran los estados en pendiente y se suman todas las diferencias entre el tiempo final y de inicio de cada uno. Esta función hace uso del atributo de timestamp que tiene la entidad evento (revisar modelo de datos).")
    public List<Double> timesInPendingState(List<Event> events) {
        List<Double> ret = new ArrayList<>();
        for (Event event : events) {
            List<EventTrack> eventTracks = event.getEventsTracks();
            double time = 0;
            for (EventTrack eventTrack : eventTracks) {
                // TODO: REVIEW: State.name refers to StateValue.VALUE.name?
                boolean inPending =
                        eventTrack.getState().getName().equals(StateValue.Pending.name());
                if (inPending) {
                    time += eventTrack.getEndTime().getTime() - eventTrack.getStartTime().getTime();
                }
            }
            ret.add(time);
        }
        return ret;
    }

    @EvalFunction(
            description = "Lista de los tiempos los que ha estado cada evento, en la lista del parámetro, en estado asignado. En esta función se filtran los estados “en asignado” y se suman todas las diferencias entre el tiempo final y de inicio de cada uno. Esta función hace uso del atributo de timestamp que tiene la entidad evento (revisar modelo de datos)")
    public List<Double> timesInAssignedState(List<Event> events) {
        List<Double> ret = new ArrayList<>();
        for (Event event : events) {
            List<EventTrack> eventTracks = event.getEventsTracks();
            double time = 0;
            for (EventTrack eventTrack : eventTracks) {
                // TODO: REVIEW: State.name refers to StateValue.VALUE.name?
                boolean assigned =
                        eventTrack.getState().getName().equals(StateValue.Assigned.name());
                if (assigned) {
                    time += eventTrack.getEndTime().getTime() - eventTrack.getStartTime().getTime();
                }
            }
            ret.add(time);
        }
        return ret;
    }

    @EvalFunction(description = "lista que resulta de la suma de los elementos de las listas.")
    @Info(key = "example", value = "retorno[0]=parámetro1[0]+parámetro2[0]")
    public List<Double> vectorSum(List<Double> vect, List<Double> vect2) {
        if (vect.size() != vect2.size()) {
            return null;
        }
        List<Double> ret = new ArrayList<>();
        for (int i = 0; i < vect.size(); i++) {
            ret.add(vect.get(i) + vect2.get(i));
        }
        return ret;
    }

    @EvalFunction(description = "máximo número dentro de la lista")
    public double max(List<Double> list) {
        double max = Double.MIN_VALUE;
        for (Double num : list) {
            max = Double.max(num, max);
        }
        return max;
    }

    @EvalFunction(
            description = "lista de tiempos en que ha estado cada evento en el estado “en proceso”, teniendo en cuenta los timestamp de los event_state en la lista del parámetro.")
    public List<Double> inProcessTime(List<Event> events) {
        List<Double> ret = new ArrayList<>();
        for (Event event : events) {
            List<EventTrack> eventTracks = event.getEventsTracks();
            double time = 0;
            for (EventTrack eventTrack : eventTracks) {
                // TODO: REVIEW: State.name refers to StateValue.VALUE.name?
                boolean inProcess =
                        eventTrack.getState().getName().equals(StateValue.In_Proccess.name());
                if (inProcess) {
                    time += eventTrack.getEndTime().getTime() - eventTrack.getStartTime().getTime();
                }
            }
            ret.add(time);
        }
        return ret;
    }

    @EvalFunction(
            description = "lista de tiempos en que ha estado cada evento en el estado “en espera”, en la lista del parámetro.")
    public List<Double> inHold(List<Event> events) {
        List<Double> ret = new ArrayList<>();
        for (Event event : events) {
            List<EventTrack> eventTracks = event.getEventsTracks();
            double time = 0;
            for (EventTrack eventTrack : eventTracks) {
                // TODO: REVIEW: State.name refers to StateValue.VALUE.name?
                boolean inHold = eventTrack.getState().getName().equals(StateValue.On_Hold.name());
                if (inHold) {
                    time += eventTrack.getEndTime().getTime() - eventTrack.getStartTime().getTime();
                }
            }
            ret.add(time);
        }
        return ret;
    }

    @EvalFunction(
            description = "Lista de los tiempos de estancia de cada evento en la lista parámetro.")
    public List<Double> lenghtOfStay(List<Event> events) {
        List<Double> ret = new ArrayList<>();
        for (Event event : events) {
            List<EventTrack> eventTracks = event.getEventsTracks();
            double time = 0;
            for (EventTrack eventState : eventTracks) {
                // TODO: REVIEW: State.name refers to StateValue.VALUE.name?
                boolean stayTime =
                        eventState.getState().getName().equals(StateValue.On_Hold.name());
                stayTime |= eventState.getState().getName().equals(StateValue.Assigned.name());
                stayTime |= eventState.getState().getName().equals(StateValue.In_Proccess.name());
                stayTime |= eventState.getState().getName().equals(StateValue.Pending.name());
                if (stayTime) {
                    time += eventState.getEndTime().getTime() - eventState.getStartTime().getTime();
                }
            }
            ret.add(time);
        }
        return ret;
    }

    @EvalFunction(
            description = "eventos agrupados por prioridad en una hashMap (priority,List<Event>)")
    public HashMap<Integer, List<Event>> groupByPriority(List<Event> events) {
        HashMap<Integer, List<Event>> groups = new HashMap<>();
        for (Event event : events) {
            // REVIEW: event.getSubCategory().getPriority() is the same to
            // event.getCategory().getBasePriority()?
            int key = event.getCategory().getBasePriority();
            if (!groups.containsKey(key)) {
                groups.put(key, new ArrayList<Event>());
            }
            List<Event> value = groups.get(key);
            value.add(event);
            groups.put(key, value);
        }
        return groups;
    }

    @EvalFunction(
            description = "retorna qué porcentaje de los números de la lista en el parámetro 1 estuvieron por encima del parámetro 2")
    public double percentageAboveThreshold(List<Double> numbers, double threshold) {
        double count = 0;
        for (Double num : numbers) {
            count += num > threshold ? 1 : 0;
        }
        return count / numbers.size();
    }

    @EvalFunction(description = "desviación normal estándar de los datos.")
    public double stddev(List<Double> values) {
        double standardDeviation = 0.0;
        int length = values.size();
        double mean = average(values);
        for (double num : values) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        return Math.sqrt(standardDeviation / length);
    }

    @EvalFunction(
            description = "suma de los tiempos de todos los estados en ocupado del controlador.")
    public double busyTime(User controller) {
        double busyTime = 0;
        // INTEGRATION: REVIEW: Use OperatorsService or
        // OperatorsRepository.retrieveUserTrackHistory. But, how?
        // List<UserState> userStates = controller.getUsersStates();
        // for (UserState userState : userStates) {
        // boolean isBusy = userState.getId() == UserService.BUSY;
        // if (isBusy) {
        // busyTime += userState.getEndTime().getTime() - userState.getStartTime().getTime();
        // }
        // }

        // INTEGRATION: REVIEW: Try using operatorsService

        List<UserTrack> userTracks = operatorsService.retrieveUserTrackHistory(
                controller.getAccountName(), SettingKey.User_Track_Time.name());
        for (UserTrack userTrack : userTracks) {
            // TODO: REVIEW: State.name refers to StateValue.VALUE.name?
            boolean isBusy = userTrack.getState().getName().equals(StateValue.Busy.name());
            if (isBusy) {
                busyTime += userTrack.getEndTime().getTime() - userTrack.getStartTime().getTime();
            }
        }
        return busyTime;
    }

    @EvalFunction(
            description = "Retorna una lista cuyos elementos son el resultado de aplicar la expresión sobre cada elemento de la lista. El tipo de los elementos dependerá del tipo del resultado de la expresión. Por ejemplo, si la expresión retorna un número entonces map retornará una lista de números.")
    @Info(key = "example", value = "map({2,3,5,1},”#value*3”) -> {6,9,15,3}")
    public List<?> map(List<?> elements, String expression) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext(this);
        List<Object> ret = new ArrayList<>();
        for (Object object : elements) {
            context.setVariable("value", object);
            Object value = parser.parseExpression(expression).getValue(context);
            ret.add(value);
        }
        return ret;
    }

    @EvalFunction(
            description = "Retorna una hashmap cuyos elementos son el resultado de aplicar la expresión sobre cada elemento de la lista del hashmap. El tipo de los elementos dependerá del tipo del resultado de la expresión. Por ejemplo, si la expresión retorna un número entonces map retornará una hashmap que conservará las claves pero el valor para cada una de las claves será el número retornado por la expresión.")
    @Info(key = "example", value = "map( { 1: {1,2},2: {1,2} }, “sum(#value)”) -> { 1: 3, 2: 3 }")
    public HashMap<?, ?> map(HashMap<?, ?> elements, String expression) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext(this);
        HashMap<Object, Object> ret = new HashMap<>();
        Iterator<?> keys = elements.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            context.setVariable("value", elements.get(key));
            Object value = parser.parseExpression(expression).getValue(context);
            ret.put(key, value);
        }
        return ret;
    }

    @EvalFunction(
            description = "Una lista de objetos que contiene todos los valores de la hash. (No se garantiza un orden específico en la lista)")
    public List<?> toList(HashMap<?, ?> values) {
        List<Object> ret = new ArrayList<>();
        Iterator<?> keys = values.keySet().iterator();
        while (keys.hasNext()) {
            ret.add(values.get(keys.next()));
        }
        return ret;
    }

    @EvalFunction(
            description = "lista con los mismos eventos pero en orden ascendente por el identificador de cada evento.")
    public List<Event> sortE(List<?> events) {
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        Event[] eventsA = (Event[]) events.toArray();
        Arrays.sort(eventsA, new Comparator<Event>() {

            @Override
            public int compare(Event o1, Event o2) {
                return Long.compare(o1.getId(), o2.getId());
            }
        });
        return Arrays.asList(eventsA);
    }


    @EvalFunction(
            description = "Retorna el tiempo de estancia, suma de todas las diferencias de tiempo de los estados en los que ha estado el controlador pasado como parámetro.")
    @Info(key = "examples", value = "controllerStay(#controllers[0])")
    public double controllerStay(User controller) {
        double stayTime = 0;
        // INTEGRATION: REVIEW: Use OperatorsService or
        // OperatorsRepository.retrieveUserTrackHistory. But, how?
        // List<UserState> userStates = controller.getUsersStates();
        // for (UserState userState : userStates) {
        // boolean all = userState.getId() == UserService.BUSY;
        // all |= userState.getId() == UserService.AVAILABLE;
        // all |= userState.getId() == UserService.EVENT_ASSIGNED;
        // all |= userState.getId() == UserService.UNAVAILABLE;
        // if (all) {
        // stayTime += userState.getEndTime().getTime() - userState.getStartTime().getTime();
        // }
        // }
        List<UserTrack> userTracks = operatorsService.retrieveUserTrackHistory(
                controller.getAccountName(), SettingKey.User_Track_Time.name());
        for (UserTrack userTrack : userTracks) {
            // TODO: REVIEW: State.name refers to StateValue.VALUE.name?
            boolean all = userTrack.getState().getName().equals(StateValue.Busy.name());
            all |= userTrack.getState().getName().equals(StateValue.Available.name());
            all |= userTrack.getState().getName().equals(StateValue.Assigned.name());
            all |= userTrack.getState().getName().equals(StateValue.Unavailable.name());
            if (all) {
                stayTime += userTrack.getEndTime().getTime() - userTrack.getStartTime().getTime();
            }
        }
        return stayTime;
    }

}
