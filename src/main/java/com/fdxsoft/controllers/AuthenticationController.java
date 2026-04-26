package com.fdxsoft.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fdxsoft.controllers.dtos.AuthLoginRequest;
import com.fdxsoft.controllers.dtos.AuthResponse;
import com.fdxsoft.controllers.dtos.WYSIWYGRequestDTO;
import com.fdxsoft.service.impl.UserDetailsServiceImpl;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/v1")
public class AuthenticationController {

	@Autowired
	private UserDetailsServiceImpl detailsServiceImpl;

	@PostMapping(value = "/log-in")
	public ResponseEntity<Map<String, Object>> addNewTemplate(@RequestBody @Valid AuthLoginRequest userRequest) {
		Map<String, Object> response = detailsServiceImpl.loginUser(userRequest).getOrderedResponse();
		return new ResponseEntity<Map<String, Object>>(response,
				HttpStatus.valueOf((Integer) response.get("httpStatus")));
	}
}
