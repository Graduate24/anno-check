package edu.tsinghua.demo;

import edu.tsinghua.demo.service.demo1.Demo1Service;
import edu.tsinghua.demo.service.demo1.GoodService;
import edu.tsinghua.demo.service.demo1.MediumService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ServiceAutowiredTest {

    @Autowired
    @Qualifier("good")
    private Demo1Service service1;

    @Autowired
    @Qualifier("medium")
    private Demo1Service service2;

    @Autowired
    private Map<String, Demo1Service> serviceMap;

    @Autowired
    private List<Demo1Service> serviceList;

    @Test
    public void test1() {
        assertThat(service1.serviceOne()).contains("Good : service one");
    }

    @Test
    public void test2() {
        assertThat(service2.serviceOne()).contains("Medium : service one");
    }

    @Test
    public void test3() {
        assertThat(serviceMap.size()).isEqualTo(2);
        assertThat(serviceMap.get("good").getClass()).isEqualTo(GoodService.class);
        assertThat(serviceMap.get("medium").getClass()).isEqualTo(MediumService.class);
    }

    @Test
    public void test4() {
        assertThat(serviceMap.size()).isEqualTo(2);
        assertThat(serviceMap.get("good").getClass()).isEqualTo(GoodService.class);
        assertThat(serviceMap.get("medium").getClass()).isEqualTo(MediumService.class);
    }

    @Test
    public void test5() {
        assertThat(serviceList.size()).isEqualTo(2);
        assertThat(serviceList.stream().filter(s -> s.getClass().equals(GoodService.class))
                .findFirst().get().serviceOne()).isEqualTo("Good : service one");
        assertThat(serviceList.stream().filter(s -> s.getClass().equals(MediumService.class))
                .findFirst().get().serviceOne()).isEqualTo("Medium : service one");
    }

}
