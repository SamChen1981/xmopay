package com.xmopay.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.UUID;

/**
 * com.xmopay.aop
 *
 * @author echo_coco.
 * @date 10:34 AM, 2018/5/2
 */
@Aspect
@Component
public class CheckArgumentAspect {

    @Pointcut("execution(public * com.xmopay.api.*.*(..))")
    public void requestArguments() {
    }

    @Before("requestArguments()")
    public void doBefore(JoinPoint joinPoint) {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 记录下请求内容
//        System.out.println("URL : " + request.getRequestURL().toString());
//        System.out.println("HTTP_METHOD : " + request.getMethod());
//        System.out.println("IP : " + request.getRemoteAddr());
//        System.out.println("CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
//        PayRequestModel tradeOrderRequest = (PayRequestModel) joinPoint.getArgs()[0];
//        System.out.println("channelCode: " + tradeOrderRequest.getChannelCode());
//        System.out.println("ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "ret", pointcut = "requestArguments()")
    public void doAfterReturning(Object ret) throws Throwable {
        // 处理完请求，返回内容
//        System.out.println("方法的返回值 : " + ret);
    }

    /**
     * 后置异常通知
     * @param jp
     */
    @AfterThrowing("requestArguments()")
    public void throwsCause(JoinPoint jp){
//        System.out.println("方法异常时执行.....");
    }

    /**
     * 后置最终通知,final增强，不管是抛出异常或者正常退出都会执行
      * @param jp
     */
    @After("requestArguments()")
//    public void after(JoinPoint jp){
//        System.out.println("方法最后执行.....");
//    }

    /**
     * 环绕通知,环绕增强,相当于MethodInterceptor
     * @param pjp
     * @return
     */
    @Around("requestArguments()")
    public Object around(ProceedingJoinPoint pjp) {
//        System.out.println("方法环绕start.....");
        try {
            Object o =  pjp.proceed();
//            System.out.println("方法环绕proceed，结果是 :" + o);
            return o;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }
}
