package no.protector.initializr.web.configuration.filter;

import co.elastic.apm.api.ElasticApm;
import no.protector.initializr.web.configuration.ThreadLocalRequestId;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StopWatch;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class MdcFilter implements Filter {

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
    private static final Logger LOG = LoggerFactory.getLogger(MdcFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        if (request instanceof HttpServletRequest httpServletRequest) {
            insertIntoMDC(request);
            StopWatch timer = new StopWatch();
            timer.start();

            try {
                chain.doFilter(request, response);
            } finally {
                timer.stop();
                LOG.info("{}={} {}={} {}={}",
                        "url", httpServletRequest.getServletPath(),
                        "httptook", timer.getTotalTimeMillis(),
                        "resultCode", ((HttpServletResponse) response).getStatus()
                );

                MDC.clear();
                ThreadLocalRequestId.unset();
            }
        }
    }

    private void insertIntoMDC(ServletRequest request) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (LOG.isDebugEnabled()) {
            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                String value = httpServletRequest.getHeader(key);
                LOG.debug("Request header: {} = {}", key, value);
            }
        }

        insertIntoMDC(REQUEST_REQUEST_ID, httpServletRequest.getHeader("X-Request-Id"),
                () -> UUID.randomUUID().toString());
        insertIntoMDC(REQUEST_REQUEST_URI, httpServletRequest.getRequestURI());
        insertIntoMDC(REQUEST_METHOD, httpServletRequest.getMethod());
        insertIntoMDC(REQUEST_REQUEST_URL, httpServletRequest.getRequestURL(), StringBuffer::toString);
        insertIntoMDC(REQUEST_QUERY_STRING, httpServletRequest.getQueryString());
        insertIntoMDC(REQUEST_X_FORWARDED_FOR, httpServletRequest.getHeader("X-Forwarded-For"));
        insertIntoMDC(REQUEST_ACCEPT_HEADER, httpServletRequest.getHeader("Accept"));
        insertIntoMDC(REQUEST_REMOTE_ADDR, httpServletRequest.getRemoteAddr());
        insertIntoMDC(REQUEST_REMOTE_URL, httpServletRequest.getHeader("X-Remote-Url"));
        insertIntoMDC(REQUEST_TRACE_ID, ElasticApm.currentTransaction().getTraceId());
        insertIntoMDC(REQUEST_TRANSACTION_ID, ElasticApm.currentTransaction().getId());
        insertIntoMDC(REQUEST_SPAN_ID, ElasticApm.currentSpan().getId());
    }

    private void insertIntoMDC(String key, String value) {
        if (StringUtils.isNotEmpty(value)) {
            MDC.put(key, value);
        }
    }

    private <T> void insertIntoMDC(String key, T value, Function<T, String> toString) {
        if (value != null) {
            insertIntoMDC(key, toString.apply(value));
        }
    }

    private void insertIntoMDC(String key, String value, Supplier<String> defaultSupplier) {
        if (StringUtils.isEmpty(value)) {
            value = defaultSupplier.get();
        }
        MDC.put(key, value);
    }
}

