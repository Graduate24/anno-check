package edu.tsinghua.demo.aop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    private ShipmentService shipmentService;

    public String orderStuff() {
        System.out.println("Ordering stuff");
        return "Order";
    }

    public void cancelStuff() {
        System.out.println("Canceling stuff");
    }

    @Log
    public void callOuter() {
        shipmentService.outerCheck();
        shipmentService.innerCheck();
        System.out.println("call outer");
    }
}
