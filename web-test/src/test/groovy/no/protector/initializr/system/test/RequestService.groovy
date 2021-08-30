package no.protector.initializr.system.test

import groovy.json.JsonBuilder
import org.mockserver.client.MockServerClient
import org.mockserver.model.Header
import org.mockserver.model.HttpResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.GenericContainer

import static org.mockserver.model.HttpRequest.request
import static org.mockserver.model.HttpResponse.response
import static org.mockserver.model.MediaType.APPLICATION_JSON_UTF_8

@Service
class RequestService {

    @Autowired
    GenericContainer protectorInitializrContainer

    @Autowired
    MockServerClient mockServerClient

    private static String fake_jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InRlc3RAcHJvdGVjdG9yZm9yc2lrcmluZy5ubyIsInVzZXJOYW1lIjoidGVzdDAwIiwicGFydHlJZCI6MzQzLCJpYXQiOjE2MTUxOTA0NjIsImV4cCI6MTYxNTI1NTIwMH0.N-GMrWGAqramgKbwqfyDB8g_4oy2L9uvBYZwLlDx7H8"

    def <T> ResponseEntity<T> exchange(URI url, HttpMethod httpMethod, ParameterizedTypeReference<T> responseType) {
        exchange(url, httpMethod, null, responseType)
    }

    def <T> ResponseEntity<T> exchange(URI url, HttpMethod httpMethod, Object request,
                                       ParameterizedTypeReference<T> responseType) {
        exchange(url, httpMethod, request, responseType, null)
    }

    def <T> ResponseEntity<T> exchange(
            URI url, HttpMethod httpMethod, Object request, ParameterizedTypeReference<T> responseType,
            Map<String, String> urlParameters) {
        internalExchange({
            new RestTemplate().exchange(getUrl(url, urlParameters), httpMethod, getHttpEntity(request), responseType)
        })
    }

    def <T> ResponseEntity<T> exchange(URI url, HttpMethod httpMethod, Class<T> responseType) {
        exchange(url, httpMethod, null, responseType)
    }

    def <T> ResponseEntity<T> exchange(URI url, HttpMethod httpMethod, Object body, Class<T> responseType) {
        exchange(url, httpMethod, body, responseType, null)
    }

    def <T> ResponseEntity<T> exchange(
            URI url, HttpMethod httpMethod, Object body, Class<T> responseType, Map<String, String> urlParameters) {
        internalExchange({
            new RestTemplate().exchange(getUrl(url, urlParameters), httpMethod, getHttpEntity(body), responseType)
        }) as ResponseEntity<T>
    }

    private <T> ResponseEntity<T> internalExchange(Closure<ResponseEntity<T>> exchange) {
        initializeAuthentication()
        try {
            return exchange()
        } catch (Exception e) {
            printContainerLog()
            throw e
        }
    }

    private static URI getUrl(URI url, Map<String, String> urlParams) {
        if (!(urlParams != null && !urlParams.isEmpty()))
            return url
        def uriBuilder = UriComponentsBuilder.fromUri(url)
        urlParams.each { uriBuilder = uriBuilder.queryParam(it.key, it.value) }
        return uriBuilder.build().toUri()
    }

    private static HttpEntity getHttpEntity(Object body) {
        new HttpEntity<>(getBody(body), headers())
    }

    private static String getBody(Object request) {
        if (request instanceof String)
            return request
        request ? new JsonBuilder(request).toString() : null
    }

    private static HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders()
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON))
        headers.setContentType(MediaType.APPLICATION_JSON)
        headers.set("authorization", "Bearer ${fake_jwt}")
        headers
    }

    private initializeAuthentication() {
        mockServerClient.when(getAuthenticationRequest()).respond(getAuthenticationResponse())
    }

    private static org.mockserver.model.HttpRequest getAuthenticationRequest() {
        request()
                .withMethod("GET")
                .withPath("/api/users/validate/userinfo")
                .withHeader("Authorization", "Bearer " + fake_jwt)
    }

    private static HttpResponse getAuthenticationResponse() {
        response()
                .withStatusCode(200)
                .withHeaders(List.of(new Header("Content-Type", "application/json; charset=utf-8")))
                .withBody(new JsonBuilder([
                        "userName"              : "TomWaits99",
                        "email"                 : "tomwaits@protectorforsikring.no",
                        "phone"                 : "+31 20 550 3838",
                        "firstName"             : "Thomas Alan",
                        "lastName"              : "Waits",
                        "authenticationProvider": "ANTI- Records Europe",
                        "partyId"               : "1",
                        "country"               : "NO",
                        "applications"          : [
                                [
                                        "name"      : "Initializr",
                                        "properties": ["property": "object"],
                                        "roles"     : [
                                                "Initializr user",
                                                "Demo user"
                                        ]
                                ]
                        ]
                ]).toString(), APPLICATION_JSON_UTF_8)
    }

    private def printContainerLog() {
        println("***********************************")
        println("LOGS FROM CONTAINER:")
        println("***********************************")
        println(protectorInitializrContainer.logs)
        println("***********************************")
    }
}
