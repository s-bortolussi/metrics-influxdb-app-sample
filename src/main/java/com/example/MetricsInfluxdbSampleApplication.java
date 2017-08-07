package com.example;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
public class MetricsInfluxdbSampleApplication {

    @Value("${influxdb.uri:http://localhost:8086}")
    private String influxdbUri;
    @Value("${influxdb.host:localhost}")
    private String influxdbHost;
    @Value("${influxdb.port:8086}")
    private String influxdbPort;
    @Value("${influxdb.dbname:my-metrics}")
    private String dbName;
    @Value("${influxdb.username:dummy}")
    private String influxdbUsername;
    @Value("${influxdb.password:dummy}")
    private String influxdbPassword;

    @Value("${cloud.application.instance_id:app_01}")
    private String appInstanceId;


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
        String uri = String.format("http://%s:%s/", influxdbHost, influxdbPort);
        InfluxDB influxDB = InfluxDBFactory.connect(uri, influxdbUsername, influxdbPassword);
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
