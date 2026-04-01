package com.fdxsoft.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fdxsoft.controllers.dtos.GenericResponseDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGRequestDTO;
import com.fdxsoft.entities.WYSIWYGEntity;
import com.fdxsoft.services.impl.WYSIWYGServiceImpl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class APIController {
    @Autowired
    private WYSIWYGServiceImpl wysiwygServiceImpl;

    @PostMapping("/wysiwyg")
    public ResponseEntity<Map<String, Object>> postMethodName(@RequestBody WYSIWYGRequestDTO wysiwygRequestDTO) {
    	Map<String, Object> response = wysiwygServiceImpl.save(wysiwygServiceImpl.mapToEntity(wysiwygRequestDTO)).getOrderedResponse();
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.valueOf((Integer)response.get("httpStatus")));    
    }

    @GetMapping("/wysiwyg")
    public ResponseEntity<Map<String, Object>> findByTemplateName(@RequestParam String templateName){
    	Map<String, Object> response = wysiwygServiceImpl.findByTemplateName(templateName).getOrderedResponse();
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.valueOf((Integer)response.get("httpStatus")));
    }
}
