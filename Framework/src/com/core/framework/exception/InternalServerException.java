package com.core.framework.exception;

import org.apache.http.HttpStatus;

/**
 * Created by IntelliJ IDEA.
 * User: tianyanlei
 * Date: 2014 14-3-4
 * Time: 下午2:03
 * To change this template use File | Settings | File Templates
 */
public class InternalServerException extends Exception {
    private static final long serialVersionUID = -2841294936395077461L;

    public int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;

    public InternalServerException() {
    }

    public InternalServerException(int status) {
        this.status = status;
    }

    public InternalServerException(String detailMessage, int status) {
        super(detailMessage);
        this.status = status;
    }

    public InternalServerException(String detailMessage, Throwable throwable, int status) {
        super(detailMessage, throwable);
        this.status = status;
    }

    public InternalServerException(Throwable throwable, int status) {
        super(throwable);
        this.status = status;
    }
}