package com.vaadin.addon.pagination;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gmind on 2016-10-31.
 */
@SpringBootApplication
public class TestApplication implements CommandLineRunner {

    static final List<TestBean> beanList = new ArrayList<>();

    @Autowired
    TestBeanRepository testBeanRepository;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class);
    }


    @Override
    public void run(String... strings) throws Exception {
        for(int i=0; i < 240; i++) {
            TestBean bean = new TestBean(Long.valueOf(i));
            bean.setName(i+ "_name");
            beanList.add(bean);
            testBeanRepository.save(bean);
        }
    }
}
