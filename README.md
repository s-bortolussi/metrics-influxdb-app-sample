# metrics-influxdb-app-sample

A very basic spring boot app to push metrics on influxdb

Implements a custom [Spring Boot Actuator metric writer](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-metrics.html#production-ready-metric-writers) to collect and push metrics to an [influxdb](https://github.com/influxdata/influxdb) timeseries database.
Metric writer implementation is fully copied from this [blog post](http://www.jiffle.net/blog/2016/11/24/spring-boot-metrics-with-influxdb/).


