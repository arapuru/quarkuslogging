package org.acme.logging;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;

@LogStructured
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class LogStructuredInterceptor {

    private static final Logger LOG = Logger.getLogger(LogStructuredInterceptor.class);

    @ConfigProperty(name = "quarkus.profile", defaultValue = "dev")
    String environment;

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "quarkus-logging-service")
    String serviceName;

    @AroundInvoke
    public Object logEvent(InvocationContext context) throws Exception {
        try {
            return context.proceed();
        } catch (Exception e) {
            Object correlationIdObj = MDC.get("correlation_id");
            String correlationId = correlationIdObj != null ? correlationIdObj.toString() : "N/A";

            LogEvent event = new LogEvent();
            event.setTimestamp(Instant.now());
            event.setLevel("ERROR");
            event.setMessage("Error in method: " + context.getMethod().getName());
            event.setCorrelation_id(correlationId);
            event.setService(serviceName);
            event.setEnvironment(environment);
            event.setTracepoint(context.getTarget().getClass().getName() + "." + context.getMethod().getName());

            LogError error = new LogError();
            error.setMessage(e.getMessage());
            error.setType(e.getClass().getName());

            if (e instanceof ServiceException) {
                ServiceException se = (ServiceException) e;
                error.setErrorCode(se.getErrorCode());
                error.setErrorDetails(se.getErrorDetails());
                error.setRetryable(se.isRetryable());
            }

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            error.setStackTrace(sw.toString());

            event.setError(error);

            // Log using the object array signature for structured logging
            LOG.errorv("Operation failed", new Object[] { event });

            throw e;
        }
    }
}
