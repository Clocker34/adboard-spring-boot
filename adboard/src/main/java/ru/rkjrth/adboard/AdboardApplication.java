package ru.rkjrth.adboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdboardApplication implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String dsUrl;

    @Value("${spring.datasource.username}")
    private String dsUser;

    @Value("${spring.datasource.password}")
    private String dsPassword;

    public static void main(String[] args) {
        SpringApplication.run(AdboardApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("=== DATASOURCE CONFIG SEEN BY SPRING ===");
        System.out.println("URL     = " + dsUrl);
        System.out.println("USER    = " + dsUser);
        System.out.println("PASS    = " + dsPassword);
        System.out.println("========================================");
    }
}
