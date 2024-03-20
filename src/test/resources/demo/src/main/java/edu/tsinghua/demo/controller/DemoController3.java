package edu.tsinghua.demo.controller;

import edu.tsinghua.demo.aop.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController3 {

    @Autowired
    private BillService billService;

    @GetMapping("/demo3test1")
    public String test1() {
        return billService.sendBill("Beijing", 10000d);
    }

    public static void main(String[] args) {
        BillService billService = new BillService();
        billService.sendBill("Beijing", 10000d);
    }
}
