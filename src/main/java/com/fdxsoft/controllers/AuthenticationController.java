package com.fdxsoft.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fdxsoft.controllers.dtos.AuthCreateUserRequestDTO;
import com.fdxsoft.controllers.dtos.AuthLoginRequest;
import com.fdxsoft.controllers.dtos.AuthResponse;
import com.fdxsoft.service.impl.UserDetailsServiceImpl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth/v1")
public class AuthenticationController {
	@Value("${spring.security.app.cookie.secure}")
	private boolean secureCookie;	
	
	@Autowired
	private UserDetailsServiceImpl detailsServiceImpl;

	@PostMapping(value = "/log-in")
	public ResponseEntity<Map<String, Object>> authenticateUser(@RequestBody @Valid AuthLoginRequest userRequest,
			HttpServletResponse httpResponse) {
		Map<String, Object> response = detailsServiceImpl.loginUser(userRequest).getOrderedResponse();

		// Crear cookie segura
		if ("success".equals(response.get("status")) && response.get("data") != null) {

		    AuthResponse authResponse =
		        ((java.util.List<AuthResponse>) response.get("data")).get(0);

		    Cookie cookie = new Cookie("access_token", authResponse.jwt());
		    cookie.setSecure(secureCookie);
		    cookie.setHttpOnly(true);
		    cookie.setPath("/");

		    httpResponse.addCookie(cookie);
		}

		return new ResponseEntity<Map<String, Object>>(response,
				HttpStatus.valueOf((Integer) response.get("httpStatus")));
	}

	@PostMapping(value = "/sign-up")
	public ResponseEntity<Map<String, Object>> createUser(@RequestBody @Valid AuthCreateUserRequestDTO userToCreate,
			HttpServletResponse httpResponse) {
		Map<String, Object> response = detailsServiceImpl.createUser(userToCreate).getOrderedResponse();

		// Crear cookie segura
		if ("success".equals(response.get("status")) && response.get("data") != null) {

		    AuthResponse authResponse =
		        ((java.util.List<AuthResponse>) response.get("data")).get(0);

		    Cookie cookie = new Cookie("access_token", authResponse.jwt());
		    cookie.setSecure(secureCookie);
		    cookie.setHttpOnly(true);
		    cookie.setPath("/");

		    httpResponse.addCookie(cookie);
		}

		return new ResponseEntity<Map<String, Object>>(response,
				HttpStatus.valueOf((Integer) response.get("httpStatus")));
	}

}
