package edu.tsinghua.demo.config;

import edu.tsinghua.demo.entity.MyBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@ConfigurationPropertiesScan
public class FieldValueTestBean {

    @Value("${field}")
    private String filedValue;

    @Value("#{myBean}")
    private MyBean myBean;

    @Value("#{configProperties.credentials}")
    private Credentials credentials;

    @Value("#{ T(java.lang.Math).random() * 100.0 }")
    private int randomInt;

    @Value("#{new java.io.File('home')}")
    private File file;

    public String getFiledValue() {
        return filedValue;
    }

    public void setFiledValue(String filedValue) {
        this.filedValue = filedValue;
    }

    public MyBean getMyBean() {
        return myBean;
    }

    public void setMyBean(MyBean myBean) {
        this.myBean = myBean;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }

    public int getRandomInt() {
        return randomInt;
    }

    public void setRandomInt(int randomInt) {
        this.randomInt = randomInt;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
