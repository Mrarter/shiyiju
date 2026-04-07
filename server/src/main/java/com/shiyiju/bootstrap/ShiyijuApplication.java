package com.shiyiju.bootstrap;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.shiyiju")
@MapperScan("com.shiyiju.modules")
public class ShiyijuApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiyijuApplication.class, args);
    }
}
