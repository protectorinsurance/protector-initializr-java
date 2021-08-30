package no.protector.initializr.web.configuration.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUserService authUserService;

    public TokenAuthenticationFilter(
            AuthUserService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        getAccessToken(request)
                .ifPresent(
                        this::handleValidation
                );

        filterChain.doFilter(request, response);
    }

    private void handleValidation(final String jwtToken) {
        AuthUser authUser = authUserService.getAuthUser(jwtToken)
                .orElseThrow(() -> new SecurityException("No user retrieved from loginservice"));
        Set<GrantedAuthority> permissions = getUserAuthorities(authUser);
        setSecurityContext(authUser, permissions, jwtToken);
    }

    private static Set<GrantedAuthority> getUserAuthorities(final AuthUser user) {
        return user.applications()
                .stream()
                .map(AuthUserApplication::roles)
                .flatMap(Collection::stream)
                .map(r -> "ROLE_" + r)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private static void setSecurityContext(@NonNull final AuthUser user,
                                           @NonNull Set<GrantedAuthority> permissions,
                                           @NonNull final String jwtToken) {
        final User securityUser = new User(
                user.userName(),
                jwtToken,
                true,
                true,
                true,
                true,
                permissions);

        final UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(securityUser, jwtToken, securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private static Optional<String> getAuthTokenFromRequest(HttpServletRequest httpRequest) {
        return Optional.ofNullable(httpRequest.getHeader("Authorization"))
                .or(() -> Optional.ofNullable(httpRequest.getParameter("jwt")))
                .or(() -> Optional.ofNullable(WebUtils.getCookie(httpRequest, "jwt")).map(Cookie::getValue));
    }

    private static Optional<String> getAccessToken(HttpServletRequest httpRequest) {
        return getAuthTokenFromRequest(httpRequest)
                .map(at -> StringUtils.remove(at, "Bearer "))
                .map(String::trim)
                .filter(Predicate.not(String::isEmpty));
    }
}
