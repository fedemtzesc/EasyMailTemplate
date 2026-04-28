package com.fdxsoft.controllers.dtos;

import java.util.List;

import org.springframework.validation.annotation.Validated;


@Validated
public record AuthCreateRoleRequestDTO(
		List<String> roleListName) {

}
