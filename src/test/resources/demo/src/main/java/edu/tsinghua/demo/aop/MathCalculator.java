package edu.tsinghua.demo.aop;

import org.springframework.stereotype.Component;

@Component
public class MathCalculator {
    public int add(int a, int b) {
        return a + b;
    }
}
