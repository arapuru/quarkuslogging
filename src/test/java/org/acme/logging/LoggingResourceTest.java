package org.acme.logging;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class LoggingResourceTest {

    @Test
    public void testInfoEndpoint() {
        given()
                .when().get("/logging/info")
                .then()
                .statusCode(200)
                .body(containsString("Logged info with correlation_id"));
    }

    @Test
    public void testErrorEndpoint() {
        given()
                .when().get("/logging/error")
                .then()
                .statusCode(200)
                .body(containsString("Logged error with correlation_id"));
    }

    @Test
    public void testInterceptorErrorEndpoint() {
        // The interceptor rethrows the exception, so we expect a 500
        given()
                .when().get("/logging/interceptor-error")
                .then()
                .statusCode(500);
    }

    @Test
    public void testServiceExceptionEndpoint() {
        // The interceptor rethrows the ServiceException, so we expect a 500
        given()
                .when().get("/logging/service-exception")
                .then()
                .statusCode(500);
    }
}
