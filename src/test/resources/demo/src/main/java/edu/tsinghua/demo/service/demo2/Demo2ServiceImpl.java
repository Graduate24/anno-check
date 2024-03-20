package edu.tsinghua.demo.service.demo2;

import org.springframework.stereotype.Service;

@Service
public class Demo2ServiceImpl implements Demo2Service {

    @Override
    public String service() {
        return "demo2 service";
    }
}
