package no.protector.initializr.web.configuration.security;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class SecurityRoles {

    public static final String BASE_USER_ROLE = "Initializr User";

    private static final String PERMISSION_READ = permission(Permission.READ);
    private static final String PERMISSION_WRITE = permission(Permission.WRITE);

    private static final Map<String, Set<String>> roleMapping = Map.of(
            "Initializr user", Set.of(BASE_USER_ROLE, PERMISSION_READ, PERMISSION_WRITE)
    );

    public static Stream<String> expandRoles(String role) {
        return Stream.concat(Stream.of(role),
                roleMapping.getOrDefault(role, Collections.emptySet()).stream());
    }

    public static String permission(Permission capability) {
        return String.format("Permission[%s]", capability.toString());
    }

    public enum Permission {
        READ,
        WRITE
    }
}
