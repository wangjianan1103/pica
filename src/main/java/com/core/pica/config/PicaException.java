package com.core.pica.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PicaException extends RuntimeException {
    private int status;
    private String message;

    public PicaException(int status) {
        this.status = status;
    }

    public PicaException(int status, String message) {
        this.status = status;
        this.message = message;
    }
}



