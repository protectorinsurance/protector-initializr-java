//INITIALIZR:KAFKA-PRODUCER
package no.protector.initializr.domain.configuration.secret;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:/var/run/secrets/initializr_kafka_client", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:/secrets/initializr_kafka_client", ignoreResourceNotFound = true)
public class KafkaSecret {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
//INITIALIZR:KAFKA-PRODUCER
