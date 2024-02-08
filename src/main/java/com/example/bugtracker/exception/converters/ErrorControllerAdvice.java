package com.example.bugtracker.exception.converters;



import com.example.bugtracker.controller.*;
import com.example.bugtracker.dto.error.BindingErrorResponse;
import com.example.bugtracker.exception.DirectMessageToClientException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.UnexpectedTypeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;

import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(
        assignableTypes = {
                AuthController.class
                , IssueController.class
                , ProjectController.class
                , SprintController.class
                , UserController.class
                , CommentController.class
                , ProjectMemberController.class})
public class ErrorControllerAdvice {

    private final BindingErrorConverter bindingErrorConverter;

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<BindingErrorResponse> validationErrorHandle(BindException e){
        /*
         * BindException class has many useful methods
         * that provides access to the errors passed as 'bindingResult'
         * */
        List<BindingErrorResponse> bindingFieldErrorResponse
                = bindingErrorConverter.fieldsToMessages(e.getFieldErrors());
        List<BindingErrorResponse> bindingGlobalErrorResponse
                = bindingErrorConverter.globalsToMessages(e.getGlobalErrors());
        List<BindingErrorResponse> responses = new ArrayList<>();
        responses.addAll(bindingFieldErrorResponse);
        responses.addAll(bindingGlobalErrorResponse);
        return responses;
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String deserializationHandle(HttpMessageConversionException e){
        log.error(e.getMessage());
        return "invalid request body. deserialization couldn't be completed";
    }


    @ExceptionHandler(UnexpectedTypeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String noSuitableValidator(UnexpectedTypeException e){
        log.error(e.getMessage());
        return "";
    }

    @ExceptionHandler(DirectMessageToClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String messageToClientForIllegalArguments(DirectMessageToClientException e){
        // All the exceptions that will generate the message that will be sent directly to clients
        // will extend 'DirectMessageToClientException'.
        log.error(e.getMessage());
        return e.getMessage();
    }



    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String entityOfIdNotFound(EntityNotFoundException e){
        // Gets rid of the detailed package name from the error message
        
        String message = e.getMessage();
        int findEntityPathIndexStart = message.lastIndexOf("find")+5;
        int findEntityPathIndexEnd = message.indexOf("with")-2;
        String entityPath = message.substring(findEntityPathIndexStart,findEntityPathIndexEnd+1);
        String entityName = entityPath.substring(entityPath.lastIndexOf(".")+1);
        String lowerEntityName = entityName.substring(0,1).toLowerCase() + entityName.substring(1);

        String id= message.substring(message.lastIndexOf(" ")+1);

        return "Unable to find " + lowerEntityName + " with id " + id;
    }


}
