package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.WorkTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorkTimeDao extends JpaRepository<WorkTime, UUID> {
}
