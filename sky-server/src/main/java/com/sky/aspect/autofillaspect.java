package com.sky.aspect;


import com.sky.annotation.autofill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Component//交给ioc
@Aspect//切面类
@Slf4j
public class autofillaspect {

    /*
    * 切入点
    */
    @Pointcut("execution(* com.sky.mapper.*.*(..))&&@annotation(com.sky.annotation.autofill)")
    public void autofillPointcut(){}


    @Before("autofillPointcut()")
    public void autofill(JoinPoint joinPoint) {
        log.info("开始进行公共字段填充");
        //获取被拦截方法上的sql操作类型
       MethodSignature methodSignature= (MethodSignature) joinPoint.getSignature();//方法签名对象
        autofill autofill = methodSignature.getMethod().getAnnotation(autofill.class);
        OperationType operationType = autofill.value();//获取成员
        //获取方法的参数--实体对象
        Object[] args = joinPoint.getArgs();//默认只要第一个参数, 相当于参数数组
        if(args==null||args.length==0){
            return;
        }
        Object entity = args[0];//拿到了实体对象
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        //根据不同的操作类型来进行不同的操作
        if(operationType==OperationType.INSERT){
            try {
                Method setcreatetime =entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class);
                Method setupdatetime =entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setcreateuser =entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER,Long.class);
                Method setupdatesuer =entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                setcreatetime.invoke(entity,now);
                setupdatetime.invoke(entity,now);
                setcreateuser.invoke(entity,currentId);
                setupdatesuer.invoke(entity,currentId);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(operationType==OperationType.UPDATE){
            try {
                Method setupdatetime =entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class);
                Method setupdatesuer =entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER,Long.class);
                setupdatetime.invoke(entity,now);
                setupdatesuer.invoke(entity,currentId);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        log.info("填充完成");

        //用反射调用该类型的设置参数

    }
}
