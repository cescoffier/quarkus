package io.quarkus.vertx.http.cors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusUnitTest;

public class CORSHandlerWildcardOriginTestCase {

    @RegisterExtension
    static QuarkusUnitTest runner = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addClasses(BeanRegisteringRoute.class)
                    .addAsResource("conf/cors-config-wildcard-origins.properties", "application.properties"));

    @Test
    @DisplayName("Returns true 'Access-Control-Allow-Credentials' header on matching origin")
    void corsMatchingOrigin() {
        String origin = "http://custom.origin.quarkus";
        String methods = "POST";
        String headers = "X-Custom";
        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .options("/test").then()
                .statusCode(204)
                .header("Access-Control-Allow-Credentials", "true");
    }

    @Test
    @DisplayName("Returns no 'Access-Control-Allow-Credentials' header on matching origin")
    void corsNotMatchingOrigin() {
        String origin = "http://non.matching.origin.quarkus";
        String methods = "POST";
        String headers = "X-Custom";
        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .options("/test").then()
                .statusCode(403)
                .header("Access-Control-Allow-Origin", nullValue())
                .header("Access-Control-Allow-Credentials", is(nullValue()));
    }

    @Test
    @DisplayName("Catch invalid origins")
    void catchInvalidOrigins() {
        String methods = "POST";
        String headers = "X-Custom";
        given().header("Origin", "http://acme.org")
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .options("/test").then()
                .statusCode(403)
                .header("Access-Control-Allow-Credentials", is(nullValue()));

        given().header("Origin", "not-an-url")
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .options("/test").then()
                .statusCode(403)
                .header("Access-Control-Allow-Credentials", is(nullValue()));

        given().header("Origin", "")
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .options("/test").then()
                .statusCode(403)
                .header("Access-Control-Allow-Credentials", is(nullValue()));
    }
}
