package com.house.hunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories
public class HouseHunterBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(HouseHunterBeApplication.class, args);
    }

}
