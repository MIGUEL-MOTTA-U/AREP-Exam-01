package org.example.framework.errors;

public class FrameworkError extends Error {
    private final int code;
    public FrameworkError(String message, int code) {
        super(message);
        this.code = code;
    }
    public int getCode() {
        return this.code;
    }
}
