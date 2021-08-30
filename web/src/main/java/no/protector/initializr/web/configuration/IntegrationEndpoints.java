package no.protector.initializr.web.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "integration")
public class IntegrationEndpoints {

    private String userServiceUrl;

    public void setUserServiceUrl(String userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }

    public String getUserServiceUrl() {
        return userServiceUrl;
    }
}
