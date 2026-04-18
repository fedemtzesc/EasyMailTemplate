package com.fdxsoft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fdxsoft.entities.SchedulerEntity;

public interface SchedulerRepository extends JpaRepository<SchedulerEntity, Long> {

}
