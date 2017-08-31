package com.example;

import org.influxdb.InfluxDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.ExportMetricWriter;
import org.springframework.boot.actuate.endpoint.MetricsEndpoint;
import org.springframework.boot.actuate.endpoint.MetricsEndpointMetricReader;
import org.springframework.boot.actuate.metrics.jmx.JmxMetricWriter;
import org.springframework.boot.actuate.metrics.writer.GaugeWriter;
import org.springframework.boot.actuate.metrics.writer.MetricWriter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class MetricsInfluxdbSampleApplication {

    @Value("${influxdb.dbname:my-metrics}")
    private String dbName;

    @Value("${cloud.application.instance_id:app_01}")
    private String appInstanceId;

    @Autowired
    private InfluxDB influxDB;


    public static void main(String[] args) {
        SpringApplication.run(MetricsInfluxdbSampleApplication.class, args);
    }

    @Bean
    @ExportMetricWriter
    MetricWriter metricWriter(MBeanExporter exporter) {
        return new JmxMetricWriter(exporter);
    }

    @Bean
    @ExportMetricWriter
    GaugeWriter influxMetricsWriter() {
        // the name of the datastore you choose
        influxDB.createDatabase(dbName);
        InfluxDBMetricWriter.Builder builder = new InfluxDBMetricWriter.Builder(influxDB);
        builder.appInstanceId(appInstanceId);
        builder.databaseName(dbName);
        builder.batchActions(500);    // number of points for batch before data is sent to Influx
        return builder.build();
    }

    @Bean
    public MetricsEndpointMetricReader metricsEndpointMetricReader(MetricsEndpoint metricsEndpoint) {
        return new MetricsEndpointMetricReader(metricsEndpoint);
    }
}

@RestController
class GreetingController {

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "world !") String name) {
        return String.format("hello %s", name);
    }

}
