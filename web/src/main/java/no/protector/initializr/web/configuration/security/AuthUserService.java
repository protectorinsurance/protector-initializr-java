package no.protector.initializr.web.configuration.security;

import no.protector.initializr.web.configuration.IntegrationEndpoints;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthUserService {

    private static final Logger LOG = LoggerFactory.getLogger(AuthUserService.class);
    private final String userServiceBaseUrl;
    private final RestTemplate restTemplate;

    public AuthUserService(IntegrationEndpoints integrationEndpoints) {
        this.userServiceBaseUrl = integrationEndpoints.getUserServiceUrl() + "/api/users";
        this.restTemplate = new RestTemplate();
    }

    public Optional<AuthUser> getAuthUser(String token) {
        URI uri = getUri(userServiceBaseUrl + "/validate/userinfo");
        HttpEntity<?> entity = getHttpEntity(token);
        try {
            ResponseEntity<AuthUser> response = restTemplate.exchange(uri, HttpMethod.GET, entity, AuthUser.class);
            if (isRequestError(response)) {
                String errorMsg = "Unable to validate token/get userInfo from userService, got http status code: " +
                        response.getStatusCode() + ". jwt=" + token;
                if (System.getenv("LOG_FAILED_TOKEN_VALIDATION") != null) {
                    LOG.error(errorMsg);
                }
                throw new SecurityException(errorMsg);
            }
            return Optional.ofNullable(response.getBody());
        } catch (RestClientException e) {
            if (System.getenv("LOG_FAILED_TOKEN_VALIDATION") != null) {
                LOG.error("Unable to validate token/get userInfo from userService. jwt=" + token, e);
            }
            throw new SecurityException("Unable to validate token/get userInfo from userService. jwt=" + token, e);
        }
    }

    private static URI getUri(String url) {
        return getUri(url, new HashMap<>());
    }

    private static URI getUri(String url, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        queryParams.forEach(builder::queryParam);
        return builder.build().encode().toUri();
    }

    private static HttpEntity<?> getHttpEntity(String token) {
        HttpHeaders httpHeaders = getHttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + token);
        return new HttpEntity<>(httpHeaders);
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Request-Id", MDC.get("req.requestID"));
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    private boolean isRequestError(ResponseEntity<AuthUser> res) {
        return res.getStatusCode().is4xxClientError() || res.getStatusCode().is5xxServerError();
    }
}
