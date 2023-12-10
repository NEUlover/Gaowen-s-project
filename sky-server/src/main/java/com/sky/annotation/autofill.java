package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//加在方法上
@Retention(RetentionPolicy.RUNTIME)//存活到运行时
public @interface autofill {
    OperationType value();
}
