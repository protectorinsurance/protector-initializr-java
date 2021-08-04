# protector-initializr-java

## Build

**Normal Build**:  
`gradle clean build`

**Build with system tests**: (Requires Docker)  
`gradle clean build -PsystemTest`  
_Note: systems tests do not execute without the `systemTest` parameter.
This is done to cut down on build time_

## Run

Go to Application and run the main method. Intellij should
pick it up.