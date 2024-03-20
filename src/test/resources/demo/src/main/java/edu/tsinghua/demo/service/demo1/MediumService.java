package edu.tsinghua.demo.service.demo1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("medium")
public class MediumService implements Demo1Service {
    // This cause circular reference
//    @Autowired()
//    @Qualifier("medium")
//    private Demo1Service mediumService;
    @Override
    public String serviceOne() {
        return "Medium : service one";
    }

    @Override
    public String serviceTwo() {
        return "Medium : service two";
    }
}
