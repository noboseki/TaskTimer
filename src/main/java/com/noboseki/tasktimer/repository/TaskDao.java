package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Component
public interface TaskDao extends JpaRepository<Task, UUID> {
}
