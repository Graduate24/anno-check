package edu.tsinghua.demo.service.demo1;
import org.springframework.stereotype.Service;

@Service("good")
public class GoodService implements Demo1Service {
    @Override
    public String serviceOne() {
        return "Good : service one";
    }

    @Override
    public String serviceTwo() {
        return "Good : service two";
    }
}
