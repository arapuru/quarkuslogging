package org.acme.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ServiceException extends RuntimeException {
    private String errorCode;
    private String errorMessage;
    private String errorDetails;
    private boolean retryable;
    private String stackTraceString; // Renamed to avoid conflict with getStackTrace() method

    public ServiceException(String errorCode, String errorMessage, String errorDetails, boolean retryable) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
        this.retryable = retryable;
        this.stackTraceString = captureStackTrace(this);
    }

    public ServiceException(String errorCode, String errorMessage, String errorDetails, boolean retryable,
            Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.errorDetails = errorDetails;
        this.retryable = retryable;
        this.stackTraceString = captureStackTrace(cause);
    }

    private String captureStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(String errorDetails) {
        this.errorDetails = errorDetails;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    public String getStackTraceString() {
        return stackTraceString;
    }

    public void setStackTraceString(String stackTraceString) {
        this.stackTraceString = stackTraceString;
    }
}
