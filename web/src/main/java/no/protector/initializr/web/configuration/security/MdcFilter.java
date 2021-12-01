package no.protector.initializr.web.configuration.security;

import co.elastic.apm.api.ElasticApm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.logstash.logback.argument.StructuredArguments.keyValue;

@Component
public class MdcFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(MdcFilter.class);
    private static final String USER_KEY = "username";
    private static final String REQUEST_REQUEST_URI = "req.requestURI";
    private static final String REQUEST_REQUEST_ID = "req.requestID";
    private static final String REQUEST_QUERY_STRING = "req.queryString";
    private static final String REQUEST_REQUEST_URL = "req.requestURL";
    private static final String REQUEST_X_FORWARDED_FOR = "req.xForwardedFor";
    private static final String REQUEST_REMOTE_ADDR = "req.remoteAddr";
    private static final String REQUEST_REMOTE_URL = "req.remoteUrl";
    private static final String REQUEST_METHOD = "req.method";
    private static final String REQUEST_ACCEPT_HEADER = "req.accept";
    private static final String REQUEST_TRACE_ID = "req.traceId";
    private static final String REQUEST_TRANSACTION_ID = "req.transactionId";
    private static final String REQUEST_SPAN_ID = "req.spanId";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        registerUsername();
        insertIntoMDC(request);
        StopWatch timer = new StopWatch();
        timer.start();
        try {
            filterChain.doFilter(request, response);
        } finally {
            timer.stop();
            LOG.info("{} {}",
                    keyValue("httptook", timer.getTotalTimeMillis()),
                    keyValue("resultCode", response.getStatus())
            );
            MDC.clear();
        }
    }

    private static void registerUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                MDC.put(USER_KEY, user.getUsername());
            }
        }
    }

    private static void insertIntoMDC(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (LOG.isDebugEnabled()) {
            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                String value = httpServletRequest.getHeader(key);
                LOG.debug("Request header: {} = {}", key, value);
            }
        }

        insertIntoMDC(httpServletRequest.getHeader("X-Request-Id"),
                () -> UUID.randomUUID().toString());
        insertIntoMDC(REQUEST_REQUEST_URI, httpServletRequest.getRequestURI());
        insertIntoMDC(REQUEST_METHOD, httpServletRequest.getMethod());
        insertIntoMDC(httpServletRequest.getRequestURL(), StringBuffer::toString);
        insertIntoMDC(REQUEST_QUERY_STRING, httpServletRequest.getQueryString());
        insertIntoMDC(REQUEST_X_FORWARDED_FOR, httpServletRequest.getHeader("X-Forwarded-For"));
        insertIntoMDC(REQUEST_ACCEPT_HEADER, httpServletRequest.getHeader("Accept"));
        insertIntoMDC(REQUEST_REMOTE_ADDR, httpServletRequest.getRemoteAddr());
        insertIntoMDC(REQUEST_REMOTE_URL, httpServletRequest.getHeader("X-Remote-Url"));
        insertIntoMDC(REQUEST_TRACE_ID, ElasticApm.currentTransaction().getTraceId());
        insertIntoMDC(REQUEST_TRANSACTION_ID, ElasticApm.currentTransaction().getId());
        insertIntoMDC(REQUEST_SPAN_ID, ElasticApm.currentSpan().getId());
    }

    private static void insertIntoMDC(String key, String value) {
        if (StringUtils.isNotEmpty(value)) {
            MDC.put(key, value);
        }
    }

    private static <T> void insertIntoMDC(T value, Function<T, String> toString) {
        if (value != null) {
            insertIntoMDC(MdcFilter.REQUEST_REQUEST_URL, toString.apply(value));
        }
    }

    private static void insertIntoMDC(String value, Supplier<String> defaultSupplier) {
        if (StringUtils.isEmpty(value)) {
            value = defaultSupplier.get();
        }
        MDC.put(MdcFilter.REQUEST_REQUEST_ID, value);
    }
}

