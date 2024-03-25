package edu.tsinghua.demo.controller;

import edu.tsinghua.demo.dao.entity.User;
import edu.tsinghua.demo.dao.repo.UserRepository;
import edu.tsinghua.demo.service.demo1.Demo1Service;
import edu.tsinghua.demo.service.demo1.GoodService;
import edu.tsinghua.demo.service.demo1.MediumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DemoController1 {

    @Autowired
    @Qualifier("good")
    private Demo1Service service;

    private String field1 = "default";

    @Autowired
    private Demo1Service medium;

    private Demo1Service serviceNew = new GoodService();

    private String field2 = serviceNew.serviceOne();

    @Autowired
    private Map<String, Demo1Service> serviceMap;

    @Autowired
    private UserRepository userRepository;

    private Map<String, Demo1Service> serviceMapNew = new HashMap<>();

    {
        serviceMapNew.put("good", new GoodService());
        serviceMapNew.put("medium", new MediumService());
    }

    @GetMapping("/test1")
    public String test1() {
        return service.serviceOne();
    }

    @GetMapping("/test2")
    public String test2() {
        return serviceNew.serviceOne();
    }

    @GetMapping("/test3")
    public String test3() {
        Demo1Service service3 = new GoodService();
        return service3.serviceTwo();
    }

    @GetMapping("/test6")
    public String test6() {
        return medium.serviceTwo();
    }

    @GetMapping("/test4")
    public String test4(@RequestParam String level) {
        return serviceMap.get(level).serviceOne();
    }

    @GetMapping("/test5")
    public String test5(@RequestParam String level) {
        return serviceMapNew.get(level).serviceOne();
    }

    @GetMapping("/test7")
    public List<User> test7(@RequestParam String name) {
        List<User> userList = userRepository.searchUser(name);
        return userList;
    }

    @GetMapping("/test8")
    public List<User> test8(@RequestParam String name) {
        List<User> userList = userRepository.findByName(name);
        return userList;
    }

    public static void main(String[] args) {
        DemoController1 controller1 = new DemoController1();
        controller1.test1();
        controller1.test2();
        controller1.test3();
        controller1.test4("good");
        controller1.test5("good");
        controller1.test7("Alex");
    }
}
