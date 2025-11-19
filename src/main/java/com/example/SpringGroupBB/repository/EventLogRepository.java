package com.example.SpringGroupBB.repository;

import com.example.SpringGroupBB.entity.EventLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLogEntity, Long> {
}
