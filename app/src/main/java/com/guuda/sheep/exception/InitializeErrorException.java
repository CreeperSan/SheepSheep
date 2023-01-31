package com.guuda.sheep.exception;

import androidx.annotation.Nullable;

public class InitializeErrorException extends Throwable{
    private final Throwable originalException;

    public InitializeErrorException(String message, @Nullable Throwable originalException) {
        super(message);
        this.originalException = originalException;
    }

    public Throwable getOriginalException() {
        return originalException;
    }
}
