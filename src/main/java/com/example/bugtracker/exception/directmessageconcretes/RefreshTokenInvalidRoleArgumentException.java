package com.example.bugtracker.exception.directmessageconcretes;

import com.example.bugtracker.exception.DirectMessageToClientException;

public class RefreshTokenInvalidRoleArgumentException extends DirectMessageToClientException {
    // Thrown when invalid roles are passed for 'RefreshTokenData' constructor arguments


    public RefreshTokenInvalidRoleArgumentException() {
        super();
    }

    public RefreshTokenInvalidRoleArgumentException(String message) {
        super(message);
    }

    public RefreshTokenInvalidRoleArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public RefreshTokenInvalidRoleArgumentException(Throwable cause) {
        super(cause);
    }

    protected RefreshTokenInvalidRoleArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
