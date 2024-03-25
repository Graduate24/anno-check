package edu.tsinghua.demo;

import edu.tsinghua.demo.aop.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AopApplicationTests {
    @Autowired
    ShipmentService shipmentService;
    @Autowired
    BillService billService;
    @Autowired
    MathCalculator mathCalculator;
    @Autowired
    UserService userService;

    @Test
    void testBeforeLog() {
        shipmentService.shipStuff();
    }

    @Test
    void testUserLog() {
        userService.place("beijing");
    }

    @Test
    void testBeforeLogWithBill() {
        shipmentService.shipStuffWithBill();
    }

    @Test
    void testBeforeOuter() {
        shipmentService.outerCheck();
    }

    @Test
    void testBillService() {
        billService.bill();
    }

    @Autowired
    OrderService orderService;

    @Test
    void testOrderWithLogicalOperator() {
        orderService.orderStuff();
    }

    @Test
    void testCancelWithLogicalOperator() {
        orderService.cancelStuff();
    }

    @Test
    void test() {
        String result = billService.sendBill("beijing", 100.00);
        System.out.println(result);
    }

    @Test
    void testAroundAop() {
        mathCalculator.add(1, 2);
    }

    @Test
    void test2() {
        orderService.callOuter();
    }

}
