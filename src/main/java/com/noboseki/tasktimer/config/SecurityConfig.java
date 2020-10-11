package com.noboseki.tasktimer.config;

import com.noboseki.tasktimer.security.SFGPasswordEncoderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${noboseki.security.admin.name}")
    private String adminName;

    @Value("${noboseki.security.admin.password}")
    private String adminPassword;

    @Value("${noboseki.security.user.name}")
    private String userName;

    @Value("${noboseki.security.user.password}")
    private String userPassword;

    @Bean
    PasswordEncoder passwordEncoder() {
        return SFGPasswordEncoderFactory.createDelegatingPasswordEncoder();
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
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        auth.inMemoryAuthentication()
                .withUser(adminName)
                .password("{bcrypt}" + encoder.encode(adminPassword))
                .roles("ADMIN")
                .and()
                .withUser(userName)
                .password("{bcrypt}" + encoder.encode(userPassword))
                .roles("USER");
    }

    /*    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username(adminName)
                .password(adminPassword)
                .roles("ADMIN").build();

        UserDetails user = User.withDefaultPasswordEncoder()
                .username(userName)
                .password(userPassword)
                .roles("USER").build();

        return new InMemoryUserDetailsManager(admin, user);
    }*/
}
