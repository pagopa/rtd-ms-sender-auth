package it.gov.pagopa.rtd.ms.rtdmssenderauth.telemetry;

import com.azure.monitor.opentelemetry.exporter.AzureMonitorExporterBuilder;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.mongo.v3_1.MongoTelemetry;
import io.opentelemetry.sdk.logs.export.LogRecordExporter;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "applicationinsights.enabled", matchIfMissing = false)
public class AppInsightConfig {

  private final AzureMonitorExporterBuilder azureMonitorExporterBuilder;

  public AppInsightConfig(
      @Value("${applicationinsights.connectionstring}") String appInsightConnectionString
  ) {
    this.azureMonitorExporterBuilder = new AzureMonitorExporterBuilder()
        .connectionString(appInsightConnectionString);
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
    return (clientSettingsBuilder) -> clientSettingsBuilder.addCommandListener(MongoTelemetry.builder(openTelemetry).build().newCommandListener());
  }
}
