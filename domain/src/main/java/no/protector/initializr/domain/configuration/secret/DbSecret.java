package no.protector.initializr.domain.configuration.secret;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

@Primary
@Configuration
@ConfigurationProperties(prefix = "db")
@PropertySource(value = "file:/var/run/secrets/db_write", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:secrets/db_write.properties", ignoreResourceNotFound = true)
public class DbSecret {
    private String hostPort;
    private String databaseName;
    private String username;
    private String password;

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

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
