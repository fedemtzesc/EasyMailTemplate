package com.fdxsoft.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fdxsoft.controllers.dtos.GenericResponseDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGListDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGRequestDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGViewDTO;
import com.fdxsoft.entities.WYSIWYGEntity;

public interface WYSIWYGService {
    GenericResponseDTO<WYSIWYGEntity> findByEntityName(String templateName);
    
    GenericResponseDTO<WYSIWYGEntity> save(WYSIWYGRequestDTO wysiwygRequestDTO);
    
    GenericResponseDTO<WYSIWYGEntity> delete(Long id);
    
    GenericResponseDTO<WYSIWYGViewDTO> findById(Long id);
    
    GenericResponseDTO<WYSIWYGEntity> update(WYSIWYGRequestDTO wysiwygRequestDTO);
    
    GenericResponseDTO<WYSIWYGListDTO> getAll(int page, int size, String search);

    WYSIWYGEntity mapToEntity(WYSIWYGRequestDTO wysiwygRequestDTO);
}
