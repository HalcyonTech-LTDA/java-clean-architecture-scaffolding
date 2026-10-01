package com.example.templatejava.customer.infrastructure.web;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.templatejava.common.AbstractIntegrationTest;
import com.example.templatejava.customer.infrastructure.web.request.CreateCustomerRequest;
import com.example.templatejava.customer.infrastructure.web.response.CustomerResponse;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CustomerControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("should create customer successfully and return 201 Created using RestAssured")
    void shouldCreateCustomerSuccessfully() {
        var request = new CreateCustomerRequest("John Doe", "john.doe@example.com");

        CustomerResponse response =
                given().contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/customers")
                        .then()
                        .statusCode(201)
                        .extract()
                        .as(CustomerResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull().isNotBlank();
        assertThat(response.name()).isEqualTo("John Doe");
        assertThat(response.email()).isEqualTo("john.doe@example.com");
        assertThat(response.createdAt()).isNotNull();
    }
}
