package no.protector.initializr.web.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConfigurationProperties(prefix = "userservice")
public class WebConfig implements WebMvcConfigurer {

    public static final String API_PREFIX = "api";

    private String url;

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.addPathPrefix(API_PREFIX, clazz -> clazz.isAnnotationPresent(RestController.class));
    }

    @Bean
    public String userServiceBaseUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
