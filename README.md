# Anno-Check: 基于注解的框架项目建模工具

Anno-Check是一个专门用于对包含框架和注解的项目进行建模的工具。它实现了一个领域特定语言(DSL)及其解释器，可以对项目进行多种建模操作，为后续的语义还原和静态分析工具提供支持。

## 功能特性

- **依赖注入建模**：分析Spring等框架的依赖注入关系
- **AOP建模**：识别和建模面向切面编程的各个切面（Before、After、Around、AfterReturning、AfterThrowing）
- **入口点建模**：识别和建模项目的入口点方法
- **Source/Sink建模**：识别和建模数据流中的源点和汇点
- **DSL支持**：提供领域特定语言，支持自定义建模规则
- **JSON输出**：生成结构化的JSON格式建模结果

## 建模结果示例

建模结果将以JSON格式保存在`./model`目录下，包含以下内容：

```json
{
  "iocContainer": {
    // IoC容器分析结果，包含Bean定义和依赖关系
  },
  "aop": {
    "beforeAspects": [...],
    "afterAspects": [...],
    "aroundAspects": [...],
    "afterReturningAspects": [...],
    "afterThrowingAspects": [...],
    "statistics": {...}
  },
  "entryPoints": {
    "entryPoints": [...],
    "count": 数量
  },
  "sources": {
    "sources": [...],
    "count": 数量
  },
  "sinks": {
    "sinks": [...],
    "count": 数量
  },
  "statistics": {
    // 各种统计信息
  }
}
```

## 使用方法

1. 编译项目：
```bash
mvn clean package
```

2. 运行工具：
```bash
java -jar target/anno-model-1.0.jar -p <项目路径> -o <输出路径>
```

参数说明：
- `-p`：指定要分析的项目路径
- `-o`：指定输出结果的路径

## 技术栈

- Java 17
- Spoon：用于Java代码分析和转换
- GSON：用于JSON处理
- Maven：项目构建工具

## 依赖项

主要依赖包括：
- Spoon Core 10.4.2
- GSON 2.10.1
- SLF4J 2.0.12
- SnakeYAML 2.2
- 其他工具库

## 应用场景

- 框架项目的静态分析
- 代码依赖关系分析
- 安全漏洞检测
- 程序行为分析
- 代码重构支持

## 注意事项

- 确保目标项目使用Java 17或更低版本
- 项目需要包含完整的源代码
- 建议在分析大型项目时预留足够的处理时间

## 许可证

[添加许可证信息]

## 贡献指南

欢迎提交Issue和Pull Request来帮助改进这个项目。