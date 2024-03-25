package edu.tsinghua.demo;

import edu.tsinghua.demo.controller.DemoController1;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private DemoController1 controller;


    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

}
