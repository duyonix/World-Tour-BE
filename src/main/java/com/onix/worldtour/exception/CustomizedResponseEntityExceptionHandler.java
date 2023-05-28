package com.onix.worldtour.exception;

import com.onix.worldtour.dto.response.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    public final ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Response<Object> response = Response.argumentNotValid();
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        response.addErrorMsgToResponse(errorMessage, ex);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationException.EntityNotFoundException.class)
    public final ResponseEntity handleNotFountExceptions(Exception ex, WebRequest request) {
        Response<Object> response = Response.notFound();
        response.addErrorMsgToResponse(ex.getMessage(), ex);
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationException.DuplicateEntityException.class)
    public final ResponseEntity handleDuplicateExceptions(Exception ex, WebRequest request) {
        Response<Object> response = Response.duplicateEntity();
        response.addErrorMsgToResponse(ex.getMessage(), ex);
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationException.EntityAlreadyUsedException.class)
    public final ResponseEntity handleEntityAlreadyUsedException(Exception ex, WebRequest request) {
        Response<Object> response = Response.useElsewhere();
        response.addErrorMsgToResponse(ex.getMessage(), ex);
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationException.EntityNotMatchException.class)
    public final ResponseEntity handleEntityNotMatchException(Exception ex, WebRequest request) {
        Response<Object> response = Response.notMatch();
        response.addErrorMsgToResponse(ex.getMessage(), ex);
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationException.DuplicateLevelException.class)
    public final ResponseEntity handleDuplicateLevelException(Exception ex, WebRequest request) {
        Response<Object> response = Response.duplicateLevel();
        response.addErrorMsgToResponse(ex.getMessage(), ex);
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApplicationException.NotSuitableException.class)
    public final ResponseEntity handleNotSuitableException(Exception ex, WebRequest request) {
        Response<Object> response = Response.notSuitable();
        response.addErrorMsgToResponse(ex.getMessage(), ex);
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity handleAllExceptions(Exception ex, WebRequest request) {
        Response<Object> response = Response.exception();
        response.addErrorMsgToResponse(ex.getMessage(), ex);
        return new ResponseEntity(response, HttpStatus.BAD_REQUEST);
    }
}