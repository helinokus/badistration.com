FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /
COPY pom.xml .
RUN mvn dependency:go-offline
COPY /src /src
#COPY checkstyle-suppressions.xml .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /target/*.jar application.jar
EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

ENTRYPOINT ["java", "-jar", "application.jar"]
