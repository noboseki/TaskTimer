package com.noboseki.tasktimer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    AdminConfig adminConfig;

    public SecurityConfig(AdminConfig adminConfig) {
        this.adminConfig = adminConfig;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> {
                    authorize.antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll();
                } )
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();
    }

    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username(adminConfig.getAdminName())
                .password(adminConfig.getAdminPassword())
                .roles("ADMIN").build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username(admin.getUsername())
                .password(adminConfig.getUserPassword())
                .roles("USER").build();

        return new InMemoryUserDetailsManager(admin, user);
    }
}
