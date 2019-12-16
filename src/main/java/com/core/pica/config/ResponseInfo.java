package com.core.pica.config;

import com.core.pica.enums.ErrorCodeEnum;
import lombok.Data;

@Data
public class ResponseInfo {

    private int status;
    private String message;
    private Object content;

    public ResponseInfo() {
    }

    public ResponseInfo(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ResponseInfo(int status, String message, Object content) {
        this.status = status;
        this.message = message;
        this.content = content;
    }

    public ResponseInfo(ErrorCodeEnum errorCodeEnum, Object content) {
        this.status = errorCodeEnum.getErrorCode();
        this.message = errorCodeEnum.getMessage();
        this.content = content;
    }
}

