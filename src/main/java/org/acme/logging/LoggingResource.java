package org.acme.logging;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.UUID;

@Path("/logging")
public class LoggingResource {

    private static final Logger LOG = Logger.getLogger(LoggingResource.class);

    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public String logInfo() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlation_id", correlationId);



        try {
            LogEvent event = new LogEvent();
            event.setTimestamp(Instant.now());
            event.setLevel("INFO");
            event.setMessage("Request processed successfully");
            event.setCorrelation_id(correlationId);
            event.setService("quarkus-logging-service");
            event.setEnvironment("dev");
            event.setTracepoint("LoggingResource.logInfo");

            LOG.info("Successful request", new Object[] { event });

            return "Logged info with correlation_id: " + correlationId;
        } finally {
            MDC.remove("correlation_id");
        }
    }

    @GET
    @Path("/error")
    @Produces(MediaType.TEXT_PLAIN)
    public String logError() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlation_id", correlationId);

        try {
            // Simulate an exception
            throw new RuntimeException("Simulated error for LogEvent");
        } catch (Exception e) {
            LogEvent event = new LogEvent();
            event.setTimestamp(Instant.now());
            event.setLevel("ERROR");
            event.setMessage("Error processing request");
            event.setCorrelation_id(correlationId);
            event.setService("quarkus-logging-service");
            event.setEnvironment("dev");
            event.setTracepoint("LoggingResource.logError");

            LogError error = new LogError();
            error.setMessage(e.getMessage());
            error.setType(e.getClass().getName());

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            error.setStackTrace(sw.toString());

            event.setError(error);

            LOG.error("An error occurred", new Object[] { event });

            return "Logged error with correlation_id: " + correlationId;
        } finally {
            MDC.remove("correlation_id");
        }
    }

    @GET
    @Path("/interceptor-error")
    @Produces(MediaType.TEXT_PLAIN)
    @LogStructured
    public String logErrorWithInterceptor() {
        // We set correlation ID, the interceptor handles the rest on exception
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlation_id", correlationId);
        try {
            throw new RuntimeException("Simulated error handled by Interceptor");
        } finally {
            MDC.remove("correlation_id");
        }
    }

    @GET
    @Path("/service-exception")
    @Produces(MediaType.TEXT_PLAIN)
    @LogStructured
    public String logServiceException() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlation_id", correlationId);
        try {
            throw new ServiceException("ERR-999", "Business validation failed", "User ID is required", false);
        } finally {
            MDC.remove("correlation_id");
        }
    }
}
