package com.example.start.booking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class RestSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.inMemoryAuthentication()
                .withUser("t").password("{noop}t")
                .authorities("ROLE_ADMIN")
                .and()
                .withUser("p").password("{noop}p")
                .authorities("ROLE_USER");
    }
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/api/basicAuth/**").permitAll()
                .antMatchers("/api/basicAuth/**")
                .hasAnyRole("ADMIN", "USER")
                .and().httpBasic();
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/bookings/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/api/**").hasRole("ADMIN")
                .and()
                .addFilter(new JWTAuthorizationFilter(authenticationManager()));
    }
}
