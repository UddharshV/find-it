package com.uddharsh.findit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration 
@ComponentScan("com.uddharsh.findit")
public class FinditApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinditApplication.class, args);
	}

}
