package com.example.security2pro.exception.directmessageconcretes;

import com.example.security2pro.exception.DirectMessageToClientException;

public class InvalidSprintDateException extends DirectMessageToClientException {
    public InvalidSprintDateException() {
        super();
    }

    public InvalidSprintDateException(String message) {
        super(message);
    }

    public InvalidSprintDateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSprintDateException(Throwable cause) {
        super(cause);
    }

    protected InvalidSprintDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
