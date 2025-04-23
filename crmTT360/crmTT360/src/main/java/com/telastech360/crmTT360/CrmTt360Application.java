package com.telastech360.crmTT360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.telastech360.crmTT360.repository")
@EntityScan("com.telastech360.crmTT360.entity")
public class CrmTt360Application {
	public static void main(String[] args) {
		SpringApplication.run(CrmTt360Application.class, args);
	}
}