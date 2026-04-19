package com.fdxsoft.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fdxsoft.entities.WYSIWYGEntity;

@Repository
public interface WYSIWYGRepository extends JpaRepository<WYSIWYGEntity, Long> {
    Optional<WYSIWYGEntity> findByTemplateName(String templateName);

    Page<WYSIWYGEntity> findByTemplateNameContainingIgnoreCase(String search, Pageable pageable);
}
