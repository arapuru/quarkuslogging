package org.acme.logging.model;

public class LogError {
    private String error_code;
    private String error_message;
    private String error_details;
    private boolean retryable;
    private String stackTrace;


    public String getError_code() {
        return error_code;
    }

    public void setError_code(String error_code) {
        this.error_code = error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public String getError_details() {
        return error_details;
    }

    public void setError_details(String error_details) {
        this.error_details = error_details;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }


}
