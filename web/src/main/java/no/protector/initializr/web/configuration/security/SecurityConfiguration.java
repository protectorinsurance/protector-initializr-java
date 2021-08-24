package no.protector.initializr.web.configuration.security;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.ResourcePools;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.time.Duration;

import static no.protector.initializr.web.configuration.security.SecurityRoles.BASE_USER_ROLE;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final MdcFilter mdcFilter;

    public SecurityConfiguration(
            TokenAuthenticationFilter tokenAuthenticationFilter, MdcFilter mdcFilter) {
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.mdcFilter = mdcFilter;
    }

    @Bean
    public UserCache authUserCache() {
        final ResourcePools resourcePools = ResourcePoolsBuilder.heap(250).build();
        final CacheConfiguration<String, UserDetails> cacheConfiguration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, UserDetails.class, resourcePools)
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMinutes(1)))
                .build();
        final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .withCache("preConfiguredUserCache", cacheConfiguration)
                .build(true);
        Cache<String, UserDetails> cache = cacheManager.createCache("userCache", cacheConfiguration);
        return new EhUserCache(cache);
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .requestMatchers(EndpointRequest.to("health"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .anyRequest().hasRole(BASE_USER_ROLE)
                .and()
                .csrf().disable();

        http.addFilterBefore(mdcFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(tokenAuthenticationFilter, MdcFilter.class);
    }
}
