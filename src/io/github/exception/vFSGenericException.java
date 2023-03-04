package io.github.exception;

import io.github.constants.Constants;

public class vFSGenericException extends Exception {
    public vFSGenericException() {
        super();
    }

    public vFSGenericException(String message) {
        super(Constants.versionCode + message);
    }

    public vFSGenericException(String message, Throwable cause) {
        super(Constants.versionCode + message, cause);
    }

    public vFSGenericException(Throwable cause) {
        super(cause);
    }
}