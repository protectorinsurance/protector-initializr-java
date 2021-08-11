# protector-initializr-java

## Build

**Normal Build**:  
`gradle clean build`

**Build with system tests**: (Requires Docker)  
`gradle clean build -PsystemTest`  
_Note: systems tests do not execute without the `systemTest` parameter. This is done to cut down on build time_

## Run

Go to Application and run the main method. Intellij should pick it up.

[comment]: # (INITIALIZR:INITIALIZR-DEMO)

# Initializr

## Adapt this project

_(Note: You need Python installed)_

1. Get the source code onto your machine or repo.
2. Run `pip install requests`.
3. In the root folder of this project execute `python init.py`. This script will essentially rename the application,
   namespaces, packages and so forth. It will change the initializr to a usable general project.
4. Delete init.py - it is no longer necessary.
5. In `.github/workflows/gradle.yml` delete the `verify_python_script` job.
6. Verify the application builds with `gradle clean build -PsystemTest`.
7. Delete "Initializr" section from this readme file :)

## Project Overview

### Web

The web packages are specifically for web functionality. There are 2 web packages:

- web: Contains the SpringBoot Application, controllers, configurations and so forth.
- web-test: Contains system test for the web application

### Domain

The "domain" packages exist to be where the main logic of the application exists. This initializr mainly separates
packages based on applications (deployments) and domain. This is why we have a web and (in the future) a kafka package.

The reason we have different domain packages is to allow for different persistence frameworks:

- domain: Contains Spring Data JDBC
- Domain-no-database: Contains no persistence framework
- domain-jpa: Contains Spring Data JPA

The reason we have to do it this way is because there's some strong opinions regarding which framework is more usable,
efficient, etc. At a later point we will also add the ability to select whether Kafka should be included as well.

People can pick which persistence framework they want when executing the `init.py`.

### Flyway

Flyway holds the SQL files when migrating. The reason it is a separate folder is in the scenario that people want to
write applications that consume both kafka and hosts web services. These applications will share a database, and
therefore both should have access to SQL files.

[comment]: # (INITIALIZR:INITIALIZR-DEMO)