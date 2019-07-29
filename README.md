# A data pipeline for detecting the healthiest git projects
This project implements a simple data pipeline that detects the top 100 healthiest git projects for the last 30 days using data from https://www.gharchive.org/


# Technical Decisions

1) Tech stack

+ Java 8
+ logback for logging
+ Gson for json parser
+ Lombok

2) Future works

+ Test developed metrics to check its efficiency level
+ Add more metrics to calculate final health score

# Getting Started

The application collects data from https://www.gharchive.org/ for the last 30 days and calculate different metrics based on events. Then, these metrics are integrated to measure the health of a project. Finally, the top 100 healthiest projects will be saved to a csv file.

I implemented three metrics to calculate the health of a project includes:
+ Average number of commits (push) per day
+ Number of release
+ Average response time of first response to an issue


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