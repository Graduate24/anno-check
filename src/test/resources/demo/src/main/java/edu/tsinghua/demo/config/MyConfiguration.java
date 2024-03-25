package edu.tsinghua.demo.config;

import edu.tsinghua.demo.entity.MyBean;
import edu.tsinghua.demo.entity.MyBeanConsumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyConfiguration {

    @Bean
    public MyBean myBean() {
        return new MyBean();
    }

    @Bean
    public MyBeanConsumer myBeanConsumer() {
        return new MyBeanConsumer(myBean());
    }


}
