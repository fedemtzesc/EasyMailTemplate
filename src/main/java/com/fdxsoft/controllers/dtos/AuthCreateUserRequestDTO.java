package com.fdxsoft.controllers.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record AuthCreateUserRequestDTO(
		@NotBlank String username, 
		@NotBlank String password, 
		@NotBlank String name, 
		@NotBlank String lastName,
		@NotBlank String phone,
		@NotBlank String email, 
		@Valid AuthCreateRoleRequestDTO roleRequest
		) {

}
