package com.noboseki.tasktimer.security;

import com.noboseki.tasktimer.repository.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String emile) throws UsernameNotFoundException {

        log.debug("Getting User info via JPA");

        return userDao.findByEmail(emile).orElseThrow(() -> {
           throw  new UsernameNotFoundException("User by emile: " + emile + "not found");
        });
    }
}
