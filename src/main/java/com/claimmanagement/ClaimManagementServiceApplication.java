package com.claimmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Application Class for Claim Management Service
 *
 * @author Claim Management Team
 * @version 1.0.0
 * @SpringBootApplication is a convenience annotation that combines:
 * - @Configuration: Marks this class as a source of bean definitions
 * - @EnableAutoConfiguration: Enables Spring Boot's auto-configuration mechanism
 * - @ComponentScan: Enables component scanning in the current package and sub-packages
 * <p>
 * This annotation tells Spring Boot to:
 * 1. Start component scanning from this package
 * 2. Auto-configure beans based on classpath dependencies
 * 3. Create an embedded web server (Tomcat by default)
 * @since 2024-01-01
 */
@SpringBootApplication
public class ClaimManagementServiceApplication {

    /**
     * Main method - Entry point of the Spring Boot application
     * <p>
     * SpringApplication.run() method:
     * 1. Creates an ApplicationContext
     * 2. Registers command line arguments as Spring properties
     * 3. Loads and refreshes the ApplicationContext
     * 4. Triggers any CommandLineRunner beans
     *
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(ClaimManagementServiceApplication.class, args);
    }
}