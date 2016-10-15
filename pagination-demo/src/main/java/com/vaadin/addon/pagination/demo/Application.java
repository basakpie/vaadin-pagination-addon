package com.vaadin.addon.pagination.demo;

import com.vaadin.spring.annotation.EnableVaadin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

/**
 * Created by basakpie on 2016-10-14.
 */
@Configuration
@EnableVaadin
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... strings) throws Exception {
        for(int i=0; i < 240; i++) {
            User user = new User(Long.valueOf(i));
            user.setEmail(i + "_@native.com");
            user.setName(i + "_native");
            DemoUI.userList.add(user);
        }
        for(int i=0; i < 512; i++) {
            User user = new User();
            user.setEmail(i + "_@jpa.com");
            user.setName(i + "_jpa");
            userRepository.save(user);
        }
    }
}
