package com.fdxsoft.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fdxsoft.controllers.dtos.GenericResponseDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGRequestDTO;
import com.fdxsoft.entities.WYSIWYGEntity;
import com.fdxsoft.repositories.WYSIWYGRepository;
import com.fdxsoft.services.WYSIWYGService;

@Service
public class WYSIWYGServiceImpl implements WYSIWYGService {

    @Autowired
    private WYSIWYGRepository wysiwygRepository;

    @Override
    public GenericResponseDTO<WYSIWYGEntity> findByTemplateName(String templateName) {
        GenericResponseDTO<WYSIWYGEntity> response = new GenericResponseDTO<>();
        Optional<WYSIWYGEntity> wysiwygEntity = wysiwygRepository.findByTemplateName(templateName);
        if (wysiwygEntity.isPresent()) {
            response.setData(wysiwygEntity.get());
            response.setMessage("Plantilla Encontrada");
            response.setStatus("success");
            response.setHttpStatus(HttpStatus.OK.value());
        } else {
            response.setMessage("Plantilla no encontrada");
            response.setStatus("error");
            response.setHttpStatus(HttpStatus.NOT_FOUND.value());
        }
        return response;
    }

    @Override
    public GenericResponseDTO<WYSIWYGEntity> save(WYSIWYGEntity wysiwygEntity) {
        GenericResponseDTO<WYSIWYGEntity> response = new GenericResponseDTO<>();
        if (wysiwygRepository.findByTemplateName(wysiwygEntity.getTemplateName()).isPresent()) {
            response.setMessage("Ya existe una plantilla con este nombre ***");
            response.setStatus("error");
            response.setHttpStatus(HttpStatus.CONFLICT.value());
            return response;
        }
        WYSIWYGEntity savedEntity = wysiwygRepository.save(wysiwygEntity);
        response.setData(savedEntity);
        response.setMessage("Plantilla guardada exitosamente");
        response.setStatus("success");
        response.setHttpStatus(HttpStatus.OK.value());
        return response;
    }

    @Override
    public WYSIWYGEntity mapToEntity(WYSIWYGRequestDTO wysiwygRequestDTO) {
        WYSIWYGEntity wysiwygEntity = new WYSIWYGEntity();
        if (wysiwygRequestDTO.getId() != null) {
            wysiwygEntity.setId(wysiwygRequestDTO.getId());
        }

        if (wysiwygRequestDTO.getTemplateName() != null) {
            wysiwygEntity.setTemplateName(wysiwygRequestDTO.getTemplateName());
        }

        if (wysiwygRequestDTO.getDescription() != null) {
            wysiwygEntity.setDescription(wysiwygRequestDTO.getDescription());
        }

        if (wysiwygRequestDTO.getSendFrequency() != null) {
            wysiwygEntity.setSendFrequency(wysiwygRequestDTO.getSendFrequency());
        }
        
        if(wysiwygRequestDTO.getDateTimeSending() != null) {
        	wysiwygEntity.setDateTimeSending(wysiwygRequestDTO.getDateTimeSending());
        }
        
        if(wysiwygRequestDTO.getRepeatEachTimeAt() != null) {
        	wysiwygEntity.setRepeatEachTimeAt(wysiwygRequestDTO.getRepeatEachTimeAt());
        }

        if(wysiwygRequestDTO.getRepeatLimitType() != null) {
        	wysiwygEntity.setRepeatLimitType(wysiwygRequestDTO.getRepeatLimitType());
        }
        
        if(wysiwygRequestDTO.getRepeatQuantity() != null) {
        	wysiwygEntity.setRepeatQuantity(wysiwygRequestDTO.getRepeatQuantity());
        }
        
        if(wysiwygRequestDTO.getRepeatEndDate() != null) {
        	wysiwygEntity.setRepeatEndDate(wysiwygRequestDTO.getRepeatEndDate());
        }
        
        if(wysiwygRequestDTO.getEmailList() != null) {
        	wysiwygEntity.setEmailList(wysiwygRequestDTO.getEmailList());
        }

        return wysiwygEntity;
    }

}
