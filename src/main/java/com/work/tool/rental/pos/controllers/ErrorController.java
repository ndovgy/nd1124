package com.work.tool.rental.pos.controllers;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.work.tool.rental.pos.exceptions.BusinessException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ErrorController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    /**
     * Overrides the handling of request validation errors so that they are more
     * "instructive, user-friendly" as per the requirements
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationError(HttpServletRequest req, Exception ex) throws Exception {
        var errors = ((MethodArgumentNotValidException) ex).getAllErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .collect(Collectors.toList());

        // Keep the entries in insertion order
        var values = new LinkedHashMap<String, Object>();
        values.put("message", "Your request was invalid due to the following errors");
        values.put("validation_errors", errors);

        return ResponseEntity.badRequest().body(objectMapper.writeValueAsString(values));
    }

    /**
     * Handles {@link BusinessExceptions} by getting the response information
     * from the error, processing the error message through localization, and
     * returning it as the response.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<String> handleBusinessError(HttpServletRequest req, Exception ex, Locale locale)
            throws Exception {
        var e = (BusinessException) ex;

        var httpStatus = e.getHttpStatus();
        var code = e.getErrorMsgCode();
        var args = e.getErrorArgs();

        var values = Map.of("message", messageSource.getMessage(code, args, locale));

        return ResponseEntity.status(httpStatus).body(objectMapper.writeValueAsString(values));
    }

}
