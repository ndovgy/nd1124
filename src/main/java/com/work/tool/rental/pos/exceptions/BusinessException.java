package com.work.tool.rental.pos.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Sub-classes are exceptions with custom handling code that call the
 * {@link #getErrorMsgCode()} and {@link #getErrorArgs()} methods, runs them
 * through the Spring localization feature, and returns the localized message as
 * the error response.
 */
public abstract class BusinessException extends RuntimeException {

    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    public abstract String getErrorMsgCode();

    public Object[] getErrorArgs() {
        return new Object[0];
    }

}
