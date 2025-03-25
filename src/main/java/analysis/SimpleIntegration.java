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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import resource.CachedElementFinder;
import resource.ModelFactory;
import resource.ProjectResource;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by: zhang ran
 * 2024-04-07
 */
public class SimpleIntegration {
    private static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

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

        public void reset() {
            this.beanDefinitionCount = 0;
            this.annoPointerCount = 0;
            this.singleLinkedCount = 0;
            this.zeroLinkedCount = 0;
            this.multiLinkedCount = 0;
            this.beforeAop = 0;
            this.afterThrowingAop = 0;
            this.afterAop = 0;
            this.aroundAop = 0;
            this.afterReturningAop = 0;
            this.entryPointCount = 0;
            this.sourceCount = 0;
            this.sinkCount = 0;
            this.time = 0;
        }

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
    private static final Map<CtField<?>, Set<BeanDefinitionModel>> linkResult = new HashMap<>();
    private static final List<AspectTracker> beforeAopResult = new ArrayList<>();
    private static final List<AspectTracker> afterAopResult = new ArrayList<>();
    private static final List<AspectTracker> aroundAopResult = new ArrayList<>();
    private static final List<AspectTracker> afterReturningAopResult = new ArrayList<>();
    private static final List<AspectTracker> afterThrowingAopResult = new ArrayList<>();
    private static final List<String> entryPoints = new ArrayList<>();
    private static final List<String> sources = new ArrayList<>();
    private static final List<String> sinks = new ArrayList<>();

    private static String linkResultJson;
    private static String aopResultJson;
    private static String entryPointsJson;
    private static String sourcesJson;
    private static String sinksJson;
    private static String finalResultJson; // 存储最终的完整JSON结果

    static class AspectTracker {
        public CtMethod<?> adviceMethod;
        public List<CtMethod<?>> targetMethods = new ArrayList<>();

        public void clear() {
            this.adviceMethod = null;
            this.targetMethods.clear();
        }
    }

    public static void analysis(String projectPath, String outputDir) throws IOException {
        File dict = new File(outputDir);
        if(! dict.exists()) {
            dict.mkdirs();
        }
        stat.reset();
        linkResult.clear();
        linkResultJson = null;
        aopResultJson = null;
        entryPointsJson = null;
        sourcesJson = null;
        sinksJson = null;
        finalResultJson = null; // 重置最终JSON结果
        beforeAopResult.clear();
        afterAopResult.clear();
        aroundAopResult.clear();
        afterReturningAopResult.clear();
        afterThrowingAopResult.clear();
        entryPoints.clear();
        sources.clear();
        sinks.clear();

        long start = System.currentTimeMillis();
        ModelFactory.reset();
        ProjectResource.getResource(projectPath);
        iocLink();
        aop();
        entryPoint();
        sourceSink();
        long end = System.currentTimeMillis();
        // 添加统计信息
        stat.time = (end - start) / 1000d;

        // 将所有JSON结果组织成一个统一的JSON对象
        Map<String, Object> resultMap = new HashMap<>();

        // 解析各个JSON字符串为Map对象并添加到结果中
        if (linkResultJson != null) {
            resultMap.put("iocLinker", gson.fromJson(linkResultJson, Map.class));
            File file = new File(outputDir,"ioc.json");
            new FileWriter(file, false).close();
            writeOutput(file.toPath().toString(), linkResultJson);
        }

        if (aopResultJson != null) {
            resultMap.put("aop", gson.fromJson(aopResultJson, Map.class));
            File file = new File(outputDir,"aop.json");
            new FileWriter(file, false).close();
            writeOutput(file.toPath().toString(), aopResultJson);
        }

        if (entryPointsJson != null) {
            resultMap.put("entryPoints", gson.fromJson(entryPointsJson, Map.class));
            File file = new File(outputDir,"entryPoints.json");
            new FileWriter(file, false).close();
            writeOutput(file.toPath().toString(), entryPointsJson);
        }

        if (sourcesJson != null) {
            resultMap.put("sources", gson.fromJson(sourcesJson, Map.class));
            File file = new File(outputDir,"sources.json");
            new FileWriter(file, false).close();
            writeOutput(file.toPath().toString(), sourcesJson);
        }

        if (sinksJson != null) {
            resultMap.put("sinks", gson.fromJson(sinksJson, Map.class));
            File file = new File(outputDir,"sinks.json");
            new FileWriter(file, false).close();
            writeOutput(file.toPath().toString(), sinksJson);
        }


        Map<String, Object> statMap = new HashMap<>();
        statMap.put("beanDefinitionCount", stat.beanDefinitionCount);
        statMap.put("annoPointerCount", stat.annoPointerCount);
        statMap.put("singleLinkedCount", stat.singleLinkedCount);
        statMap.put("zeroLinkedCount", stat.zeroLinkedCount);
        statMap.put("multiLinkedCount", stat.multiLinkedCount);
        statMap.put("beforeAop", stat.beforeAop);
        statMap.put("afterAop", stat.afterAop);
        statMap.put("aroundAop", stat.aroundAop);
        statMap.put("afterReturningAop", stat.afterReturningAop);
        statMap.put("afterThrowingAop", stat.afterThrowingAop);
        statMap.put("entryPointCount", stat.entryPointCount);
        statMap.put("sourceCount", stat.sourceCount);
        statMap.put("sinkCount", stat.sinkCount);
        statMap.put("time", stat.time);
        resultMap.put("statistics", statMap);

        // 将整个结果转换为JSON并写入输出文件
        String finalJson = gson.toJson(resultMap);
        // 保存最终JSON结果
        finalResultJson = finalJson;

        File file = new File(outputDir,"all_model.json");
        // 清空文件内容
        new FileWriter(file, false).close();
        // 写入新内容
        writeOutput(file.toPath().toString(), finalJson);
        System.out.println(stat);
    }

    private static void iocLink() throws IOException {
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
            linkResult.put(e, bs);

        });

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
            linkResult.put(e, bs);

        });

        // 修改linkResult的输出格式
        Map<String, Object> structuredLinkResult = new HashMap<>();
        for (Map.Entry<CtField<?>, Set<BeanDefinitionModel>> entry : linkResult.entrySet()) {
            CtField<?> field = entry.getKey();
            Set<BeanDefinitionModel> beanDefs = entry.getValue();

            // 创建字段信息对象
            Map<String, Object> fieldInfo = formatFieldLinkJson(field, beanDefs);

            // 使用字段完整路径作为唯一标识
            String fieldKey = field.getDeclaringType().getQualifiedName() + "." + field.getSimpleName();
            structuredLinkResult.put(fieldKey, fieldInfo);
        }
        linkResultJson = gson.toJson(structuredLinkResult);
    }

    private static void aop() throws IOException {

        CachedElementFinder cachedElementFinder = CachedElementFinder.getInstance();
        var methods = cachedElementFinder.getCachedPublicMethod();

        var before = new BeforeAspectResolverBuilder<CtMethod<?>>();
        matchTarget(before, ProjectResource.beforeAnnoMethod, methods);


        var after = new AfterAspectResolverBuilder<CtMethod<?>>();
        matchTarget(after, ProjectResource.afterAnnoMethod, methods);


        var afterReturning = new AfterReturningAspectResolverBuilder<CtMethod<?>>();
        matchTarget(afterReturning, ProjectResource.afterReturningAnnoMethod, methods);


        var afterThrowing = new AfterThrowingAspectResolverBuilder<CtMethod<?>>();
        matchTarget(afterThrowing, ProjectResource.afterThrowingAnnoMethod, methods);

        var around = new AroundAspectResolverBuilder<CtMethod<?>>();
        matchTarget(around, ProjectResource.aroundAnnoMethod, methods);

        // 生成AOP结果的JSON格式
        Map<String, Object> allAopResults = formatAopResultJson();
        aopResultJson = gson.toJson(allAopResults);

    }

    private static void entryPoint() throws IOException {
//        writeOutput(outputPath, "\n\n --- Entry point ---\n\n");

        // 创建临时文件
        File tempFile = File.createTempFile("entryPoint", ".temp");
        String tempFilePath = tempFile.getAbsolutePath();

        // 设置DSL模板并执行
        String fileName = "dsl/entrypoint_template";
        String fileContent = readFileFromResources(fileName);
        String dsl = MessageFormat.format(fileContent, tempFilePath);
        interpretRaw(dsl);

        // 从临时文件读取结果并添加到entryPoints列表
        try (BufferedReader reader = new BufferedReader(new FileReader(tempFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    entryPoints.add(line.trim());
                }
            }
        }

        // 将entryPoints转换为JSON格式
        Map<String, Object> entryPointsMap = new HashMap<>();
        List<Map<String, Object>> entryPointsList = new ArrayList<>();

        for (String entryPoint : entryPoints) {
            Map<String, Object> entryPointInfo = new HashMap<>();
            entryPointInfo.put("signature", entryPoint);
            entryPointsList.add(entryPointInfo);
        }

        entryPointsMap.put("entryPoints", entryPointsList);
        entryPointsMap.put("count", entryPoints.size());
        entryPointsJson = gson.toJson(entryPointsMap);

        // 输出JSON到文件
//        writeOutput(outputPath, "\n\n --- Entry point JSON ---\n\n");
//        writeOutput(outputPath, entryPointsJson);

        // 删除临时文件
        if (tempFile.exists()) {
            tempFile.delete();
        }

        stat.entryPointCount = entryPoints.size();
    }

    private static void sourceSink() throws IOException {
//        writeOutput(outputPath, "\n\n --- Source sink ---\n\n");

        // 创建临时文件
        File tempFile = File.createTempFile("sourceSink", ".temp");
        String tempFilePath = tempFile.getAbsolutePath();

        // 设置DSL模板并执行
        String fileName = "dsl/sourcesink_template";
        String fileContent = readFileFromResources(fileName);
        String dsl = MessageFormat.format(fileContent, tempFilePath);
        interpretRaw(dsl);

        // 从临时文件读取结果并添加到sources和sinks列表
        boolean readingSources = false;
        boolean readingSinks = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("source---")) {
                    readingSources = true;
                    readingSinks = false;
                    continue;
                } else if (line.trim().equals("sink---")) {
                    readingSources = false;
                    readingSinks = true;
                    continue;
                }

                if (readingSources && !line.trim().isEmpty()) {
                    sources.add(line.trim());
                } else if (readingSinks && !line.trim().isEmpty()) {
                    sinks.add(line.trim());
                }
            }
        }

        // 将sources转换为JSON格式
        Map<String, Object> sourcesMap = new HashMap<>();
        List<Map<String, Object>> sourcesList = new ArrayList<>();

        for (String source : sources) {
            Map<String, Object> sourceInfo = new HashMap<>();
            sourceInfo.put("signature", source);
            sourcesList.add(sourceInfo);
        }

        sourcesMap.put("sources", sourcesList);
        sourcesMap.put("count", sources.size());
        sourcesJson = gson.toJson(sourcesMap);

        // 将sinks转换为JSON格式
        Map<String, Object> sinksMap = new HashMap<>();
        List<Map<String, Object>> sinksList = new ArrayList<>();

        for (String sink : sinks) {
            Map<String, Object> sinkInfo = new HashMap<>();
            sinkInfo.put("signature", sink);
            sinksList.add(sinkInfo);
        }

        sinksMap.put("sinks", sinksList);
        sinksMap.put("count", sinks.size());
        sinksJson = gson.toJson(sinksMap);

        // 输出JSON到文件
//        writeOutput(outputPath, "\n\n --- Sources ---\n\n");
//        writeOutput(outputPath, sourcesJson);
//        writeOutput(outputPath, "\n\n --- Sinks ---\n\n");
//        writeOutput(outputPath, sinksJson);

        // 删除临时文件
        if (tempFile.exists()) {
            tempFile.delete();
        }

        stat.sourceCount = sources.size();
        stat.sinkCount = sinks.size();
    }

    private static void matchTarget(AbstractPredictResolverBuilder<CtMethod<?>> builder,
                                      List<CtMethod<?>> aspectMethods,
                                      Set<CtMethod<?>> methods) {
        /**
         * around
         * public me.zhengjie.aspect.around(org.aspectj.lang.ProceedingJoinPoint) ; aspect targets:
         *       -> public me.zhengjie.modules.system.rest.testLimit()
         * public me.zhengjie.aspect.logAround(org.aspectj.lang.ProceedingJoinPoint) ; aspect targets:
         *       -> public me.zhengjie.rest.sendEmail(me.zhengjie.domain.vo.EmailVo)
         *       -> public me.zhengjie.modules.system.rest.updateRoleMenu(me.zhengjie.modules.system.domain.Role)
         *       -> public me.zhengjie.modules.mnt.rest.startServer(me.zhengjie.modules.mnt.domain.Deploy)
         *       -> public me.zhengjie.modules.mnt.rest.deleteDeploy(java.util.Set)
         */
        for (CtMethod<?> m : aspectMethods) {
            Predicate<CtMethod<?>> p = builder.build(null, m);
            AspectTracker tracker = new AspectTracker();
            tracker.adviceMethod = m;
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
                        tracker.targetMethods.add(method);
                    }
                }
            }
            if (builder.name().equals("before")) {
                beforeAopResult.add(tracker);
            } else if (builder.name().equals("after")) {
                afterAopResult.add(tracker);
            } else if (builder.name().equals("around")) {
                aroundAopResult.add(tracker);
            } else if (builder.name().equals("afterreturning")) {
                afterReturningAopResult.add(tracker);
            } else if (builder.name().equals("afterthrowing")) {
                afterThrowingAopResult.add(tracker);
            }
        }
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

    /**
     * 格式化字段关联信息为统一的JSON结构
     * @param field 字段
     * @param beanDefs 关联的bean定义
     * @return 格式化后的JSON对象
     */
    private static Map<String, Object> formatFieldLinkJson(CtField<?> field, Set<BeanDefinitionModel> beanDefs) {
        Map<String, Object> result = new HashMap<>();
        result.put("name", field.getSimpleName());
        result.put("declaringType", field.getDeclaringType().getQualifiedName());
        result.put("fieldType", field.getType().getQualifiedName());

        // 获取注解信息
        String annotation = "";
        if (!field.getAnnotations().isEmpty()) {
            annotation = field.getAnnotations().get(0).toString();
        }
        result.put("annotation", annotation);
        result.put("linkedBeans", beanDefs);

        return result;
    }

    /**
     * 格式化AOP结果为JSON结构
     * @return 格式化后的AOP结果JSON对象
     */
    private static Map<String, Object> formatAopResultJson() {
        Map<String, Object> result = new HashMap<>();

        // 添加Before切面
        List<Map<String, Object>> beforeAspects = new ArrayList<>();
        for (AspectTracker tracker : beforeAopResult) {
            Map<String, Object> aspectInfo = formatAspectTrackerJson(tracker, "before");
            beforeAspects.add(aspectInfo);
        }
        result.put("beforeAspects", beforeAspects);

        // 添加After切面
        List<Map<String, Object>> afterAspects = new ArrayList<>();
        for (AspectTracker tracker : afterAopResult) {
            Map<String, Object> aspectInfo = formatAspectTrackerJson(tracker, "after");
            afterAspects.add(aspectInfo);
        }
        result.put("afterAspects", afterAspects);

        // 添加Around切面
        List<Map<String, Object>> aroundAspects = new ArrayList<>();
        for (AspectTracker tracker : aroundAopResult) {
            Map<String, Object> aspectInfo = formatAspectTrackerJson(tracker, "around");
            aroundAspects.add(aspectInfo);
        }
        result.put("aroundAspects", aroundAspects);

        // 添加AfterReturning切面
        List<Map<String, Object>> afterReturningAspects = new ArrayList<>();
        for (AspectTracker tracker : afterReturningAopResult) {
            Map<String, Object> aspectInfo = formatAspectTrackerJson(tracker, "afterReturning");
            afterReturningAspects.add(aspectInfo);
        }
        result.put("afterReturningAspects", afterReturningAspects);

        // 添加AfterThrowing切面
        List<Map<String, Object>> afterThrowingAspects = new ArrayList<>();
        for (AspectTracker tracker : afterThrowingAopResult) {
            Map<String, Object> aspectInfo = formatAspectTrackerJson(tracker, "afterThrowing");
            afterThrowingAspects.add(aspectInfo);
        }
        result.put("afterThrowingAspects", afterThrowingAspects);

        // 添加统计信息
        Map<String, Integer> statistics = new HashMap<>();
        statistics.put("beforeCount", stat.beforeAop);
        statistics.put("afterCount", stat.afterAop);
        statistics.put("aroundCount", stat.aroundAop);
        statistics.put("afterReturningCount", stat.afterReturningAop);
        statistics.put("afterThrowingCount", stat.afterThrowingAop);
        result.put("statistics", statistics);

        return result;
    }

    /**
     * 格式化单个AspectTracker为JSON结构
     * @param tracker 要格式化的AspectTracker
     * @param aspectType 切面类型
     * @return 格式化后的JSON对象
     */
    private static Map<String, Object> formatAspectTrackerJson(AspectTracker tracker, String aspectType) {
        Map<String, Object> result = new HashMap<>();

        // 添加切面方法信息
        if (tracker.adviceMethod != null) {
            Map<String, Object> adviceMethodInfo = new HashMap<>();
            adviceMethodInfo.put("name", tracker.adviceMethod.getSimpleName());
            adviceMethodInfo.put("declaringType", tracker.adviceMethod.getDeclaringType().getQualifiedName());
            adviceMethodInfo.put("signature", tracker.adviceMethod.getSignature());
            adviceMethodInfo.put("type", aspectType);
            result.put("adviceMethod", adviceMethodInfo);

            // 添加目标方法列表
            List<Map<String, Object>> targetMethodsInfo = new ArrayList<>();
            for (CtMethod<?> targetMethod : tracker.targetMethods) {
                Map<String, Object> targetMethodInfo = new HashMap<>();
                targetMethodInfo.put("name", targetMethod.getSimpleName());
                targetMethodInfo.put("declaringType", targetMethod.getDeclaringType().getQualifiedName());
                targetMethodInfo.put("signature", targetMethod.getSignature());
                targetMethodsInfo.add(targetMethodInfo);
            }
            result.put("targetMethods", targetMethodsInfo);
        }

        return result;
    }

    public static String getLinkResultJson() {
        return linkResultJson;
    }

    public static String getAopResultJson() {
        return aopResultJson;
    }

    public static String getEntryPointsJson() {
        return entryPointsJson;
    }

    public static String getSourcesJson() {
        return sourcesJson;
    }

    public static String getSinksJson() {
        return sinksJson;
    }

    /**
     * 获取最终组合后的完整JSON结果
     * @return 完整的分析结果JSON字符串
     */
    public static String getFinalResultJson() {
        return finalResultJson;
    }

    /**
     * 统计指定文件中匹配模式后的行数
     * @param pattern 匹配模式
     * @param filePath 文件路径
     * @param skipOneLine 是否跳过一行
     * @param end 结束模式
     * @return 匹配行数
     * @deprecated 已被新的JSON格式化方法替代，保留此方法仅用于兼容
     */
    @Deprecated
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
