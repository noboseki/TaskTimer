package com.noboseki.tasktimer.service;

import com.noboseki.tasktimer.domain.Authority;
import com.noboseki.tasktimer.exeption.ResourceNotFoundException;
import com.noboseki.tasktimer.repository.AuthorityDao;
import com.noboseki.tasktimer.service.constants.ServiceTextConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityDao authorityDao;

    public Authority findByRole(String role) {
        return authorityDao.findByRole(role).orElseThrow(() -> {
            final String ROLE = "role";
            return new ResourceNotFoundException(ServiceTextConstants.getAuthority(), ROLE);
        });
    }
}
