package org.acme.logging;

import io.quarkiverse.loggingjson.JsonGenerator;
import io.quarkiverse.loggingjson.JsonProvider;
import jakarta.inject.Singleton;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logmanager.ExtLogRecord;

import java.io.IOException;

@Singleton
public class MetadataJsonProvider implements JsonProvider {

    @ConfigProperty(name = "quarkus.application.version", defaultValue = "1.0.0-SNAPSHOT")
    String serviceVersion;

    @ConfigProperty(name = "quarkus.profile", defaultValue = "dev")
    String environment;

    @Override
    public void writeTo(JsonGenerator generator, ExtLogRecord event) throws IOException {
        generator.writeStringField("service_version", serviceVersion);
        generator.writeStringField("environment", environment);

        String correlationId = event.getMdc("correlation_id");
        if (correlationId == null) {
            correlationId = "N/A";
        }
        generator.writeStringField("correlation_id", correlationId);
    }
}
