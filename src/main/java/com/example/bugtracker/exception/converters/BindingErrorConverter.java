package com.example.bugtracker.exception.converters;

import com.example.bugtracker.dto.error.BindingErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


@Component
@RequiredArgsConstructor
public class BindingErrorConverter {

    /*
     * Re-formats BindException error messages into a user-friendly form.
     * Fetches error messages from the 'application.properties' file based on error codes.
     * If a custom message corresponding to the given error message code is not found
     * , a default Spring validation error message will be used.
     */

    private final MessageSource messageSource;
    public List<BindingErrorResponse> fieldsToMessages(List<FieldError> fieldErrors) {
        return fieldErrors.stream().map(this::fieldToMessage).toList();
    }

    private BindingErrorResponse fieldToMessage(FieldError fieldError){
        return Arrays.stream(Objects.requireNonNull(fieldError.getCodes()))
                .map(code-> {
                    return getFieldBindingErrorResponse(fieldError, code); //code -> BindingErrorResponse
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(new BindingErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()));
    }

    private BindingErrorResponse getFieldBindingErrorResponse(FieldError fieldError, String code) {
        try {
            String message = messageSource.getMessage( //Search for a custom message corresponding the given code
                    code,
                    fieldError.getArguments(),
                    Locale.ENGLISH
            );
            return new BindingErrorResponse(fieldError.getField(), message);
        } catch(NoSuchMessageException e){
            return new BindingErrorResponse(fieldError.getField(), fieldError.getDefaultMessage());
            // Returns a default message if no custom message was found.
        }
    }

    public List<BindingErrorResponse> globalsToMessages(List<ObjectError> globalErrors) {
        return globalErrors.stream().map(this::globalToMessage).toList();
    }

    private BindingErrorResponse globalToMessage(ObjectError objectError){
        return Arrays.stream(Objects.requireNonNull(objectError.getCodes()))
                .map(code-> getGlobalBindingErrorResponse(objectError, code))//code -> BindingErrorResponse
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(new BindingErrorResponse(objectError.getObjectName(), objectError.getDefaultMessage()));
    }

    private BindingErrorResponse getGlobalBindingErrorResponse(ObjectError objectError, String code) {
        try {
            String message = messageSource.getMessage( //Search for a custom message corresponding the given code
                    code,
                    objectError.getArguments(),
                    Locale.ENGLISH
            );
            return new BindingErrorResponse(objectError.getObjectName(), message);
        } catch(NoSuchMessageException e){
            return null; // Returns null if no custom message was found.
        }
        }
    }


