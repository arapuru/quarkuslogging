package org.acme.logging.model;

import java.time.Instant;
import java.util.Map;

public class LogEvent {
    private Instant timestamp;
    private String level;
    private String message;
    private String correlation_id;
    private String environment;
    private String service;
    private String tracepoint;
    private Map<?, ?> details;
    private String payload;
    private LogError error;
    private LogMetrics metrics;

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCorrelation_id() {
        return correlation_id;
    }

    public void setCorrelation_id(String correlation_id) {
        this.correlation_id = correlation_id;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getTracepoint() {
        return tracepoint;
    }

    public void setTracepoint(String tracepoint) {
        this.tracepoint = tracepoint;
    }

    public Map<?, ?> getDetails() {
        return details;
    }

    public void setDetails(Map<?, ?> details) {
        this.details = details;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LogError getError() {
        return error;
    }

    public void setError(LogError error) {
        this.error = error;
    }

    public LogMetrics getMetrics() {
        return metrics;
    }

    public void setMetrics(LogMetrics metrics) {
        this.metrics = metrics;
    }
}
