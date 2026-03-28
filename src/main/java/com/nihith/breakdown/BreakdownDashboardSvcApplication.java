package com.nihith.breakdown;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

/**
 * Spring Boot application entry point for the Breakdown Dashboard service.
 * Bootstraps the Spring context and starts the embedded server (or initialises
 * the WAR when deployed to an external container such as WildFly).
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.nihith"})
public class BreakdownDashboardSvcApplication {

	/**
	 * Application entry point. Delegates to {@link SpringApplication#run} to bootstrap
	 * the Spring context and start the service.
	 *
	 * @param args command-line arguments forwarded to the Spring application context
	 */
	public static void main(String[] args) {
		SpringApplication.run(BreakdownDashboardSvcApplication.class, args);
	}

}
