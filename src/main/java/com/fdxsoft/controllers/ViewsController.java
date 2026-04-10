package com.fdxsoft.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fdxsoft.controllers.dtos.GenericResponseDTO;
import com.fdxsoft.controllers.dtos.WYSIWYGViewDTO;
import com.fdxsoft.entities.WYSIWYGEntity;
import com.fdxsoft.services.impl.WYSIWYGServiceImpl;

import jakarta.websocket.server.PathParam;

@Controller
public class ViewsController {
	@Autowired
	WYSIWYGServiceImpl wysiwygServiceImpl;

	@GetMapping("/wysiwyg")
	public String getWYSIWYG(@RequestParam(required = false) Long id, Model model) {
		boolean editMode = false;

	    if (id != null) {
	        GenericResponseDTO<WYSIWYGViewDTO> response = wysiwygServiceImpl.findById(id);
	        if (response.getData() != null && !response.getData().isEmpty()) {
	            model.addAttribute("viewDTO", response.getData().get(0));
	            editMode = true;
	        } else {
	            model.addAttribute("errorMessage", "No se encontró la plantilla");
	        }
	    }
	    model.addAttribute("editMode", editMode);
	    return "wysiwyg";
	}
    
    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }
    
    @GetMapping("/templates-list")
    public String templatesList() {
        return "templates-list";
    }
}
