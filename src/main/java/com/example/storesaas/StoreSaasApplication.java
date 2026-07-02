package com.example.storesaas;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.example.storesaas.**.mapper")
@SpringBootApplication
public class StoreSaasApplication {
    public static void main(String[] args) {
        SpringApplication.run(StoreSaasApplication.class, args);
    }
}
