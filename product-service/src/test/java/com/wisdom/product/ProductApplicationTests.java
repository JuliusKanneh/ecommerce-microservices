package com.wisdom.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wisdom.product.dto.ProductRequest;
import com.wisdom.product.repositories.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRepository productRepository;

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.4.2"));

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl); //method reference
	}

	@Test
	void shouldCreateProduct() throws Exception {

		ProductRequest productRequest = getProductRequest();
		String stringRequestProduct = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(stringRequestProduct))
				.andExpect(status().isCreated()
		);
		Assertions.assertEquals(1, productRepository.findAll().size());
	}

	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("iPhone14")
				.description("iPhone 14")
				.price(BigDecimal.valueOf(14000))
				.build();
	}

	@Test
	public void shouldListAllProducts() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk());

//		if (perform.andExpect(status().isOk()).equals(status().isOk())){
//			OngoingStubbing<ProductResponse> productResponseString = Mockito.when(Mockito.mock(ProductResponse.class))
//					.thenReturn(ProductResponse.builder()
//							.id("product_001")
//							.name("iPhone14 Pro")
//							.description("iPhone 14 Pro")
//							.price(BigDecimal.valueOf(19000))
//							.build());
//			System.out.println(productResponseString);
//		}
//		OngoingStubbing<String> when = Mockito.when(
//				mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
//						.contentType(MediaType.APPLICATION_JSON)
//				).andExpect(status().isOk()).andReturn().getResponse().getContentAsString()
//		);


	}

}
