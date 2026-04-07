package com.fdxsoft.controllers.dtos;

import org.springframework.web.multipart.MultipartFile;

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
    private String description;
    private String sendFrequency; // S/Scheduled, D/Daily, I/Immediate
    private String dateTimeSending;
    private String repeatEachTimeAt;
    private String repeatLimitType; // UNLIMITED, QUANTITY, END_DATE
    private Integer repeatQuantity;
    private String repeatEndDate;
    private String emailList;
    private String htmlInput;
    private MultipartFile[] images;
}
