FROM eclipse-temurin:21
WORKDIR /app
COPY target/product-service-0.0.1-SNAPSHOT.jar app.jar
LABEL authors="syams"

ENTRYPOINT ["java", "-jar", "app.jar"]