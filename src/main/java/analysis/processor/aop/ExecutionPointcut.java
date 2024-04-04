package analysis.processor.aop;

import java.util.List;

public class ExecutionPointcut {
    /**
     * execution(modifiers-pattern?
     * * ret-type-pattern
     * * declaring-type-pattern?name-pattern(param-pattern)
     * * throws-pattern?)
     */

    private String modifier;
    private String retType;
    private String declaringTypeNamePattern;
    private String paramPattern;

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getRetType() {
        return retType;
    }

    public void setRetType(String retType) {
        this.retType = retType;
    }

    public String getDeclaringTypeNamePattern() {
        return declaringTypeNamePattern;
    }

    public void setDeclaringTypeNamePattern(String declaringTypeNamePattern) {
        this.declaringTypeNamePattern = declaringTypeNamePattern;
    }

    public String getParamPattern() {
        return paramPattern;
    }

    public void setParamPattern(String paramPattern) {
        this.paramPattern = paramPattern;
    }

    @Override
    public String toString() {
        return "ExecutionPointcut{" +
                "modifier='" + modifier + '\'' +
                ", retType='" + retType + '\'' +
                ", declaringTypeNamePattern='" + declaringTypeNamePattern + '\'' +
                ", paramPattern='" + paramPattern + '\'' +
                '}';
    }
}
