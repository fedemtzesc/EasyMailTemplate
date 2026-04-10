package com.fdxsoft.controllers.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WYSIWYGViewDTO {

    // =========================
    // 1. Datos del formulario
    // =========================
    private Long id;
    private String templateName;
    private String description;
    private String sendFrequency;
    private String dateTimeSending;
    private String repeatEachTimeAt;
    private String repeatLimitType;
    private Integer repeatQuantity;
    private String repeatEndDate;
    private String emailList;

    // =========================
    // 2. HTML del editor
    // =========================
    private String htmlInput;

    // =========================
    // 3. Imágenes para preview
    // =========================
    // key = "logo.png"
    // value = "data:image/png;base64,..."
    private Map<String, String> images;
}
