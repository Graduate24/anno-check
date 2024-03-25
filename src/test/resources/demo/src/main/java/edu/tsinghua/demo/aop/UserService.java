package edu.tsinghua.demo.aop;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Log
    public String place(String address) {
        return address;
    }
}
