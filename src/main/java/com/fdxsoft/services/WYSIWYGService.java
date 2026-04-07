package com.fdxsoft.services;

import com.fdxsoft.controllers.dtos.GenericResponseDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGRequestDTO;
import com.fdxsoft.entities.WYSIWYGEntity;

public interface WYSIWYGService {
    GenericResponseDTO<WYSIWYGEntity> findByEntityName(String templateName);

    GenericResponseDTO<WYSIWYGEntity> save(WYSIWYGRequestDTO wysiwygRequestDTO);
    
    GenericResponseDTO<WYSIWYGEntity> delete(Long id);
    
    GenericResponseDTO<WYSIWYGEntity> update(WYSIWYGRequestDTO wysiwygRequestDTO);
    
    GenericResponseDTO<WYSIWYGEntity> getAll();

    WYSIWYGEntity mapToEntity(WYSIWYGRequestDTO wysiwygRequestDTO);
}
