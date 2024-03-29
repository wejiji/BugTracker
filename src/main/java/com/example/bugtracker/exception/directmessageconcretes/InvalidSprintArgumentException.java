package com.example.bugtracker.exception.directmessageconcretes;

import com.example.bugtracker.exception.DirectMessageToClientException;

public class InvalidSprintArgumentException extends DirectMessageToClientException {

    public InvalidSprintArgumentException() {
        super();
    }

    public InvalidSprintArgumentException(String message) {
        super(message);
    }

    public InvalidSprintArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidSprintArgumentException(Throwable cause) {
        super(cause);
    }

    protected InvalidSprintArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
