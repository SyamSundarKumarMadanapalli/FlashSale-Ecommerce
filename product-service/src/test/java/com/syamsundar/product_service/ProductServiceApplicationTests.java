package com.syamsundar.product_service;

import com.syamsundar.product_service.product.dto.ProductResponse;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ProductServiceApplicationTests {

	@Test
	void redisSerializerRoundTripsProductResponse() {
		GenericJacksonJsonRedisSerializer serializer =
				GenericJacksonJsonRedisSerializer.builder()
						.enableDefaultTyping(
								BasicPolymorphicTypeValidator.builder()
										.allowIfSubType("com.syamsundar.product_service")
										.build()
						)
						.build();
		UUID productId = UUID.randomUUID();
		ProductResponse product = ProductResponse.builder()
				.id(productId)
				.name("Flash Sale Phone")
				.price(12999.00)
				.availableStock(25)
				.build();

		Object cachedProduct = serializer.deserialize(serializer.serialize(product));

		assertThat(cachedProduct).isInstanceOf(ProductResponse.class);
		assertThat((ProductResponse) cachedProduct)
				.extracting(
						ProductResponse::getId,
						ProductResponse::getName,
						ProductResponse::getPrice,
						ProductResponse::getAvailableStock
				)
				.containsExactly(productId, "Flash Sale Phone", 12999.00, 25);
	}

}
