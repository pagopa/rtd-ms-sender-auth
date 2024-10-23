package it.gov.pagopa.rtd.ms.rtdmssenderauth.telemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.mongo.v3_1.MongoTelemetry;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoInstrumentation {
    @Bean
    MongoClientSettingsBuilderCustomizer mongoOpenTelemetryBridge(
            OpenTelemetry openTelemetry
    ) {
        return clientSettingsBuilder -> clientSettingsBuilder
                .addCommandListener(MongoTelemetry.builder(openTelemetry).build().newCommandListener());
    }
}
