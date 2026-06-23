# Redis Cache Serialization Fix Notes

This file documents the changes made to fix the Redis cache deserialization error:

```text
java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to ProductResponse
```

## Problem

The first `GET /api/products/{productId}` request fetched the product from PostgreSQL and stored the response in Redis.

The second request found the value in Redis, but Redis returned it as a `LinkedHashMap` instead of `ProductResponse`, causing a `ClassCastException`.

This happened because the cached JSON did not include enough type information for Spring's cache layer to rebuild the correct DTO class.

## Changes Made

### 1. Updated Redis serialization configuration

File:

```text
product-service/src/main/java/com/syamsundar/product_service/common/config/RedisConfig.java
```

Changes:

- Configured Redis cache values with `GenericJacksonJsonRedisSerializer.builder()`.
- Enabled default typing using Jackson 3 / Spring Boot 4 APIs.
- Restricted allowed deserialization types to this application package:

```java
.allowIfSubType("com.syamsundar.product_service")
```

- Configured Redis cache keys with `StringRedisSerializer`.

This makes Redis deserialize cached values back into `ProductResponse` instead of `LinkedHashMap`.

### 2. Updated ProductResponse DTO

File:

```text
product-service/src/main/java/com/syamsundar/product_service/product/dto/ProductResponse.java
```

Changes:

- Added `@Setter`.
- Added `@NoArgsConstructor`.
- Added `@AllArgsConstructor`.
- Implemented `Serializable`.

This allows Jackson to recreate the DTO properly when reading from Redis.

### 3. Added serializer test

File:

```text
product-service/src/test/java/com/syamsundar/product_service/ProductServiceApplicationTests.java
```

Changes:

- Added a focused test that serializes and deserializes `ProductResponse`.
- Verified the deserialized object is actually a `ProductResponse`, not a `LinkedHashMap`.

Test command used:

```powershell
mvn test
```

Result:

```text
BUILD SUCCESS
```

### 4. Updated Dockerfile

File:

```text
product-service/Dockerfile
```

Problem:

The old Dockerfile copied an existing jar from `target`:

```dockerfile
COPY target/product-service-0.0.1-SNAPSHOT.jar app.jar
```

That meant Docker could rebuild the image while still using an old stale jar.

Fix:

The Dockerfile now builds the jar inside Docker before copying it into the final runtime image.

This ensures:

```powershell
docker compose up --build
```

uses the latest source code changes.

## Fresh Restart Commands

Run these from:

```text
D:\SystemDesign\FlashSale-Ecommerce\product-service
```

Use:

```powershell
docker compose down --volumes
docker compose up --build
```

The `--volumes` flag removes old Redis/Postgres container data so old bad cached Redis values do not remain.

## Expected Behavior

After rebuilding:

1. Create a product using Postman.
2. Fetch the product for the first time.
3. Logs should show DB fetch:

```text
Fetching product from DB...
```

4. Fetch the same product again.
5. Logs should show Redis cache hit.
6. No `LinkedHashMap cannot be cast to ProductResponse` error should occur.

