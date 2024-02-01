package com.example.bugtracker.exception.directmessageconcretes;

import com.example.bugtracker.exception.DirectMessageToClientException;

public class ProjectMemberInvalidRoleArgumentException extends DirectMessageToClientException {
    public ProjectMemberInvalidRoleArgumentException() {
        super();
    }

    public ProjectMemberInvalidRoleArgumentException(String message) {
        super(message);
    }

    public ProjectMemberInvalidRoleArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectMemberInvalidRoleArgumentException(Throwable cause) {
        super(cause);
    }

    protected ProjectMemberInvalidRoleArgumentException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
