# A data pipeline for detecting the healthiest git projects
This project implements a simple data pipeline that detect the top 100 healthiest git projects for the last 30 days using data from https://www.gharchive.org/


# Technical Decisions

1) Tech stack

+ Java 8
+ logback for logging
+ Gson for json parser
+ Lombok

2) Future works

+ Test developed metrics to check its efficiency level
+ Add more metrics to calculate final health score

# Follow those below instructions to run project

1) Prerequisites


+ Java 8
+ Apache Maven 3.3.9.

2) Compile Project

First, you need to compile the project, using the following command in project root:
```
mvn compile
```

3) Run Project

After compiling, using the following command in project root to run the application:
```
mvn exec:java -Dexec.mainClass=quod.ai.bigdata.TopProjectsDetector -Dexec.args="<directory for saving csv file>"
```

For example:

```
mvn exec:java -Dexec.mainClass=quod.ai.bigdata.TopProjectsDetector -Dexec.args="D:\\"
```