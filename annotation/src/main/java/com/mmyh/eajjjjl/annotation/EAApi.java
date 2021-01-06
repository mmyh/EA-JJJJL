package com.mmyh.eajjjjl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.CLASS)
public @interface EAApi {

    String api();

    Class<?> request();

    Class<?> callBack();

    boolean showErrorToast() default true;

    boolean showSuccessToast() default false;

    boolean controlLoadingDialog() default false;

    Class[] params() default {};

}
