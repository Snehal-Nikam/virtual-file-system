package io.github.exception;

import io.github.constants.Constants;

public class vFSGenericException extends Exception {
    public vFSGenericException() {
        super();
    }

    public vFSGenericException(String message) {
        super(Constants.version + message);
    }

    public vFSGenericException(String message, Throwable cause) {
        super(Constants.version + message, cause);
    }

    public vFSGenericException(Throwable cause) {
        super(cause);
    }
}