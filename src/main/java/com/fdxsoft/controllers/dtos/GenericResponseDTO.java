package com.fdxsoft.controllers.dtos;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GenericResponseDTO<T> {
    private String message;
    private String status;
    private Integer httpStatus;
    private List<T> data;
    
    // Campos para paginación
    private boolean paging;
    private Integer currentPage;
    private Integer lastPage;
    private Long totalRecords;

    // Con este metodo regresamos la respuesta en un JSON Ordenado
    public Map<String, Object> getOrderedResponse() {
    	Map<String, Object> response = new LinkedHashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", message);
        response.put("status", status);
        response.put("httpStatus", httpStatus);
        if(paging) {
        	response.put("lastPage", lastPage);
            response.put("totalRecords", totalRecords);
            response.put("currentPage", currentPage);
        }
        response.put("data", data);
        return response;
    }
}
