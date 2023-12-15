package com.example.security2pro.converters;



import com.example.security2pro.controller.*;
import com.example.security2pro.dto.error.BindingErrorResponse;
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
@RestControllerAdvice(assignableTypes = {AuthController.class, IssueController.class,  ProjectController.class, SprintController.class, UserController.class}) //여기 잘못하면 적용 안되므로 주의할것
public class ErrorControllerAdvice {

    private final BindingErrorConverter bindingErrorConverter;

    //Exception을 새로 만들어서 활용할수도 있지만 그냥 단순 오버라이딩은 안되고(autogeneration)
    //getFieldErrors 같은 메소드는 bindingResult 에 있는것이므로
    //bindingResult를 받아서 exception을 생성해야 하는것.
    //이미 스프링은 그러한 기능을 제공하는 exception을 만들어두었다.
    //그것은 BindException 클래스다.
    //getFieldError 등등 유용한 메소드가 많음!

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<BindingErrorResponse> validationErrorHandle(BindException e){
        List<BindingErrorResponse> bindingFieldErrorResponse = bindingErrorConverter.fieldsToMessages(e.getFieldErrors());
        List<BindingErrorResponse> bindingGlobalErrorResponse = bindingErrorConverter.globalsToMessages(e.getGlobalErrors());
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

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String notFound(IllegalArgumentException e){
        log.error(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String entityOfIdNotFound(EntityNotFoundException e){
        //gets rid of the detailed package name- from the error message
        
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
