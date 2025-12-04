package com.surest.management.Surest_Management_App;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SurestManagementAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SurestManagementAppApplication.class, args);
	}

}
