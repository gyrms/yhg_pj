package com.yhg.hotelbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class YhgPjApplication {

    public static void main(String[] args) {
        SpringApplication.run(YhgPjApplication.class, args);
    }

}
