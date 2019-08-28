package com.gyhqq.common.Exception;

import com.gyhqq.common.enums.ExceptionEnum;
import lombok.Getter;

@Getter
public class GyhException extends RuntimeException {

    private int status;

    public GyhException(ExceptionEnum em) {
        super(em.getMessage());
        this.status = em.getStatus();
    }

    public GyhException(ExceptionEnum em, Throwable cause) {
        super(em.getMessage(), cause);
        this.status = em.getStatus();
    }

    public GyhException(int status, String allErrorMsg) {
        super(allErrorMsg);
        this.status = status;
    }
}
