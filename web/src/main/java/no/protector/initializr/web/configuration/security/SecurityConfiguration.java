package no.protector.initializr.web.configuration.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

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

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .requestMatchers(EndpointRequest.to("health"));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .anyRequest().fullyAuthenticated()
                .and()
                .csrf().disable();

        http.addFilterBefore(mdcFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(tokenAuthenticationFilter, MdcFilter.class);
    }
}
