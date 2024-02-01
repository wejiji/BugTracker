package com.example.bugtracker.exception.directmessageconcretes;

import com.example.bugtracker.exception.DirectMessageToClientException;

public class NotExistException extends DirectMessageToClientException {
    public NotExistException() {
        super();
    }

    public NotExistException(String message) {
        super(message);
    }

    public NotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistException(Throwable cause) {
        super(cause);
    }

    protected NotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
