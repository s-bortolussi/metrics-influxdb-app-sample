package com.example;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.actuate.metrics.writer.GaugeWriter;

import java.util.concurrent.TimeUnit;

/*
duplicated from http://www.jiffle.net/blog/2016/11/24/spring-boot-metrics-with-influxdb/
 */
public class InfluxDBMetricWriter implements GaugeWriter {

    private static final String DEFAULT_DATABASE_NAME = "metrics";
    private static final int DEFAULT_BATCH_ACTIONS = 500;
    private static final int DEFAULT_FLUSH_DURATION = 30;

    private final InfluxDB influxDB;
    //private final String databaseName;
    private String appInstanceId;

    private InfluxDBMetricWriter(Builder builder) {
        this.influxDB = builder.influxDB;
        //this.databaseName = builder.databaseName;
        this.appInstanceId = builder.appInstanceId;
        //this.influxDB.createDatabase(this.databaseName);
        this.influxDB.enableBatch(builder.batchActions, builder.flushDuration,
                builder.flushDurationTimeUnit);
        this.influxDB.setLogLevel(builder.logLevel);
    }

    @Override
    public void set(Metric<?> value) {
        Point point = Point.measurement(value.getName())
                .time(value.getTimestamp().getTime(), TimeUnit.MILLISECONDS)
                .tag("app_instance_id",this.appInstanceId)
                .addField("value", value.getValue())
                .build();
        this.influxDB.write(point);
    }

    @Accessors(fluent = true, chain = true)
    @RequiredArgsConstructor
    public static class Builder {
        @NonNull
        private final InfluxDB influxDB;
        @Setter
        private String databaseName = DEFAULT_DATABASE_NAME;
        @Setter
        private int batchActions = DEFAULT_BATCH_ACTIONS;
        private int flushDuration = DEFAULT_FLUSH_DURATION;
        private TimeUnit flushDurationTimeUnit = TimeUnit.SECONDS;
        @Setter
        private InfluxDB.LogLevel logLevel = InfluxDB.LogLevel.BASIC;
        @Setter
        private String appInstanceId;

        public Builder flushDuration(int flushDuration, TimeUnit flushDurationTimeUnit) {
            this.flushDuration = flushDuration;
            this.flushDurationTimeUnit = flushDurationTimeUnit;
            return this;
        }

        public InfluxDBMetricWriter build() {
            return new InfluxDBMetricWriter(this);
        }

    }
}