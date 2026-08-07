FROM eclipse-temurin:21 AS build
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /workspace/target/payment_service-0.0.1-SNAPSHOT.jar app.jar
LABEL authors="syams"

ENTRYPOINT ["java", "-jar", "app.jar"]
