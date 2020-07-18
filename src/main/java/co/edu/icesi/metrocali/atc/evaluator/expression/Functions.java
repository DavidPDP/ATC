package co.edu.icesi.metrocali.atc.evaluator.expression;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import co.edu.icesi.metrocali.atc.constants.StateValue;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalController;
import co.edu.icesi.metrocali.atc.entities.evaluator.EvalEvent;
import co.edu.icesi.metrocali.atc.entities.events.EventTrack;
import co.edu.icesi.metrocali.atc.entities.events.UserTrack;
import co.edu.icesi.metrocali.atc.evaluator.expression.EvalFunction.Info;

@Service
public class Functions {

    @Autowired
    private Context context;

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
    public List<Double> timesInPendingState(List<EvalEvent> events) {
        List<Double> ret = new ArrayList<>();
        for (EvalEvent event : events) {
            List<EventTrack> eventTracks = event.getEventsTracks();

            double time = 0;
            for (EventTrack eventTrack : eventTracks) {

                boolean inPending =
                        eventTrack.getState().getName().equalsIgnoreCase(StateValue.Pending.name());
                if (inPending) {
                    Timestamp last=eventTrack.getEndTime();
                    if(last==null){
                        last=new Timestamp(System.currentTimeMillis());
                    }
                    time += last.getTime() - eventTrack.getStartTime().getTime();
                }
            }
            time/=(1000*60);
            ret.add(time);
        }
        return ret;
    }

    @EvalFunction(
            description = "Lista de los tiempos los que ha estado cada evento, en la lista del parámetro, en estado asignado. En esta función se filtran los estados “en asignado” y se suman todas las diferencias entre el tiempo final y de inicio de cada uno. Esta función hace uso del atributo de timestamp que tiene la entidad evento (revisar modelo de datos)")
    public List<Double> timesInAssignedState(List<EvalEvent> events) {
        List<Double> ret = new ArrayList<>();
        for (EvalEvent event : events) {
            List<EventTrack> eventTracks = event.getEventsTracks();
            double time = 0;
            for (EventTrack eventTrack : eventTracks) {
                boolean assigned = eventTrack.getState().getName()
                        .equalsIgnoreCase(StateValue.Approbing.name());
                if (assigned) {
                    Timestamp last=eventTrack.getEndTime();
                    if(last==null){
                        last=new Timestamp(System.currentTimeMillis());
                    }
                    time += last.getTime() - eventTrack.getStartTime().getTime();
                }
            }
            time/=(1000*60);
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
    public double max(Double ... list) {
        double max = Double.MIN_VALUE;
        for (Double num : list) {
            max = Double.max(num, max);
        }
        return max;
    }

    @EvalFunction(
            description = "lista de tiempos en que ha estado cada evento en el estado “en proceso”, teniendo en cuenta los timestamp de los event_state en la lista del parámetro.")
    public List<Double> inProcessTime(List<EvalEvent> events) {
        List<Double> ret = new ArrayList<>();
        for (EvalEvent event : events) {
            List<EventTrack> eventTracks = event.getEventsTracks();
            double time = 0;
            for (EventTrack eventTrack : eventTracks) {
                boolean inProcess = eventTrack.getState().getName()
                        .equalsIgnoreCase(StateValue.Processing.name());
                if (inProcess) {
                    Timestamp last=eventTrack.getEndTime();
                    if(last==null){
                        last=new Timestamp(System.currentTimeMillis());
                    }
                    time += last.getTime() - eventTrack.getStartTime().getTime();
                }
            }
            time/=(1000*60);
            ret.add(time);
        }
        return ret;
    }

    @EvalFunction(
            description = "lista de tiempos en que ha estado cada evento en el estado “en espera”, en la lista del parámetro.")
    public List<Double> inHold(List<EvalEvent> events) {
        List<Double> ret = new ArrayList<>();
        for (EvalEvent event : events) {
            List<EventTrack> eventTracks = event.getEventsTracks();
            double time = 0;
            for (EventTrack eventTrack : eventTracks) {
                boolean inHold =
                        eventTrack.getState().getName().equalsIgnoreCase(StateValue.Waiting.name());
                if (inHold) {
                    Timestamp last=eventTrack.getEndTime();
                    if(last==null){
                        last=new Timestamp(System.currentTimeMillis());
                    }
                    time += last.getTime() - eventTrack.getStartTime().getTime();
                }
            }
            time/=(1000*60);
            ret.add(time);
        }
        return ret;
    }

    @EvalFunction(
            description = "Lista de los tiempos de estancia de cada evento en la lista parámetro.")
    public List<Double> lenghtOfStay(List<EvalEvent> events) {
        List<Double> ret = new ArrayList<>();
        for (EvalEvent event : events) {
            List<EventTrack> eventTracks = event.getEventsTracks();
            double time = 0;
            for (EventTrack eventTrack : eventTracks) {

                boolean stayTime =
                        eventTrack.getState().getName().equalsIgnoreCase(StateValue.Waiting.name());
                stayTime |= eventTrack.getState().getName()
                        .equalsIgnoreCase(StateValue.Approbing.name());
                stayTime |= eventTrack.getState().getName()
                        .equalsIgnoreCase(StateValue.Processing.name());
                stayTime |=
                        eventTrack.getState().getName().equalsIgnoreCase(StateValue.Pending.name());

                if (stayTime) {
                    Timestamp last=eventTrack.getEndTime();
                    if(last==null){
                        last=new Timestamp(System.currentTimeMillis());
                    }
                    time += last.getTime() - eventTrack.getStartTime().getTime();
                }
            }
            time/=(1000*60);
            ret.add(time);
        }
        return ret;
    }

    @EvalFunction(
            description = "eventos agrupados por prioridad en una hashMap (priority,List<Event>)")
    public HashMap<Integer, List<EvalEvent>> groupByPriority(List<EvalEvent> events) {
        HashMap<Integer, List<EvalEvent>> groupsByPriority = new HashMap<>();
        List<Integer> priorities= (List<Integer>) context.getVar(Context.PRIORITIES);
        for (Integer integer : priorities) {
            groupsByPriority.put(integer, new ArrayList<EvalEvent>());
        }
        for (EvalEvent event : events) {

            int priority = event.getCategory().getBasePriority();
            if (!groupsByPriority.containsKey(priority)) {
                groupsByPriority.put(priority, new ArrayList<EvalEvent>());
            }
            List<EvalEvent> value = groupsByPriority.get(priority);
            value.add(event);
            groupsByPriority.put(priority, value);
        }
        return groupsByPriority;
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
    public double busyTime(EvalController controller) {
        double busyTime = 0;
        
        List<UserTrack> userTracks = controller.getUserTracks();
        for (UserTrack userTrack : userTracks) {
            boolean isBusy = userTrack.getState().getName().equalsIgnoreCase(StateValue.Busy.name());
            if (isBusy) {
                Timestamp last=userTrack.getEndTime();
                if(last==null){
                    last=new Timestamp(System.currentTimeMillis());
                }
                busyTime += last.getTime() - userTrack.getStartTime().getTime();
            }
        }
        busyTime/=(1000*60);
        return busyTime;
    }

    @EvalFunction(
            description = "Retorna una lista cuyos elementos son el resultado de aplicar la expresión sobre cada elemento de la lista. El tipo de los elementos dependerá del tipo del resultado de la expresión. Por ejemplo, si la expresión retorna un número entonces map retornará una lista de números.")
    @Info(key = "example", value = "map({2,3,5,1},”#value*3”) -> {6,9,15,3}")
    public List<?> map(List<?> elements, String expression) {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext(this);
        HashMap<String,Object> variables=this.context.getVariables();
        context.setVariables(variables);
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
        HashMap<String,Object> variables=this.context.getVariables();
        context.setVariables(variables);
        HashMap<Object, Object> ret = new HashMap<>();
        Iterator<?> keys = elements.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            Object hashVal=elements.get(key);
            context.setVariable("value", hashVal);
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
    public List<EvalEvent> sortE(List<EvalEvent> events) {
        if (events.isEmpty()) {
            return new ArrayList<>();
        }
        events.sort(new Comparator<EvalEvent>() {

            @Override
            public int compare(EvalEvent o1, EvalEvent o2) {
                return Long.compare(o1.getId(), o2.getId());
            }
        });

        return events;
    }


    @EvalFunction(
            description = "Retorna el tiempo de estancia, suma de todas las diferencias de tiempo de los estados en los que ha estado el controlador pasado como parámetro.")
    @Info(key = "examples", value = "controllerStay(#controllers[0])")
    public double controllerStay(EvalController controller) {
        double stayTime = 0;
        List<UserTrack> userTracks = controller.getUserTracks();
        for (UserTrack userTrack : userTracks) {
            boolean all = userTrack.getState().getName().equalsIgnoreCase(StateValue.Busy.name());
            all |= userTrack.getState().getName().equalsIgnoreCase(StateValue.Available.name());
            all |= userTrack.getState().getName().equalsIgnoreCase(StateValue.Unavailable.name());
            
            if (all) {
                Timestamp last=userTrack.getEndTime();
                if(last==null){
                    last=new Timestamp(System.currentTimeMillis());
                }
                stayTime += last.getTime() - userTrack.getStartTime().getTime();
            }
        }
        stayTime/=(1000*60);
        return stayTime;
    }
    @EvalFunction(
        description ="Retorna la representación en cadena del objeto")
    public String s(Object o){
        return o.toString();
    }

    //Math Functions
    @EvalFunction(
        description ="Retorna el seno del angulo, en radianes, pasado por parámetro")
    public double sin(double n){
        return Math.sin(n);
    }
    @EvalFunction(
        description ="Retorna el coseno del angulo, en radianes, pasado por parámetro")
    public double cos(double n){
        return Math.cos(n);
    }
    @EvalFunction(
        description ="Retorna el resultado de elevar el primer parámetro al segundo")
        @Info(key = "examples", value = "pow(2,3)=2^3=8")
    public double pow(double a, double b){
        return Math.pow(a, b);
    }
    @EvalFunction(
        description ="Retorna la raiz cuadrada del número pasado por parámetro ")
    public double sqrt(double a){
        return Math.sqrt(a);
    }
    @EvalFunction(
        description ="Convierte un angulo medido en grados a uno en radianes")
    public double toRadians(double angle){
        return Math.toRadians(angle);
    }



}
