package no.protector.initializr.web.configuration.security;

import java.util.List;

public record AuthUser(String userName,
                       String email,
                       String phone,
                       String firstName,
                       String lastName,
                       List<AuthUserApplication> applications,
                       String authenticationProvider,
                       Long partyId,
                       String country) {
}
