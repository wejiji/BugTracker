package com.example.security2pro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BindingErrorResponse {
    private String field;
    private String message;
}
