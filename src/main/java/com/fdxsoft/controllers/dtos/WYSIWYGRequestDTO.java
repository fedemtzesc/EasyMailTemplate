package com.fdxsoft.controllers.dtos;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import com.fdxsoft.enums.RepeatLimitType;
import com.fdxsoft.enums.SendFrequency;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WYSIWYGRequestDTO {
	private Long id;
	private String templateName;
	private String templateSubject;
	private String description;
	@NotNull
	@Enumerated(EnumType.STRING)
	private SendFrequency sendFrequency;
	private LocalDateTime dateTimeSending;
	private LocalDateTime repeatEachTimeAt;
	@NotNull
	@Enumerated(EnumType.STRING)
	private RepeatLimitType repeatLimitType;
	private Integer repeatQuantity;
	private LocalDateTime repeatEndDate;
	private String emailList;
	private String htmlInput;
	private MultipartFile[] images;
}
