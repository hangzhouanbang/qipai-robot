package com.anbang.qipai.robot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.anbang.qipai.robot.cqrs.c.repository.SingletonEntityFactoryImpl;
import com.highto.framework.ddd.SingletonEntityRepository;

@EnableEurekaClient
@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class QipaiRobotApplication {

	@Bean
	public SingletonEntityRepository singletonEntityRepository() {
		SingletonEntityRepository singletonEntityRepository = new SingletonEntityRepository();
		singletonEntityRepository.setEntityFactory(new SingletonEntityFactoryImpl());
		return singletonEntityRepository;
	}

	public static void main(String[] args) {
		SpringApplication.run(QipaiRobotApplication.class, args);
	}

}