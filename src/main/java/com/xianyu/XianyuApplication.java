package com.xianyu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.xianyu.dao")
public class XianyuApplication {

    public static void main(String[] args) {
        SpringApplication.run(XianyuApplication.class, args);
    }
}

