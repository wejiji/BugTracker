package com.example.bugtracker.exception;

public abstract class DirectMessageToClientException extends RuntimeException{
    public DirectMessageToClientException() {
        super();
    }

    public DirectMessageToClientException(String message) {
        super(message);
    }

    public DirectMessageToClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public DirectMessageToClientException(Throwable cause) {
        super(cause);
    }

    protected DirectMessageToClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
