package no.protector.initializr.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "no.protector.initializr")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
