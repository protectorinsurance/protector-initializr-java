//INITIALIZR:KAFKA
package no.protector.initializr.domain.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(prefix = "kafka")
@PropertySource(value = "classpath:/secrets/kafka_client", ignoreResourceNotFound = true)
@PropertySource(value = "file:/var/run/secrets/kafka_client", ignoreResourceNotFound = true)
public class KafkaUser {

    private String username;
    private String password;

    public String getUsernameAndPassword(final String separator) {
        return String.format("%s%s%s", username, separator, password);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
//INITIALIZR:KAFKA
