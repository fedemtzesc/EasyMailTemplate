package com.fdxsoft.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fdxsoft.controllers.dtos.WYSIWYGRequestDTO;
import com.fdxsoft.services.impl.WYSIWYGServiceImpl;

@RestController
@RequestMapping("/api/v1")
public class APIController {
    @Autowired
    private WYSIWYGServiceImpl wysiwygServiceImpl;

    @PostMapping(value="/wysiwyg", 
    		     consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addNewTemplate(@ModelAttribute WYSIWYGRequestDTO wysiwygRequestDTO) {
    	Map<String, Object> response = wysiwygServiceImpl.save(wysiwygRequestDTO).getOrderedResponse();
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.valueOf((Integer)response.get("httpStatus")));    
    }

    @GetMapping("/wysiwyg")
    public ResponseEntity<Map<String, Object>> findByTemplateName(@RequestParam String templateName){
    	Map<String, Object> response = wysiwygServiceImpl.findByEntityName(templateName).getOrderedResponse();
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.valueOf((Integer)response.get("httpStatus")));
    }
    
    @DeleteMapping("/wysiwyg/{id}")
    public ResponseEntity<Map<String, Object>> deleteTemplateById(@PathVariable Long id){
    	Map<String, Object> response = wysiwygServiceImpl.delete(id).getOrderedResponse();
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.valueOf((Integer)response.get("httpStatus")));
    }
    
    @PatchMapping(value="/wysiwyg", 
		     consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateTemplate(@ModelAttribute WYSIWYGRequestDTO wysiwygRequestDTO) {
    	Map<String, Object> response = wysiwygServiceImpl.update(wysiwygRequestDTO).getOrderedResponse();
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.valueOf((Integer)response.get("httpStatus")));    
    }
    
    @GetMapping("/wysiwyg/all")
    public ResponseEntity<Map<String, Object>> getAllTemplates(){
    	Map<String, Object> response = wysiwygServiceImpl.getAll().getOrderedResponse();
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.valueOf((Integer)response.get("httpStatus")));
    }
}