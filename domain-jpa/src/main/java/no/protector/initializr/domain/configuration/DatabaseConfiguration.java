package no.protector.initializr.domain.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("no.protector.initializr")
@EnableJpaRepositories("no.protector.initializr")
@ComponentScan(basePackages = "no.protector.initializr")
public class DatabaseConfiguration {

}
