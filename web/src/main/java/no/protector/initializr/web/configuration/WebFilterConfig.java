package no.protector.initializr.web.configuration;

import no.protector.initializr.web.configuration.filter.MdcFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
public class WebFilterConfig {

    private static final Logger LOG = LoggerFactory.getLogger(WebFilterConfig.class);

    private final String apiPattern;

    public WebFilterConfig() {
        this.apiPattern = String.format("/%s/*", ApiConfig.API_PREFIX);
    }

    @Bean
    public FilterRegistrationBean<MdcFilter> mdcFilter() {
        FilterRegistrationBean<MdcFilter> mdcFilter = new FilterRegistrationBean<>(new MdcFilter());
        mdcFilter.addUrlPatterns(apiPattern);
        mdcFilter.setOrder(WebFilterOrder.MDC_FILTER.getOrder());
        logFilterCreation(mdcFilter);
        return mdcFilter;
    }

    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> etagFilter() {
        ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
        filter.setWriteWeakETag(true);

        FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter = new FilterRegistrationBean<>(filter);
        shallowEtagHeaderFilter.addUrlPatterns(apiPattern);
        shallowEtagHeaderFilter.setOrder(WebFilterOrder.ETAG_FILTER.getOrder());
        logFilterCreation(shallowEtagHeaderFilter);
        return shallowEtagHeaderFilter;
    }

    private void logFilterCreation(FilterRegistrationBean<?> filterRegistrationBean) {
        LOG.info(
                "Servlet filter created {}={} {}={} {}={}",
                "filterName", filterRegistrationBean.getFilter().getClass().getSimpleName(),
                "urlPattern", filterRegistrationBean.getUrlPatterns(),
                "order", filterRegistrationBean.getOrder()
        );
    }

    private enum WebFilterOrder {
        MDC_FILTER,
        ETAG_FILTER;
        int getOrder() {
            return this.ordinal();
        }
    }
}
