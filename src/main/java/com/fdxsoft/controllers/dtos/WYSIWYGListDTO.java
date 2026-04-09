package com.fdxsoft.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WYSIWYGListDTO {

    private Long id;
    private String templateName;
    private String sendFrequencyDescription;
    private String repeatLimitDescription;

}

