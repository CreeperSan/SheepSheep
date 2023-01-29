package com.littletree.mysunsheep.exception;

public class UncaughtException extends Throwable {
    private final Exception exception;

    UncaughtException(Exception exception) {
        this.exception = exception;
    }

    public Exception getException() {
        return exception;
    }


}
