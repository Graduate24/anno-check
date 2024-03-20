package edu.tsinghua.demo.controller;

import edu.tsinghua.demo.service.demo2.Demo2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController2 {

    @Autowired
    private Demo2Service demo2Service;

    @GetMapping("/demo2test1")
    public String test1() {
        return demo2Service.service();
    }

}
