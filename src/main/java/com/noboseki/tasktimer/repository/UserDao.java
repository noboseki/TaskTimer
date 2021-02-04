package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String user);

    Optional<User> findByEmailAndPassword(String email, String password);

}
