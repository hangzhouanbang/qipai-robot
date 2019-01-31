package com.anbang.fake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FakeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FakeApplication.class, args);

	}

}

