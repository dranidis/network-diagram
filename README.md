# net-diagram

## Description

Reads a list of tasks from a json file (default example: "examples/tasks.json"), calculates ES, EF, LS, LF, and Slack for each task and finds critical paths.

Prints the result at the console using a Gannt chart in ASCII.

## How to use

Build and test:

```
mvn clean package
```

Execute on the default file:

```
mvn exec:java -quiet  -Dexec.mainClass="com.se.netdiagram.App"
```

Execute on the different file:

```
mvn exec:java -quiet -Dexec.mainClass="com.se.netdiagram.App" -Dexec.args="examples/tasks2.json"
```

Provide a scale (zoom level) integer, e.g. 5.

```
mvn exec:java -quiet -Dexec.mainClass="com.se.netdiagram.App" -Dexec.args="examples/tasks_all.json 5"
```

Generate a coverage report (target/site/jacoco):

```
mvn jacoco:report
```
