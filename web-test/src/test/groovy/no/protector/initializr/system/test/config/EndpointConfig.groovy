package no.protector.initializr.system.test.config


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer

@Configuration
class EndpointConfig {

    @Autowired
    GenericContainer protectorInitializrContainer

    @Bean
    URL initializrBaseUrl() {
        new URL("http://${protectorInitializrContainer.host}:${protectorInitializrContainer.getMappedPort(8080)}/api")
    }
}
