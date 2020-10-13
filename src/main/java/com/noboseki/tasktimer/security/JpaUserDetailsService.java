package com.noboseki.tasktimer.security;

import com.noboseki.tasktimer.domain.Authority;
import com.noboseki.tasktimer.domain.User;
import com.noboseki.tasktimer.repository.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Getting User info via JPA");

        User userSecurity = userDao.findByPublicId(Long.valueOf(username)).orElseThrow(() -> {
           throw  new UsernameNotFoundException("User name: " + username + "not found");
        });

        return new org.springframework.security.core.userdetails.User(userSecurity.getUsername(), userSecurity.getPassword(),
                userSecurity.getEnabled(), userSecurity.getAccountNonExpired(), userSecurity.getCredentialsNonExpired(),
                userSecurity.getAccountNonLocked(), convertToSpringAuthorities(userSecurity.getAuthorities()));
    }

    private Collection<? extends GrantedAuthority> convertToSpringAuthorities(Set<Authority> authorities) {
        if (authorities != null && authorities.size() > 0){
            return authorities.stream()
                    .map(Authority::getRole)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }
}
