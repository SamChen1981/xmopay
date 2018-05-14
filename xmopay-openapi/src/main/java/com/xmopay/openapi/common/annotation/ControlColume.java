package com.xmopay.openapi.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ControlColume {
    
    String value() default "";
    
    int length() default -1;
    
    boolean requid() default false;
    
} 
