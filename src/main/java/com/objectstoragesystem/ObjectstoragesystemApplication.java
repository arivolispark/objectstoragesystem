package com.objectstoragesystem;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class ObjectstoragesystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(ObjectstoragesystemApplication.class, args);
    }
}