package com.noboseki.tasktimer.repository;

import com.noboseki.tasktimer.domain.WorkTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WorkTimeRepository extends JpaRepository<WorkTime, UUID> {
}
