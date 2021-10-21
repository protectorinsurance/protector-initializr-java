[![Build Main](https://github.com/protectorinsurance/protector-initializr-java/actions/workflows/gradle-main.yml/badge.svg)](https://github.com/protectorinsurance/protector-initializr-java/actions/workflows/gradle-main.yml)

# protector-initializr-java

Protector Initializr is a template and project configurator for new Java applications at Protector.

The goal is to provide a sensible and opinionated default on which new systems can be built upon while removing a lot of yak shaving. 

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
- [ ] SonarCloud integration
- [ ] Contract-first development (with OpenApi/Swagger)

### Getting started

[Step-by-step guide can be found here](https://github.com/protectorinsurance/protector-initializr-java/wiki/Getting-started)

### Documentation

[Documentation can be found here](https://github.com/protectorinsurance/protector-initializr-java/wiki)

[comment]: # (INITIALIZR:INITIALIZR-DEMO)

