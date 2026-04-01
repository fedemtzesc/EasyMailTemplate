package com.fdxsoft.services;

import com.fdxsoft.controllers.dtos.GenericResponseDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGRequestDTO;
import com.fdxsoft.entities.WYSIWYGEntity;

public interface WYSIWYGService {
    GenericResponseDTO<WYSIWYGEntity> findByTemplateName(String templateName);

    GenericResponseDTO<WYSIWYGEntity> save(WYSIWYGEntity wysiwygEntity);

    WYSIWYGEntity mapToEntity(WYSIWYGRequestDTO wysiwygRequestDTO);
}
