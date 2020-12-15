package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuthorityDao extends JpaRepository<Authority, UUID> {

    Optional<Authority> findByRole(String role);
}
