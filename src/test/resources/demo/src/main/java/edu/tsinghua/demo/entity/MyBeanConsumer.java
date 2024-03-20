package edu.tsinghua.demo.entity;

public class MyBeanConsumer {
    private MyBean myBean;

    public MyBeanConsumer(MyBean myBean) {
        this.myBean = myBean;
        System.out.println("MyBeanConsumer created");
        System.out.println("myBean hashcode = " + myBean.hashCode());
    }

    public MyBean getMyBean() {
        return myBean;
    }

    public void setMyBean(MyBean myBean) {
        this.myBean = myBean;
    }
}
