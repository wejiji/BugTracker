package com.example.bugtracker.exception.directmessageconcretes;

import com.example.bugtracker.exception.DirectMessageToClientException;

public class InvalidUserArgumentException extends DirectMessageToClientException {

    public InvalidUserArgumentException() {
        super();
    }

    public InvalidUserArgumentException(String message) {
        super(message);
    }

    public InvalidUserArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUserArgumentException(Throwable cause) {
        super(cause);
    }

    protected InvalidUserArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
