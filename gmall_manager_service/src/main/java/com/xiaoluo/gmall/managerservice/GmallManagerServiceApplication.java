package com.xiaoluo.gmall.managerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan(basePackages = "com.xiaoluo.gmall.managerservice.mapper")
public class GmallManagerServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GmallManagerServiceApplication.class, args);
	}

}
