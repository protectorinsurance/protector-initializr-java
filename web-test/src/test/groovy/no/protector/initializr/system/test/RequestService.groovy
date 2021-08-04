package no.protector.initializr.system.test

import groovy.json.JsonBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import org.testcontainers.containers.GenericContainer

@Service
class RequestService {

    @Autowired
    GenericContainer protectorInitializrContainer

    def <T> ResponseEntity<T> exchange(URI url, HttpMethod httpMethod, ParameterizedTypeReference<T> responseType) {
        exchange(url, httpMethod, null, responseType)
    }

    def <T> ResponseEntity<T> exchange(URI url, HttpMethod httpMethod, Object request, ParameterizedTypeReference<T> responseType) {
        exchange(url, httpMethod, request, responseType, null)
    }

    def <T> ResponseEntity<T> exchange(
            URI url, HttpMethod httpMethod, Object request, ParameterizedTypeReference<T> responseType, Map<String, String> urlParameters) {
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

    private HttpEntity getHttpEntity(Object body) {
        new HttpEntity<>(getBody(body), headers())
    }

    private static String getBody(Object request) {
        if (request instanceof String)
            return request
        request ? new JsonBuilder(request).toString() : null
    }

    private HttpHeaders headers() {
        HttpHeaders headers = new HttpHeaders()
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON))
        headers
    }

    private def printContainerLog() {
        println("***********************************")
        println("LOGS FROM CONTAINER:")
        println("***********************************")
        println(protectorInitializrContainer.logs)
        println("***********************************")
    }
}
