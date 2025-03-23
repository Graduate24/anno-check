package tool;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MyBatis XML SQL提取器
 * 用于提取MyBatis XML文件中的namespace和各种SQL ID，并拼接成完整方法路径
 */
public class MybatisSqlExtractor {
    private static final String[] SQL_TYPES = {"select", "insert", "update", "delete"};

    /**
     * 从单个XML文件中提取方法路径
     *
     * @param xmlFile MyBatis XML文件
     * @return 提取的方法路径列表
     */
    public static List<String> extractMethodPaths(File xmlFile) {
        List<String> methodPaths = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 禁用DTD验证
            factory.setValidating(false);
            // 禁用外部实体处理
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // 禁用XML Schema验证
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // 提取namespace
            Element mapperElement = document.getDocumentElement();
            String namespace = mapperElement.getAttribute("namespace");

            // 提取各种SQL类型的ID
            for (String sqlType : SQL_TYPES) {
                NodeList sqlNodes = document.getElementsByTagName(sqlType);
                for (int i = 0; i < sqlNodes.getLength(); i++) {
                    Element sqlElement = (Element) sqlNodes.item(i);
                    String id = sqlElement.getAttribute("id");
                    if (id != null && !id.isEmpty()) {
                        String methodPath = namespace + "." + id;
                        methodPaths.add(methodPath);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("提取" + xmlFile.getName() + "文件时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return methodPaths;
    }

    /**
     * 从目录中提取所有XML文件的方法路径
     *
     * @param directory 包含MyBatis XML文件的目录
     * @return 映射表: XML文件名 -> 方法路径列表
     */
    public static Map<String, List<String>> extractMethodPathsFromDirectory(File directory) {
        Map<String, List<String>> result = new HashMap<>();

        if (!directory.isDirectory()) {
            System.err.println(directory.getAbsolutePath() + "不是一个目录");
            return result;
        }

        File[] xmlFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
        if (xmlFiles == null || xmlFiles.length == 0) {
            System.out.println("目录" + directory.getAbsolutePath() + "中没有XML文件");
            return result;
        }

        for (File xmlFile : xmlFiles) {
            List<String> methodPaths = extractMethodPaths(xmlFile);
            result.put(xmlFile.getName(), methodPaths);
        }

        return result;
    }

    /**
     * 提取可能存在SQL注入风险的方法
     * 主要检测使用了${} 而不是 #{} 的方法
     *
     * @param xmlFile MyBatis XML文件
     * @return 可能存在SQL注入的方法路径列表
     */
    public static List<String> extractSqlInjectionRiskMethods(File xmlFile) {
        List<String> riskMethodPaths = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 禁用DTD验证
            factory.setValidating(false);
            // 禁用外部实体处理
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // 禁用XML Schema验证
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // 提取namespace
            Element mapperElement = document.getDocumentElement();
            String namespace = mapperElement.getAttribute("namespace");

            // 检查各种SQL类型
            for (String sqlType : SQL_TYPES) {
                NodeList sqlNodes = document.getElementsByTagName(sqlType);
                for (int i = 0; i < sqlNodes.getLength(); i++) {
                    Element sqlElement = (Element) sqlNodes.item(i);
                    String id = sqlElement.getAttribute("id");
                    if (id != null && !id.isEmpty()) {
                        String methodPath = namespace + "." + id;
                        // 获取SQL内容
                        String sqlContent = sqlElement.getTextContent();
                        // 检查是否存在${} 模式
                        if (sqlContent != null && sqlContent.contains("${")) {
                            riskMethodPaths.add(methodPath);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("分析" + xmlFile.getName() + "文件时出错: " + e.getMessage());
            e.printStackTrace();
        }

        return riskMethodPaths;
    }

    /**
     * 获取MyBatis XML文件的namespace（对应的Java接口全名称）
     *
     * @param xmlFile MyBatis XML文件
     * @return namespace字符串，如果解析出错则返回null
     */
    public static String getNamespace(File xmlFile) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            // 禁用DTD验证
            factory.setValidating(false);
            // 禁用外部实体处理
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            // 禁用XML Schema验证
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            // 提取namespace
            Element mapperElement = document.getDocumentElement();
            String namespace = mapperElement.getAttribute("namespace");
            
            return namespace; // 返回找到的namespace
        } catch (Exception e) {
            System.err.println("获取" + xmlFile.getName() + "的namespace时出错: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取目录中所有MyBatis XML文件的namespace
     *
     * @param directory 包含MyBatis XML文件的目录
     * @return 映射表: XML文件名 -> namespace
     */
    public static Map<String, String> getNamespacesFromDirectory(File directory) {
        Map<String, String> result = new HashMap<>();

        if (!directory.isDirectory()) {
            System.err.println(directory.getAbsolutePath() + "不是一个目录");
            return result;
        }

        File[] xmlFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
        if (xmlFiles == null || xmlFiles.length == 0) {
            System.out.println("目录" + directory.getAbsolutePath() + "中没有XML文件");
            return result;
        }

        for (File xmlFile : xmlFiles) {
            String namespace = getNamespace(xmlFile);
            if (namespace != null && !namespace.isEmpty()) {
                result.put(xmlFile.getName(), namespace);
            }
        }

        return result;
    }

    /**
     * 从目录中提取所有XML文件中可能存在SQL注入风险的方法
     *
     * @param directory 包含MyBatis XML文件的目录
     * @return 映射表: XML文件名 -> 可能存在SQL注入风险的方法路径列表
     */
    public static Map<String, List<String>> extractSqlInjectionRiskMethodsFromDirectory(File directory) {
        Map<String, List<String>> result = new HashMap<>();

        if (!directory.isDirectory()) {
            System.err.println(directory.getAbsolutePath() + "不是一个目录");
            return result;
        }

        File[] xmlFiles = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".xml"));
        if (xmlFiles == null || xmlFiles.length == 0) {
            System.out.println("目录" + directory.getAbsolutePath() + "中没有XML文件");
            return result;
        }

        for (File xmlFile : xmlFiles) {
            List<String> riskMethodPaths = extractSqlInjectionRiskMethods(xmlFile);
            if (!riskMethodPaths.isEmpty()) {
                result.put(xmlFile.getName(), riskMethodPaths);
            }
        }

        return result;
    }

    /**
     * 生成方法调用代码
     *
     * @param methodPath 方法路径
     * @return 生成的方法调用代码
     */
    public static String generateMethodCall(String methodPath) {
        int lastDotIndex = methodPath.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "// 无效的方法路径: " + methodPath;
        }

        String mapperVar = convertToVariableName(methodPath.substring(0, lastDotIndex));
        String methodName = methodPath.substring(lastDotIndex + 1);

        return mapperVar + "." + methodName + "();";
    }

    /**
     * 将类名转换为变量名
     *
     * @param className 类名
     * @return 变量名
     */
    private static String convertToVariableName(String className) {
        if (className == null || className.isEmpty()) {
            return "mapper";
        }

        int lastDotIndex = className.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return className.substring(0, 1).toLowerCase() + className.substring(1);
        }

        String simpleName = className.substring(lastDotIndex + 1);
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    /**
     * 主方法示例
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("请提供MyBatis XML文件或目录路径");
            return;
        }

        File input = new File(args[0]);
        if (input.isDirectory()) {
            // 获取所有XML文件的namespace
            Map<String, String> namespaces = getNamespacesFromDirectory(input);
            System.out.println("\n===== XML文件对应的Java接口 =====");
            for (Map.Entry<String, String> entry : namespaces.entrySet()) {
                System.out.println("文件: " + entry.getKey() + " -> 接口: " + entry.getValue());
            }
            
            // 提取所有方法路径
            Map<String, List<String>> result = extractMethodPathsFromDirectory(input);
            System.out.println("\n所有方法汇总结果:");
            for (Map.Entry<String, List<String>> entry : result.entrySet()) {
                System.out.println("\n文件: " + entry.getKey());
                for (String methodPath : entry.getValue()) {
                    System.out.println("  " + methodPath);
                    System.out.println("  示例调用: " + generateMethodCall(methodPath));
                }
            }
            
            // 提取可能存在SQL注入风险的方法
            Map<String, List<String>> riskResult = extractSqlInjectionRiskMethodsFromDirectory(input);
            if (!riskResult.isEmpty()) {
                System.out.println("\n\n===== 可能存在SQL注入风险的方法 =====");
                for (Map.Entry<String, List<String>> entry : riskResult.entrySet()) {
                    System.out.println("\n文件: " + entry.getKey());
                    for (String methodPath : entry.getValue()) {
                        System.out.println("  " + methodPath);
                        System.out.println("  存在风险的调用: " + generateMethodCall(methodPath));
                    }
                }
                System.out.println("\n提示: 这些方法使用了${} 而不是 #{} 进行参数替换，可能存在SQL注入风险");
                System.out.println("建议: 检查这些方法，将${} 替换为 #{} 或添加适当的输入验证和转义");
            } else {
                System.out.println("\n\n未发现存在SQL注入风险的方法，很好！");
            }
        } else if (input.isFile() && input.getName().toLowerCase().endsWith(".xml")) {
            // 获取单个文件的namespace
            String namespace = getNamespace(input);
            System.out.println("\n===== XML文件对应的Java接口 =====");
            System.out.println("文件: " + input.getName() + " -> 接口: " + namespace);
            
            // 提取单个文件的所有方法路径
            List<String> methodPaths = extractMethodPaths(input);
            System.out.println("\n文件: " + input.getName() + " 中提取的方法路径:");
            for (String methodPath : methodPaths) {
                System.out.println("  " + methodPath);
                System.out.println("  示例调用: " + generateMethodCall(methodPath));
            }
            
            // 提取单个文件中可能存在SQL注入风险的方法
            List<String> riskMethodPaths = extractSqlInjectionRiskMethods(input);
            if (!riskMethodPaths.isEmpty()) {
                System.out.println("\n\n===== 可能存在SQL注入风险的方法 =====");
                for (String methodPath : riskMethodPaths) {
                    System.out.println("  " + methodPath);
                    System.out.println("  存在风险的调用: " + generateMethodCall(methodPath));
                }
                System.out.println("\n提示: 这些方法使用了${} 而不是 #{} 进行参数替换，可能存在SQL注入风险");
                System.out.println("建议: 检查这些方法，将${} 替换为 #{} 或添加适当的输入验证和转义");
            } else {
                System.out.println("\n\n未发现存在SQL注入风险的方法，很好！");
            }
        } else {
            System.out.println("提供的路径不是有效的XML文件或目录");
        }
    }

}
