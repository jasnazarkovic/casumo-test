# Casumo - Devowelizer tests

This is a small testsuite, demonstration of using TestNG + RestAssured
for automated testing of REST API service.

## Requirements

- Java + Maven
- Docker

## Run test suite

First, spawn docker container with command: 

```commandline
docker run -p 8080:8080 -it casumo/devowelizer:latest
```

and run `mvn test` from command line (assuming that Maven installed globally)
or run it via your IDE and bundled Maven in it. When test suite is executed,
report will be generated in `reports/extent-report.html` file.
