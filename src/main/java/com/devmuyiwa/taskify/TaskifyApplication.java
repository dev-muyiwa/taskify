package com.devmuyiwa.taskify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.modulith.Modulith;
import org.springframework.scheduling.annotation.EnableAsync;

@Modulith
@EnableCaching
@EnableAsync
@SpringBootApplication
public class TaskifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskifyApplication.class, args);
    }

}
