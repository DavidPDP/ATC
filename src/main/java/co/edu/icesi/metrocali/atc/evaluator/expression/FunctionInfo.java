package co.edu.icesi.metrocali.atc.evaluator.expression;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import org.springframework.data.util.Pair;
import co.edu.icesi.metrocali.atc.evaluator.expression.EvalFunction.Info;


public class FunctionInfo {

    @JsonProperty(value = "name", access = Access.READ_ONLY)
    private String name;

    @JsonProperty(value = "parameters", access = Access.READ_ONLY)
    private List<ElementInfo> parametersInfo;

    @JsonProperty(value = "return_type", access = Access.READ_ONLY)
    private String returnInfo;

    @JsonProperty(value = "description", access = Access.READ_ONLY)
    private String description;

    @JsonProperty(value = "extra", access = Access.READ_ONLY)
    private List<Pair<String, String>> extra;



    public FunctionInfo(Method method) {
        parametersInfo = getParametersInfo(method);
        returnInfo = getReturnInfo(method);
        description = method.getAnnotation(EvalFunction.class).description();
        setExtraInfo(method);
        name = method.getName();

    }



    private void setExtraInfo(Method method) {
        extra = new ArrayList<>();
        for (Info info : method.getAnnotationsByType(Info.class)) {
            Pair<String, String> pair = Pair.of(info.key(), info.value());
            extra.add(pair);
        }
    }



    private String getReturnInfo(Method method) {
        String type = method.getReturnType().getName();
        return type;
    }


    private List<ElementInfo> getParametersInfo(Method method) {
        List<ElementInfo> infos = new ArrayList<>();
        for (Parameter parameter : method.getParameters()) {
            String type = parameter.getType().getName();
            String name = parameter.getName();
            infos.add(new ElementInfo(type, name));
        }
        return infos;
    }


    private class ElementInfo {

        @JsonProperty(value = "type", access = Access.READ_ONLY)
        private String type;

        @JsonProperty(value = "name", access = Access.READ_ONLY)
        private String name;

        public ElementInfo(String type, String name) {
            this.type = type;
            this.name = name;
        }

    }

}
