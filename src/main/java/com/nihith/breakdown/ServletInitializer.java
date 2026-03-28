package com.nihith.breakdown;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * WAR deployment initialiser for the Breakdown Dashboard service.
 * Extends {@link SpringBootServletInitializer} so the application can be
 * packaged as a WAR and deployed to an external servlet container.
 */
public class ServletInitializer extends SpringBootServletInitializer {

	/**
	 * Configures the {@link SpringApplicationBuilder} with the primary application
	 * source class, enabling WAR-based deployment.
	 *
	 * @param application the builder used to configure the application
	 * @return the configured {@link SpringApplicationBuilder}
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(BreakdownDashboardSvcApplication.class);
	}

}
