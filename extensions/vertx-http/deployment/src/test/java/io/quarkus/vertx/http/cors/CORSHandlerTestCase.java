package io.quarkus.vertx.http.cors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class CORSHandlerTestCase {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(BeanRegisteringRoute.class)
                    .addAsResource("conf/cors-config.properties", "application.properties"));

    @Test
    @DisplayName("Handles a preflight CORS request correctly")
    public void corsPreflightTestServlet() {
        String origin = "http://custom.origin.quarkus";
        String methods = "POST";
        String headers = "X-Custom";
        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .options("/test").then()
                .statusCode(204) // No content
                .header("Access-Control-Allow-Origin", origin)
                .header("Access-Control-Allow-Methods", "GET,OPTIONS,POST")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Headers", headers);
    }

    @Test
    @DisplayName("Handles a direct CORS request correctly")
    public void corsNoPreflightTestServlet() {
        String origin = "http://custom.origin.quarkus";
        String methods = "POST";
        String headers = "X-Custom";
        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .get("/test").then()
                .statusCode(200)
                .header("Access-Control-Allow-Origin", origin)
                .header("Access-Control-Allow-Credentials", "true")
                // As it's not a preflight requests, the Access-Control-Allow-Methods and
                // Access-Control-Allow-Headers must NOT be passed.
                .header("Access-Control-Allow-Methods", is(nullValue()))
                .header("Access-Control-Allow-Headers", is(nullValue()))
                .body(is("test route"));
    }

}
