package com.mmyh.eajjjjl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface EAApi {

    @Deprecated
    String api() default "";

    Class<?> apiClass() default Object.class;

    String apiMethod() default "";

    Class<?> request();

    Class<?> callBack() default Object.class;

    boolean controlLoadingDialog() default false;

    Class[] params() default {};

}
