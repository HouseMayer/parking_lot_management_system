package com.example.aspect;


/*
    实现公共字段填充
 */

import com.example.annotation.AutoFill;
import com.example.constant.AutoFillConstant;
import com.example.context.BaseContext;
import com.example.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Slf4j
@Aspect
@Component
public class AutoFillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.example.mapper.*.*(..)) && @annotation(com.example.annotation.AutoFill)")
    public void autoFillPointCut(){}

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        // 判断是否开始填充公共字段
        log.info("公共字段填充开始...");

        // 获取方法签名对象
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        // 获取注解对象
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);

        // 获取注解的值
        OperationType operationType = autoFill.value();

        // 获取注解参数
        Object[] args = joinPoint.getArgs();

        // 如果没有参数则返回
        if (args == null || args.length == 0){
            return;
        }

        // 获取实体对象
        Object entity = args[0];

        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 获取当前用户ID
        Long currentId = BaseContext.getCurrentId();

        // 如果操作类型为INSERT
        if (operationType == OperationType.INSERT){
            try {
                // 获取设置创建时间的方法
                Method setCreateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);

                // 获取设置更新时间的方法
                Method setUpDateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                // 获取设置创建用户的方法
                Method setCreateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_CREATE_USER, Long.class);

                // 获取设置更新用户的方法
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 调用设置创建时间的方法
                setCreateTime.invoke(entity,now);

                // 调用设置更新时间的方法
                setUpDateTime.invoke(entity,now);

                // 调用设置创建用户的方法
                setCreateUser.invoke(entity,currentId);

                // 调用设置更新用户的方法
                setUpdateUser.invoke(entity,currentId);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        // 如果操作类型为UPDATE
        else if (operationType == OperationType.UPDATE){
            try {
                // 获取设置更新时间的方法
                Method setUpDateTime = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);

                // 获取设置更新用户的方法
                Method setUpdateUser = entity.getClass().getDeclaredMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);

                // 调用设置更新时间的方法
                setUpDateTime.invoke(entity,now);

                // 调用设置更新用户的方法
                setUpdateUser.invoke(entity,currentId);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
