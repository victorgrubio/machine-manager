FROM maven:3.9.4 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:21.0.1-alpine3.18
WORKDIR /app
COPY --from=build app/target/machineapi-0.0.1.jar /machineapi-0.0.1.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/machineapi-0.0.1.jar"]