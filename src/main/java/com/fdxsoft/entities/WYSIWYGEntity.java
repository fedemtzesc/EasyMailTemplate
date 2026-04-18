package com.fdxsoft.entities;

import java.time.LocalDateTime;

import com.fdxsoft.enums.RepeatLimitType;
import com.fdxsoft.enums.SendFrequency;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(columnDefinition = "TEXT")
    private String description;


	@Enumerated(EnumType.STRING)
	private SendFrequency sendFrequency;
	
    private LocalDateTime dateTimeSending;

    private LocalDateTime repeatEachTimeAt;

    @Enumerated(EnumType.STRING)
	private RepeatLimitType repeatLimitType;

    private Integer repeatQuantity;

    private LocalDateTime repeatEndDate;

    private String emailList;
    
    @Column(name = "creation_date",
    	    nullable = false,
    	    updatable = false,
    	    insertable = false,
    	    columnDefinition = "TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP(3)")
    private LocalDateTime creationDate;

}
