package com.example.templatejava.common;

import com.example.templatejava.ApiApplication;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(
        classes = ApiApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractIntegrationTest {

    @LocalServerPort protected int port;

    protected static final MongoDBContainer mongoDBContainer;
    protected static final MockServerContainer mockServerContainer;

    static {
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:8.0"));
        mongoDBContainer.start();

        mockServerContainer =
                new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"));
        mockServerContainer.start();
    }

    @Autowired(required = false)
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUpIntegrationTest() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        if (mongoTemplate != null) {
            for (String collectionName : mongoTemplate.getCollectionNames()) {
                if (!collectionName.startsWith("system.")) {
                    mongoTemplate.dropCollection(collectionName);
                }
            }
        }
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("mockserver.endpoint", mockServerContainer::getEndpoint);
        registry.add("mockserver.host", mockServerContainer::getHost);
        registry.add("mockserver.port", mockServerContainer::getServerPort);
        registry.add("app.feign.bureau.url", mockServerContainer::getEndpoint);
        registry.add("feign.client.config.default.url", mockServerContainer::getEndpoint);
    }
}
