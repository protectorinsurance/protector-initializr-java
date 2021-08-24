package no.protector.initializr.web.configuration.security;

import java.util.Map;
import java.util.Set;

public record AuthUserApplication(String name, Map<String, Object> properties, Set<String> roles) {
}
