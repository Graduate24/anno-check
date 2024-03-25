package edu.tsinghua.demo.aop;

import org.springframework.stereotype.Service;

@Service
public class BillService {

    public void bill() {
        System.out.println("billing... ");
    }

    public String sendBill(String address, Double fee) {
        String text = "address: " + address + ", fee: " + fee;
        System.out.println(text);
        return text;
    }

}
