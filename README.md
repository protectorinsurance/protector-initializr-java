[comment]: # (INITIALIZR:INITIALIZR-DEMO)

[![Build Main](https://github.com/protectorinsurance/protector-initializr-java/actions/workflows/gradle-main.yml/badge.svg)](https://github.com/protectorinsurance/protector-initializr-java/actions/workflows/gradle-main.yml)

[comment]: # (INITIALIZR:INITIALIZR-DEMO)

# protector-initializr-java

[comment]: # (INITIALIZR:INITIALIZR-DEMO)

Protector Initializr is a template and project configurator for new Java applications at Protector.

The goal is to provide a sensible and opinionated default on which new systems can be built upon while removing a lot of yak shaving. 

[comment]: # (INITIALIZR:INITIALIZR-DEMO)

### Build

**Normal Build**:  
`gradle clean build`

**Build with system tests**: (Requires Docker)  
`gradle clean build -PsystemTest`  
_Note: systems tests do not execute without the `systemTest` parameter. This is done to cut down on build time_

### Run

Go to Application and run the main method. Intellij should pick it up.

[comment]: # (INITIALIZR:INITIALIZR-DEMO)

# Initializr

![](initializr-script-demo.gif)

### Features

- [x] Standard GitHub Actions Workflow
- [x] Web services
- [x] Optional database support (JDBC or JPA)
- [x] Flyway
- [x] Configuration & dependencies for producing Kafka messages using Avro schemas
- [x] Logging & APM
- [x] System tests
- [x] Automatic pull requests for new dependency versions
- [x] Automatic pull requests for new gradle versions
- [x] Kafka consumer
- [x] SonarCloud integration
- [ ] Contract-first development (with OpenApi/Swagger)

### Getting started

[Step-by-step guide can be found here](https://github.com/protectorinsurance/protector-initializr-java/wiki/Getting-started)

### Documentation

[Documentation can be found here](https://github.com/protectorinsurance/protector-initializr-java/wiki)

[comment]: # (INITIALIZR:INITIALIZR-DEMO)

## Operational information

__[TODO]__: Once the system has been put in to production, make sure to
include information about where to reach the service and other relevant
information.

* Service name: `protector-initializr`
* Internal ports: 8080
* DNS:
  * __[TODO]__
* Internal URL:
  * __[TODO]__
* External URL:
  * __[TODO]__
* Health checks:
  * Defined in Dockerfile: `curl --fail http://localhost:8391/actuator/health || exit 1`
* Environment variables: `SPRING_PROFILES_ACTIVE=prod`
* Replicas: 2 or more
