package com.nihith.breakdown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@ComponentScan(basePackages = {"com.nihith"})
public class BreakdownDashboardSvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(BreakdownDashboardSvcApplication.class, args);
	}

}
