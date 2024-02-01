package com.example.bugtracker.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BindingErrorResponse {
    private final String field;
    private final String message;
}
