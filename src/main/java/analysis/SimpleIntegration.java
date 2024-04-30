package analysis;

import analysis.lang.interpreter.Interpreter;
import analysis.lang.parser.Parser;
import analysis.lang.parser.Scanner;
import analysis.lang.parser.Token;
import analysis.processor.aop.resolver.builder.*;
import analysis.processor.ioc.beanloader.*;
import analysis.processor.ioc.beanregistor.BeanRegister;
import analysis.processor.ioc.linker.SpringAutowiredAnnoFieldLinker;
import analysis.processor.ioc.linker.SpringValueAnnoFieldLinker;
import resource.CachedElementFinder;
import resource.ModelFactory;
import resource.ProjectResource;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by: zhang ran
 * 2024-04-07
 */
public class SimpleIntegration {

    static class Stat {
        public int beanDefinitionCount = 0;
        public int annoPointerCount = 0;
        public int singleLinkedCount = 0;
        public int zeroLinkedCount = 0;
        public int multiLinkedCount = 0;

        public int beforeAop = 0;
        public int afterAop = 0;
        public int aroundAop = 0;
        public int afterReturningAop = 0;
        public int afterThrowingAop = 0;

        public int entryPointCount = 0;
        public int sourceCount = 0;
        public int sinkCount = 0;

        public double time;

        @Override
        public String toString() {
            return "Stat{" +
                    "beanDefinitionCount=" + beanDefinitionCount +
                    ", annoPointerCount=" + annoPointerCount +
                    ", singleLinkedCount=" + singleLinkedCount +
                    ", zeroLinkedCount=" + zeroLinkedCount +
                    ", multiLinkedCount=" + multiLinkedCount +
                    ", beforeAop=" + beforeAop +
                    ", afterAop=" + afterAop +
                    ", aroundAop=" + aroundAop +
                    ", afterReturningAop=" + afterReturningAop +
                    ", afterThrowingAop=" + afterThrowingAop +
                    ", entryPointCount=" + entryPointCount +
                    ", sourceCount=" + sourceCount +
                    ", sinkCount=" + sinkCount +
                    ", time=" + time +
                    '}';
        }
    }

    private static final Stat stat = new Stat();

    public static void analysis(String projectPath, String outputPath) throws IOException {
        long start = System.currentTimeMillis();
        ModelFactory.reset();
        ProjectResource.getResource(projectPath);
        iocLink(outputPath);
        aop(outputPath);
        entryPoint(outputPath);
        sourceSink(outputPath);
        long end = System.currentTimeMillis();
        stat.time = (end - start) / 1000d;
        writeOutput(outputPath, stat.toString());
    }

    private static void iocLink(String outputPath) throws IOException {
        writeOutput(outputPath, "\n\n --- IoC Container ---\n\n");
        var list = new ArrayList<BeanDefinitionModel>();

        var com = new SpringComponentAnnoBeanLoader();
        ProjectResource.springComponentAnnoClass.forEach(e -> {
            var bd = com.load(null, e);
            list.add(bd);
        });
        var ser = new SpringServiceAnnoBeanLoader();
        ProjectResource.springServiceAnnoClass.forEach(e -> {
            var bd = ser.load(null, e);
            list.add(bd);
        });
        var con = new SpringControllerAnnoBeanLoader();
        ProjectResource.springControllerClass.forEach(e -> {
            var bd = con.load(null, e);
            list.add(bd);
        });
        var be = new SpringBeanAnnoBeanLoader();
        ProjectResource.springBeanAnnoMethod.forEach(e -> {
            var bd = be.load(null, e);
            list.add(bd);
        });
        var re = new SpringRepositoryAnnoBeanLoader();
        ProjectResource.springRepositoryAnnoInterface.forEach(e -> {
            var bd = re.load(null, e);
            list.add(bd);
        });
        var my = new MybatisMapperBeanLoader();
        ProjectResource.mybatisMapperInterface.forEach(e -> {
            var bd = my.load(null, e);
            list.add(bd);
        });
        var mo = new SpringMongoRepoBeanLoader();
        ProjectResource.springMongoInterface.forEach(e -> {
            var bd = mo.load(null, e);
            list.add(bd);
        });

        var cp = new SpringConfigurationPropertiesBeanLoader();
        ProjectResource.springConfigurationPropertiesClass.forEach(e -> {
            var bd = cp.load(null, e);
            list.add(bd);
        });

        var c = new SpringConfigurationBeanLoader();
        ProjectResource.springConfigurationClass.forEach(e -> {
            var bd = c.load(null, e);
            list.add(bd);
        });

        stat.beanDefinitionCount = list.size();


//        var v = new SpringValueAnnoBeanLoader();
//        ProjectResource.springValueAnnoField.forEach(e -> {
//            var bd = v.load(null, e);
//            list.add(bd);
//        });

        // load bean and register bean
        list.forEach(BeanRegister::register);

        // collect @Autowired fields
        var linker = new SpringAutowiredAnnoFieldLinker();
        StringBuilder sb = new StringBuilder();
        sb.append("---------@Autowired-----------\n\n");
        ProjectResource.springAutowiredAnnoField.forEach(e -> {
            stat.annoPointerCount++;
            linker.link(e);
            var bs = linker.findLink(e);
            if (bs.isEmpty()) {
                stat.zeroLinkedCount++;
            } else if (bs.size() == 1) {
                stat.singleLinkedCount++;
            } else {
                stat.multiLinkedCount++;
            }

            sb.append(e.getDeclaringType().getQualifiedName()).append("#").append(e.getSimpleName())
                    .append("\n  -->  ").append(bs.size()).append("| ").append(bs).append("\n");
        });
        sb.append("\n\n---------@Value-----------\n\n");
        var linker2 = new SpringValueAnnoFieldLinker();
        // link
        ProjectResource.springValueAnnoField.forEach(e -> {
            stat.annoPointerCount++;
            linker2.link(e);
            var bs = linker2.findLink(e);
            if (bs.isEmpty()) {
                stat.zeroLinkedCount++;
            } else if (bs.size() == 1) {
                stat.singleLinkedCount++;
            } else {
                stat.multiLinkedCount++;
            }
            sb.append(e.getDeclaringType().getQualifiedName()).append("#").append(e.getSimpleName())
                    .append("\n  -->  ").append(bs.size()).append("| ").append(bs).append("\n");
            ;
        });
        if (outputPath.equals("stdout")) {
            System.out.println(sb);
        } else {
            writeOutput(outputPath, sb.toString());
        }
    }

    private static void aop(String outputPath) throws IOException {
        writeOutput(outputPath, "\n\n --- AOP ---\n\n");
        CachedElementFinder cachedElementFinder = CachedElementFinder.getInstance();
        var methods = cachedElementFinder.getCachedPublicMethod();

        var before = new BeforeAspectResolverBuilder<CtMethod<?>>();
        String content = matchTarget(before, ProjectResource.beforeAnnoMethod, methods);
        if (outputPath.equals("stdout")) {
            System.out.println(content);
        } else {
            writeOutput(outputPath, content);
        }


        var after = new AfterAspectResolverBuilder<CtMethod<?>>();
        content = matchTarget(after, ProjectResource.afterAnnoMethod, methods);
        if (outputPath.equals("stdout")) {
            System.out.println(content);
        } else {
            writeOutput(outputPath, content);
        }

        var afterReturning = new AfterReturningAspectResolverBuilder<CtMethod<?>>();
        content = matchTarget(afterReturning, ProjectResource.afterReturningAnnoMethod, methods);
        if (outputPath.equals("stdout")) {
            System.out.println(content);
        } else {
            writeOutput(outputPath, content);
        }

        var afterThrowing = new AfterThrowingAspectResolverBuilder<CtMethod<?>>();
        content = matchTarget(afterThrowing, ProjectResource.afterThrowingAnnoMethod, methods);
        if (outputPath.equals("stdout")) {
            System.out.println(content);
        } else {
            writeOutput(outputPath, content);
        }

        var around = new AroundAspectResolverBuilder<CtMethod<?>>();
        content = matchTarget(around, ProjectResource.aroundAnnoMethod, methods);
        if (outputPath.equals("stdout")) {
            System.out.println(content);
        } else {
            writeOutput(outputPath, content);
        }
    }

    private static void entryPoint(String outputPath) throws IOException {
        writeOutput(outputPath, "\n\n --- Entry point ---\n\n");
        String fileName = "dsl/entrypoint_template";
        String fileContent = readFileFromResources(fileName);
        // Replace the placeholder {0}
        String dsl = MessageFormat.format(fileContent, outputPath);
        interpretRaw(dsl);

        int count = countLinesAfterEntry("--- Entry point ---", outputPath, true, null);
        stat.entryPointCount = count;
    }

    private static void sourceSink(String outputPath) throws IOException {
        writeOutput(outputPath, "\n\n --- Source sink ---\n\n");

        String fileName = "dsl/sourcesink_template";
        String fileContent = readFileFromResources(fileName);
        // Replace the placeholder {0}
        String dsl = MessageFormat.format(fileContent, outputPath);
        interpretRaw(dsl);
        int count = countLinesAfterEntry("source---", outputPath, false, "sink---");
        stat.sourceCount = count;
        int sinkCount = countLinesAfterEntry("sink---", outputPath, false, null);
        stat.sinkCount = sinkCount;
    }

    private static String matchTarget(AbstractPredictResolverBuilder<CtMethod<?>> builder,
                                      List<CtMethod<?>> aspectMethods,
                                      Set<CtMethod<?>> methods) {
        StringBuilder sb = new StringBuilder();
        sb.append(builder.name()).append("\n");
        for (CtMethod<?> m : aspectMethods) {
            Predicate<CtMethod<?>> p = builder.build(null, m);
            sb.append(printMethod(m)).append(" ; aspect targets: \n");
            if (p != null) {
                for (CtMethod<?> method : methods) {
                    if (p.test(method)) {
                        if (builder.name().equals("before")) {
                            stat.beforeAop++;
                        } else if (builder.name().equals("after")) {
                            stat.afterAop++;
                        } else if (builder.name().equals("around")) {
                            stat.aroundAop++;
                        } else if (builder.name().equals("afterreturning")) {
                            stat.afterReturningAop++;
                        } else if (builder.name().equals("afterthrowing")) {
                            stat.afterThrowingAop++;
                        }
                        sb.append("      -> ").append(printMethod(method)).append("\n");
                    }
                }
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    private static void writeOutput(String filePath, String content) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (parentDir.mkdirs()) {
                System.out.println("Created the directory structure: " + parentDir);
            } else {
                throw new RuntimeException("Failed to create the directory structure: " + parentDir);
            }
        }
        FileWriter writer = new FileWriter(filePath, true);
        writer.write(content);
        writer.close();
    }

    private static String printMethod(CtMethod<?> m) {
        StringBuilder sb = new StringBuilder();
        var modifiers = m.getModifiers();
        if (modifiers.contains(ModifierKind.PUBLIC)) {
            sb.append("public ");
        } else if (modifiers.contains(ModifierKind.PRIVATE)) {
            sb.append("private ");
        } else if (modifiers.contains(ModifierKind.PROTECTED)) {
            sb.append("protected ");
        }
        if (modifiers.contains(ModifierKind.ABSTRACT)) {
            sb.append("abstract ");
        }
        if (modifiers.contains(ModifierKind.STATIC)) {
            sb.append("static ");
        }
        if (modifiers.contains(ModifierKind.FINAL)) {
            sb.append("final ");
        }
        if (modifiers.contains(ModifierKind.NATIVE)) {
            sb.append("native ");
        }
        sb.append(m.getDeclaringType().getPackage().toString()).append(".").append(m.getSignature());
        return sb.toString();
    }

    private static String readFileFromResources(String fileName) {
        try (InputStream is = SimpleIntegration.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean interpretRaw(String dsl) {
        Scanner scanner = new Scanner(dsl);
        if (scanner.isHasError()) {
            return false;
        }
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        var stmts = parser.parse();
        if (parser.isHasError()) {
            return false;
        }
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(stmts);
        return !interpreter.hadRuntimeError;
    }

    public static int countLinesAfterEntry(String pattern, String filePath, boolean skipOneLine, String end) {
        boolean startCounting = false;
        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (pattern.equals(line.trim())) {
                    startCounting = true;
                    if (skipOneLine)
                        reader.readLine();
                    continue;
                }
                if (startCounting) {
                    if (line.trim().isEmpty() || (end != null && end.equals(line.trim()))) {
                        break;
                    }
                    lineCount++;
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }

        return lineCount;
    }
}
