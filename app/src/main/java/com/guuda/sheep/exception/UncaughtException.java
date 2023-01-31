package com.guuda.sheep.exception;

public class UncaughtException extends Throwable {
    private final Exception exception;

    UncaughtException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }


}
