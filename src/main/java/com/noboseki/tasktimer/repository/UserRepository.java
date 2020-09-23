package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
