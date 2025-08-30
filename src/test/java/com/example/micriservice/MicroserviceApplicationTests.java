package com.example.micriservice;

import com.example.micriservice.dto.ProductRequest;
import com.example.micriservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers //Testcontainers automatically starts and stops Docker containers for tests.
@AutoConfigureMockMvc //which lets you simulate HTTP requests to your controllers without starting the actual server.
class MicroserviceApplicationTests {

    @Container  //The container runs only during the test and is automatically stopped afterward.
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.4");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;//used to map java Object into Json

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    //Spring Boot normally reads from application.properties but after adding this annotation overrides
    static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
        dynamicPropertyRegistry.add("spring.data.mongodb.host", mongoDBContainer::getReplicaSetUrl);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        productRepository.deleteAll();
        ProductRequest productRequest = getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product/createProduct")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());
        Assertions.assertEquals(1, productRepository.findAll().size());

    }


    private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("iphone 13")
                .description("iphone 13")
                .price(1200.0)
                .build();
    }

}
