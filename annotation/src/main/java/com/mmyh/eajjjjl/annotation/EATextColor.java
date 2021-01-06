package com.mmyh.eajjjjl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EATextColor {

    String value();

    String[] switch_key() default {};

    String[] switch_value() default {};

}
