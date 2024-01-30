package com.example.security2pro.exception.directmessageconcretes;

import com.example.security2pro.exception.DirectMessageToClientException;

public class InvalidIssueArgumentException extends DirectMessageToClientException {
    public InvalidIssueArgumentException() {
        super();
    }

    public InvalidIssueArgumentException(String message) {
        super(message);
    }

    public InvalidIssueArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidIssueArgumentException(Throwable cause) {
        super(cause);
    }

    protected InvalidIssueArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
