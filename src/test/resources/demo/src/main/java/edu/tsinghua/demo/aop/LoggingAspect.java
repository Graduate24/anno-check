package edu.tsinghua.demo.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import java.util.ArrayList;
import java.util.List;

@Component("xxx")
@Aspect
public class LoggingAspect {

    @Pointcut("@annotation(Log)")
    public void logPointcut() {
    }

    @Pointcut("execution(public void edu.tsinghua.demo.aop.ShipmentService.shipStuffWithBill())")
    public void logPointcutWithExecution() {
    }

    @Pointcut("execution(public void edu.tsinghua.demo.aop.ShipmentService.innerCheck())")
    public void innerCheck() {
    }

    @Pointcut("execution(public void edu.tsinghua.demo.aop.ShipmentService.outerCheck())")
    public void outerCheck() {
    }

    @Pointcut("execution(public * edu.tsinghua.demo.aop.BillService.*(..))")
    public void logPointCutWithWildcard() {
    }

    @Pointcut("within(edu.tsinghua.demo.aop.OrderService) && execution(public String edu.tsinghua.demo.aop.OrderService.*(..))")
    public void logPointcutWithLogicalOperator() {
    }

    @Pointcut("execution(public * edu.tsinghua.demo.aop.MathCalculator.add(..))")
    public void mathAdd() {
    }

    @Before("logPointcut()")
    public void logAllMethodCallsAdvice1(JoinPoint joinPoint) {
        System.out.println(Arrays.toString(joinPoint.getArgs()));
        System.out.println("@Log in Aspect");
    }

    @Before("logPointcutWithExecution()")
    public void logMethodCallsWithExecutionAdvice() {
        System.out.println("In Aspect from execution");
    }

    @Before("logPointcutWithLogicalOperator()")
    public void logPointcutWithLogicalOperatorAdvice() {
        System.out.println("In Aspect from logical operator");
    }

    @Before("innerCheck()")
    public void innerCheckAdvice() {
        // this won't work because calls within the target object are not intercepted
        // https://docs.spring.io/spring-framework/reference/core/aop/ataspectj/pointcuts.html
        System.out.println("In Aspect from inner check");
    }

    @Before("outerCheck()")
    public void outerCheckAdvice() {
        System.out.println("In Aspect from outer check");
    }

    @Around("logPointCutWithWildcard()")
    public Object logAllMethodCallsAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        //before point joint
        System.out.println("In Aspect at " + joinPoint.getSignature().getName());
        System.out.println("==");
        Object[] newArgus = new Object[joinPoint.getArgs().length];
        for (int i = 0; i < joinPoint.getArgs().length; i++) {
            Object o = joinPoint.getArgs()[i];
            System.out.println("before arg: " + o);
            if (o instanceof String) {
                newArgus[i] = "nwe_" + o;
                System.out.println("change to " + newArgus[i]);
            }
            if (o instanceof Double) {
                newArgus[i] = ((Double) o) + 100;
                System.out.println("change to " + newArgus[i]);
            }
        }
        // execute point joint
        System.out.print("execute point joint: ");
        Object ret = joinPoint.proceed(newArgus);
        System.out.println("==");
        System.out.println("return value: " + ret);
        //after point joint
        ret = "new result";
        System.out.println("change return value:" + ret);

        return ret;
    }

    @Around("mathAdd()")
    public Object modifyAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        // Intercept the method call
        Object[] args = joinPoint.getArgs();
        int modifiedArg1 = (int) args[0] + 1;
        int modifiedArg2 = (int) args[1] + 1;
        System.out.println("original arg0:" + args[0] + "; original arg1:" + args[1]);
        System.out.println("modify arg1 to: " + modifiedArg1 + "; arg1 to: " + modifiedArg2);

        // Call the original method with modified arguments
        Object result = joinPoint.proceed(new Object[]{modifiedArg1, modifiedArg2});

        // Modify the return value
        int modifiedResult = (int) result * 2;
        System.out.println("original result:" + result + "; modified result:(a + b)*2:" + modifiedResult);
        return modifiedResult;
    }
}
