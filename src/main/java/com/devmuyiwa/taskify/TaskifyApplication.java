package com.devmuyiwa.taskify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulith;

@Modulith
@SpringBootApplication
public class TaskifyApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskifyApplication.class, args);
    }

}
