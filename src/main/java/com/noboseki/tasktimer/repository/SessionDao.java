package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.Session;
import com.noboseki.tasktimer.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SessionDao extends JpaRepository<Session, UUID> {

    List<Session> findAllByTask(Task task);

}
