package com.example.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler 
{

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
    				ResourceNotFoundException ex, 
    				HttpServletRequest request) 
    {
        return build(HttpStatus.NOT_FOUND, 
        				ex.getMessage(), 
        				request, null);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(
    							DuplicateResourceException ex, 
    							HttpServletRequest request) 
    {
        return build(HttpStatus.CONFLICT, 
        				ex.getMessage(), 
        				request, null);
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
    										BusinessRuleException ex, 
    										HttpServletRequest request) 
    {
        return build(HttpStatus.BAD_REQUEST, 
        				ex.getMessage(), 
        				request, null);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
    										InvalidCredentialsException ex,
    										HttpServletRequest request) 
    {
        return build(HttpStatus.UNAUTHORIZED, 
        				ex.getMessage(), 
        				request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
    										MethodArgumentNotValidException ex,
    										HttpServletRequest request) 
    {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
                				errors.putIfAbsent(
                				error.getField(), 
                				error.getDefaultMessage()));
        
        return build(HttpStatus.BAD_REQUEST, 
        				"Validation failed", 
        				request, errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(
    										HttpMessageNotReadableException ex,
    										HttpServletRequest request) 
    {
        return build(HttpStatus.BAD_REQUEST, 
        				"Malformed JSON or invalid value in request body", 
        				request, 
        				null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
    										MethodArgumentTypeMismatchException ex,
    										HttpServletRequest request) 
    {
        String message = "Invalid value for parameter '" + ex.getName() + "'";
        return build(HttpStatus.BAD_REQUEST, 
        				message, 
        				request, 
        				null);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
    										DataIntegrityViolationException ex,
    										HttpServletRequest request) 
    {
        log.error("Data integrity violation", ex);
        return build(HttpStatus.CONFLICT, 
        				"Operation violates a data constraint", 
        				request, 
        				null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(
    										NoResourceFoundException ex, 
    										HttpServletRequest request) 
    {
        return build(HttpStatus.NOT_FOUND,
                		"No endpoint found for " + 
                		request.getMethod() + " " + 
                		request.getRequestURI(), 
                		request, null);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
    										HttpRequestMethodNotSupportedException ex,
    										HttpServletRequest request) 
    {
        return build(HttpStatus.METHOD_NOT_ALLOWED,
                		"Method " + ex.getMethod() + 
                		" is not supported for this URL", 
                		request, 
                		null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
    										MissingServletRequestParameterException ex,
    										HttpServletRequest request) 
    {
        return build(HttpStatus.BAD_REQUEST,
                "Required parameter '" + ex.getParameterName() + "' is missing", request, null);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaType(
    						HttpMediaTypeNotSupportedException ex,
    						HttpServletRequest request) 
    {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "Content type is not supported. Send the body as application/json", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) 
    {
        log.error("Unexpected error at {}", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, 
        				"Something went wrong. Please try again later.", 
        				request, 
        				null);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request,
            Map<String, String> validationErrors) 
    {
        ErrorResponse body = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .validationErrors(validationErrors)
                .build();
        return ResponseEntity.status(status).body(body);
    }
}
