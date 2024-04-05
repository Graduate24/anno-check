import analysis.processor.aop.ExecutionPointcut;
import analysis.processor.aop.parser.Expr;
import analysis.processor.aop.parser.Parser;
import analysis.processor.aop.parser.Scanner;
import analysis.processor.aop.parser.Token;
import analysis.processor.aop.resolver.PredictorResolver;
import analysis.processor.aop.resolver.builder.CachedPredictPointcutResolverBuilder;
import io.github.azagniotov.matcher.AntPathMatcher;
import org.junit.Test;
import resource.CachedElementFinder;
import spoon.reflect.declaration.CtMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static resource.ElementUtil.getValueOfAnnotationAsString;

/**
 * Created by: zhang ran
 * 2024-04-04
 */
public class TestAop extends BaseTest {


    /**
     * execution(modifiers-pattern?
     * ret-type-pattern
     * declaring-type-pattern?name-pattern(param-pattern)
     * throws-pattern?)
     * <p>
     * All parts except the returning type pattern (ret-type-pattern in the preceding snippet), the name pattern,
     * and the parameters pattern are optional. The returning type pattern determines what the return type of the
     * method must be in order for a join point to be matched. * is most frequently used as the returning type
     * pattern. It matches any return type. A fully-qualified type name matches only when the method returns the
     * given type. The name pattern matches the method name. You can use the * wildcard as all or part of a name
     * pattern. If you specify a declaring type pattern, include a trailing . to join it to the name pattern
     * component. The parameters pattern is slightly more complex: () matches a method that takes no parameters,
     * whereas (..) matches any number (zero or more) of parameters. The (*) pattern matches a method that takes
     * one parameter of any type. (*,String) matches a method that takes two parameters. The first can be of any
     * type, while the second must be a String. Consult the Language Semantics section of the AspectJ Programming
     * Guide for more information.
     * <p>
     * The following examples show some common pointcut expressions:
     * The execution of any public method:
     * execution(public * *(..))
     * <p>
     * The execution of any method with a name that begins with set:
     * execution(* set*(..))
     * <p>
     * The execution of any method defined by the AccountService interface:
     * execution(* com.xyz.service.AccountService.*(..))
     * <p>
     * The execution of any method defined in the service package:
     * execution(* com.xyz.service.*.*(..))
     * <p>
     * The execution of any method defined in the service package or one of its sub-packages:
     * execution(* com.xyz.service..*.*(..))
     */
    @Test
    public void test1() {
        String[] expression = {
                "execution( public * *(..)  )",
                "execution(* set*(..))",
                "execution(* com.xyz.service.AccountService.*(..))",
                "execution(* com.xyz.service.*.*(..))",
                "execution(* com.xyz.service..*.*(..))",
                "execution(public void edu.tsinghua.demo.aop.ShipmentService.outerCheck())",
                "execution(public * edu.tsinghua.demo.aop.BillService.*(..))",
                "execution(public String edu.tsinghua.demo.aop.OrderService.*(..))",
                "execution(public * edu.tsinghua.demo.aop.MathCalculator.add(String, Object))"
        };
        // Regular expression pattern
        String patternString = "execution\\s*\\((.*?\\))\\s*";

        // Compile the regular expression pattern
        Pattern pattern = Pattern.compile(patternString);


        String paramPatternRegex = "\\((.*?)\\)";
        Pattern paramPattern = Pattern.compile(paramPatternRegex);

        for (String s : expression) {
            System.out.println(s);
            // Create a matcher for the input string
            Matcher matcher = pattern.matcher(s);
            // Check if the pattern matches
            if (!matcher.find()) {
                // If found, group(1) contains the matched substring
                continue;
            }
            ExecutionPointcut executionPointcut = getPatternBefore(matcher.group(1));
            System.out.println(executionPointcut);
            System.out.println();
        }
    }

    private ExecutionPointcut getPatternBefore(String expression) {
        expression = expression.trim();
        String paramPatternRegex = "\\((.*?)\\)";
        Pattern paramPattern = Pattern.compile(paramPatternRegex);
        Matcher pm = paramPattern.matcher(expression);
        if (!pm.find()) return null;
        String param = pm.group(1);

        // Find the index of the opening parenthesis of the method parameters
        // Trim any whitespace right before the pattern
        int startIndex = expression.indexOf("(");

        while (startIndex > 0 && Character.isWhitespace(expression.charAt(startIndex - 1))) {
            startIndex--;
        }
        if (startIndex == 0) return null;
        // Move backwards until the first whitespace is found to capture the pattern
        int endIndex = startIndex;
        while (endIndex > 0 && !Character.isWhitespace(expression.charAt(endIndex - 1))) {
            endIndex--;
        }
        if (endIndex == 0 || endIndex == startIndex) return null;
        String declaringNamePattern = expression.substring(endIndex, startIndex).trim();
        String restPrefix = expression.substring(0, endIndex);
        String[] arr = restPrefix.split(" ");
        String retType = null;
        String modifier = null;
        if (arr.length == 1) {
            retType = arr[0];
        } else if (arr.length == 2) {
            modifier = arr[0];
            retType = arr[1];
        } else {
            return null;
        }
        ExecutionPointcut pointcut = new ExecutionPointcut();
        pointcut.setModifier(modifier);
        pointcut.setRetType(retType);
        pointcut.setDeclaringTypeNamePattern(declaringNamePattern);
        pointcut.setParamPattern(param);
        return pointcut;
    }

    @Test
    public void test2() {
        String[] patterns = {
                "*",
                "set*",
                "com.xyz.service.AccountService.*",
                "com.xyz.service.*.*",
                "com.xyz.service..*.*"
        };

        for (String pattern : patterns) {
            if (!pattern.contains(".")) {
                // No declaring type is specified
                System.out.println("Pattern: " + pattern + " => Declaring Type: [none], Method Name: " + pattern);
            } else {
                // Split at the last period
                int lastDotIndex = pattern.lastIndexOf('.');
                String declaringType = pattern.substring(0, lastDotIndex);
                String methodName = pattern.substring(lastDotIndex + 1);

                // Adjust for special cases like "..*.*"
                if (methodName.startsWith("..")) {
                    declaringType += methodName.substring(0, 2);
                    methodName = methodName.substring(3); // Skip past "..*"
                }

                System.out.println("Pattern: " + pattern + " => Declaring Type: " + declaringType + ", Method Name: " + methodName);
            }
        }
    }

    @Test
    public void test3() {
        String[] patterns = {
                "com.xyz.service..*",
                "com.xyz.service.*",
                "com..*.service.*"
        };
        for (String pattern : patterns) {
            String regex = pattern
                    .replaceAll("\\.\\.\\*", "/**")
                    .replaceAll("\\.", "/");

            System.out.println("Pattern: " + pattern + "  Regular expression: " + regex);
        }
    }

    @Test
    public void test4() {
        AntPathMatcher antPathMatcher = new AntPathMatcher.Builder().build();
        System.out.println(antPathMatcher.isMatch("com/xyz/**/*", "com/xyz/a/f/g/cded"));
        System.out.println(antPathMatcher.isMatch("set*", "setId"));
    }

    @Test
    public void test5() {
        String[] patterns = {
                "@annotation(auditable.asdf) and within(com.xyz.service..*.*)",
                "@annotation(auditable.asdf) || within(com.xyz.service..*.*)",
                "edu.tsinghua.demo.aop.ShipmentService.outerCheck()",
                "execution( public * *(..)) || execution(* set*(..))",
                "execution(* set*(..))",
                "execution(* com.xyz.service.AccountService.*(..))",
                "execution(* com.xyz.service.*.*(..))",
                "execution(* com.xyz.service..*.*(..))",
                "execution(* com.xyz.service..*.*Set*Id*(..))",
                "execution(public void edu.tsinghua.demo.aop.ShipmentService.outerCheck())",
                "execution(public * edu.tsinghua.demo.aop.BillService.*(..))",
                "execution(public String edu.tsinghua.demo.aop.OrderService.*(..))",
                "execution(public * edu.tsinghua.demo.aop.MathCalculator.add(String, Object))",
                "execution(* com.xyz.service..*.*(java.lang.String, int)) && execution(public void " +
                        "edu.tsinghua.demo.aop.ShipmentService.outerCheck()) " +
                        "||(execution(* set*(..))&& (!execution( public * *(..)  )) )"
        };

        for (String pattern : patterns) {
            System.out.println(pattern);
            Scanner scanner = new Scanner(pattern);
            List<Token> tokens = scanner.scanTokens();
            Parser parser = new Parser(tokens);
            Expr expr = parser.parse();
            assert !parser.isHasError();
            System.out.print(expr.getClass().getSimpleName() + " :");
            System.out.println(expr);
            System.out.println();
        }
    }

    @Test
    public void test6() {
        getResource("src/test/resources/demo/");
        var builder = new CachedPredictPointcutResolverBuilder<CtMethod<?>>();
        List<Predicate<CtMethod<?>>> predictors = new ArrayList<>();
        pointcutMethod.forEach(m -> {
            var p = builder.build(null, m);
            if (p != null) predictors.add(p);
        });

        CachedElementFinder cachedElementFinder = CachedElementFinder.getInstance();

        var methods = cachedElementFinder.getCachedPublicMethod();
        for (CtMethod<?> method : methods) {
            for (Predicate<CtMethod<?>> predictor : predictors) {
                if (predictor.test(method)) {
                    System.out.println(method.getDeclaringType().getQualifiedName() + "." + method.getSignature());
                }
            }
        }

    }

    @Test
    public void test7() {
        String project = "D:\\edgedownload\\mall-master";
//        String project = "src/test/resources/demo/";
        getResource(project);
        CachedElementFinder cachedElementFinder = CachedElementFinder.getInstance();
        var methods = cachedElementFinder.getCachedPublicMethod();
        for (CtMethod<?> m : pointcutMethod) {
            System.out.println("-- method: " + m.getSimpleName());
            String pointcut = getValueOfAnnotationAsString(m, "org.aspectj.lang.annotation.Pointcut");
            System.out.print("pointcut: " + pointcut);
            Scanner scanner = new Scanner(pointcut);
            List<Token> tokens = scanner.scanTokens();
            if (scanner.isHasError()) {
                System.out.println("scan error");
                continue;
            }
            Parser parser = new Parser(tokens);
            Expr expr = parser.parse();
            if (parser.isHasError()) {
                System.out.println("parse error");
                continue;
            }

            String basePackage = m.getDeclaringType().getPackage().toString();
            String declaringClass = m.getDeclaringType().getSimpleName();
            System.out.print("; defined package: " + basePackage);
            System.out.print("; class: " + declaringClass);
            PredictorResolver<CtMethod<?>> resolver = new PredictorResolver<>(expr,
                    basePackage,
                    declaringClass);

            resolver.setSource(pointcut);

            var predictor = resolver.resolvePredictor();
            if (predictor == null) {
                System.out.println("resolve error");
                continue;
            }
            for (CtMethod<?> method : methods) {
                if (predictor.test(method)) {
                    System.out.println("\n    find target method: " + method.getDeclaringType().getQualifiedName() + "." + method.getSignature());
                }
            }
            System.out.println();
        }
    }

}
