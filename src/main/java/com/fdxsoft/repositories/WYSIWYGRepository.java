package com.fdxsoft.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fdxsoft.entities.WYSIWYGEntity;

public interface WYSIWYGRepository extends JpaRepository<WYSIWYGEntity, Long> {
    Optional<WYSIWYGEntity> findByTemplateName(String templateName);

}
