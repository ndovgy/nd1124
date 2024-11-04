package com.work.tool.rental.pos.exceptions;

import org.springframework.http.HttpStatus;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class NoSuchToolCodeException extends BusinessException {

    private String toolCode;

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String getErrorMsgCode() {
        return "validation.toolCode.noSuchFound";
    }

    @Override
    public Object[] getErrorArgs() {
        return new Object[] { toolCode };
    }

}
