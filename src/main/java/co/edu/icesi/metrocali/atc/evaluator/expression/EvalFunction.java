package co.edu.icesi.metrocali.atc.evaluator.expression;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents an util function in the evaluator's context. It must be in a public method, instead
 * this annotationt won't be detected.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface EvalFunction {

    public Info[] value() default {};

    public String description() default "";

    @Retention(RetentionPolicy.RUNTIME)
    @Repeatable(value = EvalFunction.class)
    public @interface Info {
        public String key() default "";

        public String value();
    }

}


