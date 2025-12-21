package org.acme.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Map;

@Singleton
public class StructuredLogger {

    private static final Logger CONSOLE_LOG = Logger.getLogger("CONSOLE");
    private static final Logger FILE_LOG = Logger.getLogger("FILE");

    private final ObjectMapper objectMapper;

    public StructuredLogger() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @ConfigProperty(name = "quarkus.profile", defaultValue = "dev")
    String environment;

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "quarkus-logging-service")
    String serviceName;

    @ConfigProperty(name = "structured.logging.console.enabled", defaultValue = "true")
    boolean consoleEnabled;

    @ConfigProperty(name = "structured.logging.file.enabled", defaultValue = "true")
    boolean fileEnabled;

    public void info(String message) {
        log("INFO", message, null, null, null, null);
    }

    public void info(String message, String tracepoint) {
        log("INFO", message, tracepoint, null, null, null);
    }

    public void info(String message, Map<String, Object> details) {
        log("INFO", message, null, details, null, null);
    }

    public void info(String message, String tracepoint, Map<String, Object> details) {
        log("INFO", message, tracepoint, details, null, null);
    }

    public void warn(String message) {
        log("WARN", message, null, null, null, null);
    }

    public void error(String message) {
        log("ERROR", message, null, null, null, null);
    }

    public void error(String message, Throwable t) {
        log("ERROR", message, null, null, null, t);
    }

    public void error(String message, String tracepoint, Throwable t) {
        log("ERROR", message, tracepoint, null, null, t);
    }

    public void error(String message, String tracepoint, Map<String, Object> details, Throwable t) {
        log("ERROR", message, tracepoint, details, null, t);
    }

    private void log(String level, String message, String tracepoint, Map<?, ?> details, String payload, Throwable t) {
        Object correlationIdObj = MDC.get("correlation_id");
        String correlationId = correlationIdObj != null ? correlationIdObj.toString() : "N/A";

        LogEvent event = new LogEvent();
        event.setTimestamp(Instant.now());
        event.setLevel(level);
        event.setMessage(message);
        event.setCorrelation_id(correlationId);
        event.setService(serviceName);
        event.setEnvironment(environment);
        event.setTracepoint(tracepoint);
        event.setDetails(details);
        event.setPayload(payload);

        if (t != null) {
            LogError logError = new LogError();
            logError.setMessage(t.getMessage());
            logError.setType(t.getClass().getName());

            if (t instanceof ServiceException) {
                ServiceException se = (ServiceException) t;
                logError.setErrorCode(se.getErrorCode());
                logError.setErrorDetails(se.getErrorDetails());
                logError.setRetryable(se.isRetryable());
            }

            // Capture stack trace
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            logError.setStackTrace(sw.toString());

            event.setError(logError);
        }

        if (!consoleEnabled && !fileEnabled) {
            return;
        }

        try {
            String json = objectMapper.writeValueAsString(event);

            if (consoleEnabled) {
                logToTarget(CONSOLE_LOG, level, json);
            }

            if (fileEnabled) {
                logToTarget(FILE_LOG, level, json);
            }
        } catch (Exception e) {
            // Fallback in case of serialization error
            Logger.getLogger(StructuredLogger.class).error("Failed to serialize LogEvent", e);
        }
    }

    private void logToTarget(Logger logger, String level, String message) {
        switch (level) {
            case "INFO":
                logger.info(message);
                break;
            case "WARN":
                logger.warn(message);
                break;
            case "ERROR":
                logger.error(message);
                break;
            case "DEBUG":
                logger.debug(message);
                break;
            default:
                logger.info(message);
        }
    }
}
