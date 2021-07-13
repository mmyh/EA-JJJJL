package com.mmyh.eajjjjl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EAView {

    Class<?>[] viewModels() default {};

    Class<?> superClass() default Object.class;

    Class<?>[] bindings() default {};

    Class<?> listModel() default Object.class;

    Class<?>[] listBindings() default {};

    Class<?> headViewBinding() default Object.class;

    Class<?> headViewModel() default Object.class;

    Class<?> footViewBinding() default Object.class;

    Class<?> footViewModel() default Object.class;

}
