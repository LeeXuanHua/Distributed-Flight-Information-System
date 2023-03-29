# Distributed-Flight-Information-System
Design and Implementation of A Distributed Flight Information System for Distributed System


### Requirements
1. Postgres
2. JDK 15


### Installation
1. (If cloning from GitHub) Rename the application.properties.stub file to application.properties
2. Update the following fields in application.properties file
```
   spring.datasource.username=
   spring.datasource.password=
```
3. Run `mvn clean compile`
4. If you want to run the client, run `mvn exec:java@client`
5. If you want to run the server, run `mvn exec:java@server`
