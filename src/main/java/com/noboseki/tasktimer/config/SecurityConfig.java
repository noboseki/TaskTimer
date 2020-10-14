package com.noboseki.tasktimer.config;

import com.noboseki.tasktimer.security.RestHeaderAuthFilter;
import com.noboseki.tasktimer.security.SFGPasswordEncoderFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager){
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()),
                UsernamePasswordAuthenticationFilter.class)
                .csrf().disable();

        http
                .authorizeRequests(authorize -> {
                    authorize.antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll();
                    authorize.antMatchers("/h2-console/**").permitAll();
                })
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic();

        //h2 console config
        http
                .headers().frameOptions().sameOrigin();
    }

/*    @Override
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
    }*/
}
