package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.Task;
import com.noboseki.tasktimer.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskDao extends JpaRepository<Task, UUID> {

    Optional<Task> findByNameAndUser(String name, User user);

    List<Task> findAllByUser(User user);
}
