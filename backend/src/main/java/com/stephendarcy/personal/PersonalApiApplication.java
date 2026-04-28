package com.stephendarcy.personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PersonalApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonalApiApplication.class, args);
    }
}
