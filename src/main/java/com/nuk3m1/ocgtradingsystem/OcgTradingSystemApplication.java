package com.nuk3m1.ocgtradingsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.nuk3m1.ocgtradingsystem.mapper")

public class OcgTradingSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(OcgTradingSystemApplication.class, args);
    }

}
