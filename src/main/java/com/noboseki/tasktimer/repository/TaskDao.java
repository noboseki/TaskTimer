package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskDao extends JpaRepository<Task, UUID> {
}
