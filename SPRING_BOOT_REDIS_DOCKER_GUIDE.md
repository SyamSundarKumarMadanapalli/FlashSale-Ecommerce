# Spring Boot Redis Docker Guide

This guide is a quick reference for adding Redis cache to a Spring Boot service running with Docker Compose.

## 1. Add Dependencies

For Maven:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
```

## 2. Enable Cache In Spring Boot

Create a Redis config class:

```java
package com.example.demo.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.example.demo")
                .build();

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(
                                        GenericJacksonJsonRedisSerializer.builder()
                                                .enableDefaultTyping(typeValidator)
                                                .build()
                                )
                );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}

(or)

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        objectMapper.findAndRegisterModules(); // JavaTime etc.

        // No default typing here
        var valueSerializer =
                new org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer<>(Object.class);
        valueSerializer.setObjectMapper(objectMapper);

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(new StringRedisSerializer())
                        )
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair
                                        .fromSerializer(valueSerializer)
                        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }
}
```

Important:

Replace this package:

```java
.allowIfSubType("com.example.demo")
```

with your real base package, for example:

```java
.allowIfSubType("com.syamsundar.product_service")
```

This helps Redis deserialize cached JSON back into your DTO classes instead of `LinkedHashMap`.

## 3. Configure application.yml

For local Docker Compose usage:

```yaml
spring:
  cache:
    type: redis

  data:
    redis:
      host: localhost
      port: 6379f
```

When the Spring Boot app also runs inside Docker Compose, use the Redis service name instead of `localhost`:

```yaml
spring:
  cache:
    type: redis

  data:
    redis:
      host: redis
      port: 6379
```

You can also pass these values through Docker Compose environment variables.

## 4. Add Redis To docker-compose.yml

Example:

```yaml
services:
  redis:
    image: redis:7-alpine
    container_name: app-redis
    ports:
      - "6379:6379"

  app:
    build: .
    container_name: app-service
    depends_on:
      - redis
    ports:
      - "8080:8080"
    environment:
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
```

Important:

- Inside Docker Compose, the app should connect to `redis`, not `localhost`.
- `localhost` inside the app container means the app container itself, not the Redis container.

## 5. Make DTOs Cache Friendly

For objects returned from `@Cacheable`, make sure DTOs can be recreated by Jackson.

Example:

```java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse implements Serializable {

    private UUID id;
    private String name;
    private Double price;
    private Integer availableStock;
}
```

Recommended:

- Use DTOs for cache values, not JPA entities.
- Add no-args constructor.
- Add setters, or use a Jackson-compatible constructor.
- Implement `Serializable`.

## 6. Use @Cacheable

Example:

```java
@Cacheable(value = "products", key = "#productId")
public ProductResponse getProduct(UUID productId) {
    System.out.println("Fetching product from DB...");

    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));

    return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .price(product.getPrice())
            .availableStock(product.getAvailableStock())
            .build();
}
```

Expected behavior:

- First request: data comes from DB and is saved to Redis.
- Second request with same key: data comes from Redis.

## 7. Evict Or Update Cache When Data Changes

If product data changes, remove or update the related cache entry.

Example with `@CacheEvict`:

```java
@CacheEvict(value = "products", key = "#productId")
public void deleteProduct(UUID productId) {
    productRepository.deleteById(productId);
}
```

Example for updating:

```java
@CacheEvict(value = "products", key = "#productId")
public ProductResponse updateProduct(UUID productId, UpdateProductRequest request) {
    Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException("Product not found"));

    product.setName(request.getName());
    product.setPrice(request.getPrice());

    Product savedProduct = productRepository.save(product);

    return ProductResponse.builder()
            .id(savedProduct.getId())
            .name(savedProduct.getName())
            .price(savedProduct.getPrice())
            .availableStock(savedProduct.getAvailableStock())
            .build();
}
```

For create methods, caching is usually not needed unless you immediately return a cacheable object by ID.

## 8. Dockerfile Recommendation

Avoid copying a stale jar from `target` unless you always build the jar manually first.

Better Dockerfile:

```dockerfile
FROM eclipse-temurin:21 AS build
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -DskipTests package

FROM eclipse-temurin:21
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
```

This makes sure:

```powershell
docker compose up --build
```

builds the latest Java code into the Docker image.

## 9. Useful Docker Commands

Start:

```powershell
docker compose up --build
```

Stop:

```powershell
docker compose down
```

Stop and remove container data:

```powershell
docker compose down --volumes
```

Rebuild one service without cache:

```powershell
docker compose build --no-cache app
```

View logs:

```powershell
docker compose logs -f app
```

Open Redis CLI:

```powershell
docker exec -it app-redis redis-cli
```

List Redis keys:

```text
KEYS *
```

Check a key TTL:

```text
TTL key-name
```

Delete one key:

```text
DEL key-name
```

Clear current Redis database:

```text
FLUSHDB
```

## 10. Common Issues

### Issue: LinkedHashMap cannot be cast to DTO

Cause:

Redis deserialized JSON into a generic map instead of your DTO class.

Fix:

- Use `GenericJacksonJsonRedisSerializer` with type metadata.
- Make your DTO Jackson-friendly.
- Clear old cached values after changing serializers.

### Issue: App cannot connect to Redis in Docker

Cause:

The app is using `localhost` from inside the container.

Fix:

Use the Docker Compose service name:

```yaml
SPRING_DATA_REDIS_HOST: redis
```

### Issue: Docker rebuild did not include code changes

Cause:

Dockerfile copied an old jar from `target`.

Fix:

Build the jar inside Docker, or run:

```powershell
mvn clean package
docker compose up --build
```

### Issue: Old Redis values still cause errors

Cause:

Redis contains values written by an older serializer.

Fix:

Use one of these:

```powershell
docker compose down --volumes
```

or:

```text
FLUSHDB
```

## 11. Quick Checklist

Before testing Redis cache:

- `spring-boot-starter-data-redis` is added.
- `spring-boot-starter-cache` is added.
- `@EnableCaching` exists.
- `spring.cache.type=redis` is configured.
- Docker Compose has a Redis service.
- App container uses Redis host `redis`.
- Cache DTO has no-args constructor and setters.
- Redis serializer stores type info.
- Docker image contains the latest jar.
- Old Redis data is removed after serializer changes.

