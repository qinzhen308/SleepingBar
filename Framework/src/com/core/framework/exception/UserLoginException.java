package com.core.framework.exception;

import org.apache.http.HttpStatus;

/**
 * Created with IntelliJ IDEA.
 * User: mark
 * Date: 14-11-11
 * Time: 下午3:58
 * To change this template use File | Settings | File Templates.
 */
public class UserLoginException extends Exception {
    private static final long serialVersionUID = -2841294936395077461L;

    public int status = HttpStatus.SC_INTERNAL_SERVER_ERROR;

    public UserLoginException() {
    }

    public UserLoginException(int status) {
        this.status = status;
    }

    public UserLoginException(String detailMessage, int status) {
        super(detailMessage);
        this.status = status;
    }

    public UserLoginException(String detailMessage, Throwable throwable, int status) {
        super(detailMessage, throwable);
        this.status = status;
    }

    public UserLoginException(Throwable throwable, int status) {
        super(throwable);
        this.status = status;
    }
}
