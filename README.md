# protector-initializr-java

## Build

**Normal Build**:  
`gradle clean build`

**Build with system tests**: (Requires Docker)  
`gradle clean build -PsystemTest`  
_Note: systems tests do not execute without the `systemTest` parameter. This is done to cut down on build time_

## Run

Go to Application and run the main method. Intellij should pick it up.

## Adapt this project

_(Note: You need Python installed)_

1. Get the source code onto your machine or repo.
2. Run `pip install requests`.
3. In the root folder of this project execute `python init.py`. This script will essentially rename the application,
   namespaces, packages and so forth. It will change the initializr to a usable general project.
4. Delete init.py - it is no longer necessary.
5. In `.github/workflows/gradle.yml` delete the `verify_python_script` job.
6. Verify the application builds with `gradle clean build -PsystemTest`.
7. Delete this section in the readme file. You`re all done! Happy coding ;)