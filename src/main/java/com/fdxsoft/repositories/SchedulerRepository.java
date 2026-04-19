package com.fdxsoft.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdxsoft.entities.SchedulerEntity;

@Repository
public interface SchedulerRepository extends JpaRepository<SchedulerEntity, Long> {

}
