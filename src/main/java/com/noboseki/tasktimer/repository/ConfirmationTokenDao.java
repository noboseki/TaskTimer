package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.ConfirmationToken;
import com.noboseki.tasktimer.domain.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfirmationTokenDao extends JpaRepository<ConfirmationToken, UUID> {

    Optional<ConfirmationToken> findByUser_EmailAndType(String emile, TokenType type);

    Optional<ConfirmationToken> findByConfirmationTokenAndType(String token, TokenType type);
}
