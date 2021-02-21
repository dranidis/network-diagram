# net-diagram

## Description
Reads a list of tasks from a json file (default example: "examples/tasks.json"), calculates ES, EF, LS, LF, and Slack for each task and finds critical paths.

Prints the result at the console.

## How to use
Build and test:
```
mvn clean package
```

Execute on the default file:
```
mvn exec:java -Dexec.mainClass="com.se.netdiagram.App"
```

Execute on the different file:
```
mvn exec:java -Dexec.mainClass="com.se.netdiagram.App" -Dexec.args="examples/tasks2.json"
```

Generate a coverage report (target/site/jacoco):
```
mvn jacoco:report
```