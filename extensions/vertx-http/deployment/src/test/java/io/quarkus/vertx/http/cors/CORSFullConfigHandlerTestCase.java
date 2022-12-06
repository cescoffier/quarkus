package io.quarkus.vertx.http.cors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class CORSFullConfigHandlerTestCase {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(BeanRegisteringRoute.class)
                    .addAsResource("conf/cors-config-full.properties", "application.properties"));

    @Test
    @DisplayName("Handles a detailed CORS preflight request correctly")
    public void corsFullConfigTestServlet() {
        given().header("Origin", "http://custom.origin.quarkus")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "X-Custom")
                .when()
                .options("/test").then()
                .statusCode(204)
                .header("Access-Control-Allow-Origin", "http://custom.origin.quarkus")
                .header("Access-Control-Allow-Methods", "GET,PUT,POST")
                // Cannot be part of a preflight response
                .header("Access-Control-Expose-Headers", is(nullValue()))
                .header("Access-Control-Allow-Headers", "X-Custom")
                // Cannot be part of a preflight response as, if not set, credentials mode is always "same-origin",
                // and this header is only set if the credential mode is "include"
                .header("Access-Control-Allow-Credentials", is(nullValue()))
                .header("Access-Control-Max-Age", "86400");

        given().header("Origin", "http://www.quarkus.io")
                .header("Access-Control-Request-Method", "POST")
                .when()
                .options("/test").then()
                .statusCode(204)
                .header("Access-Control-Allow-Origin", "http://www.quarkus.io")
                .header("Access-Control-Allow-Methods", "GET,PUT,POST")
                .header("Access-Control-Expose-Headers", is(nullValue()));
    }

    @Test
    @DisplayName("Returns only allowed headers and methods")
    public void corsPartialMethodsTestServlet() {
        given().header("Origin", "http://custom.origin.quarkus")
                .header("Access-Control-Request-Method", "DELETE")
                .header("Access-Control-Request-Headers", "X-Custom,X-Custom2")
                .when()
                .options("/test").then()
                .statusCode(204)
                .header("Access-Control-Allow-Origin", "http://custom.origin.quarkus")
                // Spec: If request’s method is not in methods, request’s method is not a CORS-safelisted method,
                // and request’s credentials mode is "include" or methods does not contain `*`, then return a network error.
                // So basically, it's the browser that needs to handle this case.
                .header("Access-Control-Allow-Methods", "GET,PUT,POST") // Should not return DELETE
                .header("Access-Control-Allow-Headers", "X-Custom");// Should not return X-Custom2
    }

}
