package com.devm.devmtodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@EntityScan("com.user_entity")
@EnableJpaRepositories("com.repo")
@ComponentScan(basePackages = "com.controller,"+"com.user_entity,"+"com..services2")
@SpringBootApplication
public class DevmtodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevmtodoApplication.class, args);
    }

}