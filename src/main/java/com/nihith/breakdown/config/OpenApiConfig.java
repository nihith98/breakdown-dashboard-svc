package com.nihith.breakdown.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:breakdown-dashboard-svc}")
    private String applicationName;

    @Bean
    public OpenAPI breakdownOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Breakdown Dashboard API")
                        .version("0.0.1-SNAPSHOT")
                        .description(
                                "REST API backend for **Breakdown** — an expense-management and bill-splitting platform " +
                                "inspired by Splitwise.\n\n" +
                                "Users are organised into **Groups**. Inside a group, expenses are recorded as Transactions. " +
                                "The settlement engine automatically computes the minimum number of transfers needed to " +
                                "clear all outstanding balances.\n\n" +
                                "**Family feature:** A Family is a named sub-group within an expense group. Members of the " +
                                "same family are treated as a single collective entity during settlement computation. " +
                                "For example, if A pays $6 split equally among A, B, and C, and B & C belong to Family F, " +
                                "a single settlement record `F → A: $4` is generated instead of two separate records."
                        )
                        .contact(new Contact()
                                .name("Nihith")))
                .servers(List.of(
                        new Server()
                                .url("/")
                                .description("Default server")));
    }
}
