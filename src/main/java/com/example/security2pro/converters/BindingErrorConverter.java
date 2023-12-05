package com.example.security2pro.converters;

import com.example.security2pro.dto.BindingErrorResponse;
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

    //validation 에러 발생시 작동하는 Advice 클래스.
    // application.properties 에서 설정한 메시지가 출력되도록 하는것 messageSource를 이용해서 가져온다.
    // codes 4가지중 제일 첫번째것을 고르면 되는데
    // 아예 설정한 메시지가 없을경우 디폴트 메시지를 가져온다.
    private final MessageSource messageSource;
    public List<BindingErrorResponse> fieldsToMessages(List<FieldError> fieldErrors) {
        return fieldErrors.stream().map(fieldError -> fieldToMessage(fieldError)).toList();
    }

    private BindingErrorResponse fieldToMessage(FieldError fieldError){
        return Arrays.stream(Objects.requireNonNull(fieldError.getCodes()))
                .map(code-> {
                    return getFieldBindingErrorResponse(fieldError, code); //code -> BindingErrorResponse로 변환
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(new BindingErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()));
    }

    private BindingErrorResponse getFieldBindingErrorResponse(FieldError fieldError, String code) {
        try {
            String message = messageSource.getMessage(
                    code,
                    fieldError.getArguments(),
                    Locale.ENGLISH
            //        LocaleContextHolder.getLocale()
            );
            return new BindingErrorResponse(fieldError.getField(), message); //코드에 상응하는 메시지 있으면 반환
        } catch(NoSuchMessageException e){
            return new BindingErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()); //코드에 상응하는 메시지 없으면 null 반환/ 아님!!!
        }
    }

    public List<BindingErrorResponse> globalsToMessages(List<ObjectError> globalErrors) {
        return globalErrors.stream().map(globalError -> globalToMessage(globalError)).toList();
    }

    private BindingErrorResponse globalToMessage(ObjectError objectError){
        return Arrays.stream(Objects.requireNonNull(objectError.getCodes()))
                .map(code-> {
                    return getGlobalBindingErrorResponse(objectError, code); //code -> BindingErrorResponse로 변환
                })
                .filter(Objects::nonNull)
                .findFirst()
                //처음 찾은 코드를 반환
                .orElse(new BindingErrorResponse(objectError.getObjectName(), objectError.getDefaultMessage()));
    }

    private BindingErrorResponse getGlobalBindingErrorResponse(ObjectError objectError, String code) {
        try {
            String message = messageSource.getMessage(
                    code,
                    objectError.getArguments(),
                    Locale.ENGLISH
                    //LocaleContextHolder.getLocale()
            );
            return new BindingErrorResponse(objectError.getObjectName(), message); //코드에 상응하는 메시지 있으면 반환
        } catch(NoSuchMessageException e){
            return null; //코드에 상응하는 메시지 없으면 null 반환
        }
    }

}
