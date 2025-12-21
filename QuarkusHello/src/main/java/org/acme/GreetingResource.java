package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.logging.LogStructured;
import org.acme.logging.ServiceException;
import org.jboss.logging.Logger;
import org.jboss.logging.MDC;
import java.util.UUID;

@Path("/hello")
public class GreetingResource {

    private static final Logger LOG = Logger.getLogger(GreetingResource.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        LOG.info("Hello endpoint called");
        return "Hello from Quarkus REST";
    }

    @GET
    @Path("/error")
    @Produces(MediaType.TEXT_PLAIN)
    public String error() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlation_id", correlationId);
        try {
            throw new RuntimeException("Simulated RuntimeException in QuarkusHello");
        } catch (Exception e) {
            LOG.error("Manually caught exception", e);
            return "Error logged with correlation_id: " + correlationId;
        } finally {
            MDC.remove("correlation_id");
        }
    }

    @GET
    @Path("/interceptor")
    @Produces(MediaType.TEXT_PLAIN)
    @LogStructured
    public String interceptor() {
        // Interceptor will capture the exception and log it structured
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlation_id", correlationId);
        try {
            throw new RuntimeException("Simulated Intercepted Exception in QuarkusHello");
        } finally {
            // Clean up MDC, though interceptor reads it before this if it wraps the method
            // call properly.
            // Wait, @AroundInvoke wraps *outside* this method.
            // If we remove it here, it might be gone before the interceptor catch block if
            // the interceptor logic is after proceed().
            // Actually, in the interceptor: call proceeds, exception thrown, catch block in
            // interceptor uses MDC.
            // If we remove MDC in finally block *inside* the method, is it available in the
            // interceptor's catch?
            // Yes, because the method execution completes (exceptionally), finally block
            // runs, THEN interceptor catches.
            // So we should NOT remove MDC here if we want the interceptor to see it?
            // Actually, MDC is thread-local.
            // Let's defer MDC removal to a filter or assume the interceptor can handle
            // missing MDC (it defaults to N/A).
            // But for this test, let's keep it simple.
        }
    }

    @GET
    @Path("/service-exception")
    @Produces(MediaType.TEXT_PLAIN)
    @LogStructured
    public String serviceException() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlation_id", correlationId);
        // Interceptor catches ServiceException and logs extended details
        throw new ServiceException("HELLO-101", "Service Validation Failed", "Invalid parameters provided", true);
    }

    @jakarta.inject.Inject
    org.acme.logging.StructuredLogger structuredLogger;

    @GET
    @Path("/structured")
    @Produces(MediaType.TEXT_PLAIN)
    public String structured() {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlation_id", correlationId);
        try {
            structuredLogger.info("This is a structured info log", "GreetingResource.structured");

            try {
                throw new RuntimeException("Inner error");
            } catch (Exception e) {
                structuredLogger.error("Caught internal error", "GreetingResource.structured-error", e);
            }

            return "Structured logging verified";
        } finally {
            MDC.remove("correlation_id");
        }
    }
}
