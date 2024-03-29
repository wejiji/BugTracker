package com.example.bugtracker.exception.directmessageconcretes;

import com.example.bugtracker.exception.DirectMessageToClientException;

public class InvalidIssueRelationException extends DirectMessageToClientException {

    public InvalidIssueRelationException() {
        super();
    }

    public InvalidIssueRelationException(String message) {
        super(message);
    }

    public InvalidIssueRelationException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidIssueRelationException(Throwable cause) {
        super(cause);
    }

    protected InvalidIssueRelationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
