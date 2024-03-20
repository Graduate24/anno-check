package edu.tsinghua.demo.aop;

import org.springframework.stereotype.Service;

@Service
public class ShipmentService {
    @Log
    // this here is what's called a join point
    public void shipStuff() {
        System.out.println("shipStuff In Service");
    }

    public void shipStuffWithBill() {
        innerCheck();
        System.out.println("shipStuffWithBill In Service");
    }

    public void innerCheck(){
        System.out.println("inner check");
    }

    public void outerCheck(){
        System.out.println("outer check");
    }

}
