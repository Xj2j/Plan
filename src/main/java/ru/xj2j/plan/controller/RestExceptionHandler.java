package ru.xj2j.plan.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.xj2j.plan.dto.Response;
import ru.xj2j.plan.exception.*;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({MyEntityNotFoundException.class, EntityNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFoundEx(RuntimeException ex, WebRequest request) {
        Response response = new Response(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MyEntityAlreadyExistsException.class})
    protected ResponseEntity<Object> handleEntityConflictEx(MyEntityAlreadyExistsException ex, WebRequest request) {
        Response response = new Response(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({CustomBadRequestException.class, HttpClientErrorException.BadRequest.class})
    protected ResponseEntity<Object> handleEntityBadRequestEx(CustomBadRequestException ex, WebRequest request) {
        Response response = new Response(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({CustomForbiddenException.class, HttpClientErrorException.Forbidden.class})
    protected ResponseEntity<Object> handleEntityForbiddenEx(CustomForbiddenException ex, WebRequest request) {
        Response response = new Response(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = new ApiError("Malformed JSON Request", ex.getMessage());
        return new ResponseEntity(apiError, status);
    }

    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ApiError apiError = new ApiError("Method Argument Not Valid", ex.getMessage(), errors);
        return new ResponseEntity<>(apiError, status);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,HttpStatus status,
                                                                      WebRequest request) {
        ApiError apiError = new ApiError();
        apiError.setMessage(String.format("The parameter '%s' of value '%s' could not be converted to type '%s'",
                ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName()));
        apiError.setDebugMessage(ex.getMessage());
        return new ResponseEntity<>(apiError, status);
    }

}
