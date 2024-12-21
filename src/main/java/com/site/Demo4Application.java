package com.site;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Demo4Application {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Demo4Application.class);
        app.run(args);

    }

}
