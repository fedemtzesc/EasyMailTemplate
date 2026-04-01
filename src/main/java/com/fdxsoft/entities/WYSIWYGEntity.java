package com.fdxsoft.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "wysiwyg")
public class WYSIWYGEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String templateName;

    private String description;

    @Column(length = 1)
    private String sendFrequency; // S/Scheduled, D/Daily, I/Immediate

    private String dateTimeSending;

    private String repeatEachTimeAt;

    private String repeatLimitType; // UNLIMITED, QUANTITY, END_DATE

    private Integer repeatQuantity;

    private String repeatEndDate;

    private String emailList;

}
