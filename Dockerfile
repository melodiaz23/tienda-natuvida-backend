FROM eclipse-temurin:17-jdk AS build
WORKDIR /workspace/app

COPY pom.xml .
COPY src src
COPY mvnw .
COPY .mvn .mvn

RUN ./mvnw package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--server.port=${PORT:8080}"]