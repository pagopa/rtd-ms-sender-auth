package it.gov.pagopa.rtd.ms.rtdmssenderauth.telemetry;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.mongo.v3_1.MongoTelemetry;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for AppInsight OpenTelemetry.
 * Connection string will automatically take from environment variable APPLICATIONINSIGHTS_CONNECTION_STRING
 */
@Configuration
@ConditionalOnProperty(value = "applicationinsights.enabled", havingValue = "true", matchIfMissing = false)
public class AppInsightConfig {

  private final AzureMonitorExporterBuilder azureMonitorExporterBuilder;

  public AppInsightConfig() {
    this.azureMonitorExporterBuilder = new AzureMonitorExporterBuilder();
  }

  @Bean
  public SpanExporter azureSpanProcessor() {
    return azureMonitorExporterBuilder.buildTraceExporter();
  }

  @Bean
  public MetricExporter azureMetricExporter() {
    return azureMonitorExporterBuilder.buildMetricExporter();
  }

  @Bean
  public LogRecordExporter azureLogRecordExporter() {
    return azureMonitorExporterBuilder.buildLogRecordExporter();
  }

  @Bean
  MongoClientSettingsBuilderCustomizer mongoOpenTelemetryBridge(
      OpenTelemetry openTelemetry
  ) {
    return clientSettingsBuilder -> clientSettingsBuilder
        .addCommandListener(MongoTelemetry.builder(openTelemetry).build().newCommandListener());
  }
}
