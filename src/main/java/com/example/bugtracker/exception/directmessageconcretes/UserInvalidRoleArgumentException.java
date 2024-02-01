package com.example.bugtracker.exception.directmessageconcretes;

import com.example.bugtracker.exception.DirectMessageToClientException;

public class UserInvalidRoleArgumentException extends DirectMessageToClientException {

    public UserInvalidRoleArgumentException() {
        super();
    }

    public UserInvalidRoleArgumentException(String message) {
        super(message);
    }

    public UserInvalidRoleArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserInvalidRoleArgumentException(Throwable cause) {
        super(cause);
    }

    protected UserInvalidRoleArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
