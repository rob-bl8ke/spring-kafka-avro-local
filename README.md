
## Usage

```bash
mvn clean install
mvn -pl app-main spring-boot:run
```

## Overview
In Java (Spring Boot world), the equivalent of having a multi-project C# .NET solution is to have multiple Maven (or Gradle) modules inside a single parent project.

Each module = like a "project" (a buildable JAR or library) that you can reference from your main Spring Boot app, and you can debug into them seamlessly.

- Each module has its own pom.xml describing its dependencies..
- In spring-boot-solution/pom.xml, you declare it as a packaging type pom and list all submodules:
- In app-main/pom.xml, you can reference domain-logic and shared-utils:

```xml
<dependencies>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>domain-logic</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <dependency>
        <groupId>com.example</groupId>
        <artifactId>shared-utils</artifactId>
        <version>1.0.0-SNAPSHOT</version>
    </dependency>
    <!-- plus your other Spring Boot dependencies -->
</dependencies>
```
- Your library modules (domain-logic, shared-utils) should NOT have spring-boot-starter-parent in their POMs — they are just plain Java libraries (unless you want them to be mini-Spring modules, but that's advanced).
- The parent project itself (spring-boot-multimodule-quickstart) doesn't generate any code; it's just there to group everything together.

Debugging your entire solution across modules is not possible. In Visual Studio Code set up your launch.json file like this:

```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Debug Spring Boot App",
            "request": "launch",
            "mainClass": "com.example.app.Application",
            "projectName": "app-main",
            "cwd": "${workspaceFolder}/app-main",
            "args": "" ,
            // "vmArgs": "-Dspring.profiles.active=dev",
            // "console": "integratedTerminal"
        }
    ]
}
```
- You can set breakpoints inside domain-logic or shared-utils.
- Step into their source code exactly like you do when stepping into a C# library project.

## Tips

- If you're using IntelliJ IDEA, do File > New > Project from Existing Sources > Maven, and open the root pom.xml. It will understand multi-modules automatically.
- If you want to make shared-utils a non-Spring module (pure Java, no @Component/@Service), that's totally fine — it's just a utility library.
- You can easily add more modules by duplicating the pattern!

## Typical structure of this multi-module Spring Boot Application

```
spring-boot-multimodule-quickstart/
├── pom.xml
├── domain-logic/
│   ├── pom.xml
│   └── src/main/java/com/example/domain/HelloService.java
│   └── src/test/java/com/example/domain/HelloServiceTest.java
├── shared-utils/
│   ├── pom.xml
│   └── src/main/java/com/example/utils/StringUtils.java
│   └── src/test/java/com/example/utils/StringUtilsTest.java
└── app-main/
    ├── pom.xml
    ├── src/main/java/com/example/app/Application.java
    ├── src/main/java/com/example/app/HelloController.java
    └── src/test/java/com/example/app/HelloControllerIntegrationTest.java
```

## Adding Application Properties/Settings

```
spring-boot-multimodule-quickstart/
└── app-main/
    └── src/
        └── main/
            ├── java/
            │   └── com/example/app/
            │       ├── Application.java
            │       └── HelloController.java
            └── resources/
                └── application.properties   <-- HERE!

```

In a Spring Boot multi-module project, place `application.properties` inside the app-main module, because:

- app-main is your runnable Spring Boot application (@SpringBootApplication)
- application.properties is only needed by the Spring Boot runtime, not by your libraries (domain-logic, shared-utils)
- Libraries like domain-logic and shared-utils should never have their own application.properties — they are reusable components.