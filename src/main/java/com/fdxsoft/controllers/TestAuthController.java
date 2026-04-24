package com.fdxsoft.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class TestAuthController {

	@GetMapping("/get")
	public String helloGet() {
		return "Hello World - GET";
	}
	
	@PostMapping("/post")
	public String helloPost() {
		return "Hello World - POST";
	}
	
	@PutMapping("/put")
	public String helloPut() {
		return "Hello World - PUT";
	}
	
	@PatchMapping("/patch")
	public String helloPatch() {
		return "Hello World - PATCH";
	}
	
	@DeleteMapping("/delete")
	public String helloDelete() {
		return "Hello World - DELETE";
	}
	
}
