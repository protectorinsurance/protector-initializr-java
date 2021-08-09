package no.protector.initializr.domain.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcRepositories(basePackages = "no.protector.initializr")
public class DatabaseConfiguration extends AbstractJdbcConfiguration {

}
