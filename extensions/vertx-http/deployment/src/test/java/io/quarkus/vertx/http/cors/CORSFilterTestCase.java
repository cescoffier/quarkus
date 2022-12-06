package io.quarkus.vertx.http.cors;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.quarkus.security.test.utils.TestIdentityController;
import io.quarkus.security.test.utils.TestIdentityProvider;
import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.vertx.http.runtime.cors.CORSConfig;
import io.quarkus.vertx.http.runtime.cors.CORSFilter;
import io.quarkus.vertx.http.security.PathHandler;
import io.restassured.RestAssured;
import io.restassured.http.Headers;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.given;
import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static io.vertx.core.http.HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS;
import static io.vertx.core.http.HttpHeaders.ORIGIN;
import static io.vertx.core.http.HttpHeaders.VARY;
import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CORSFilterTestCase {

    private CORSFilter filter;

    @BeforeEach
    void setup() {
        CORSConfig conf = new CORSConfig();
        conf.origins = Optional.of(List.of("https://acme.org", "http://my-domain.com"));
        conf.methods = Optional.of(List.of("GET", "POST"));
        conf.headers = Optional.of(List.of("header1", "header2"));
        conf.exposedHeaders = Optional.of(List.of("header3", "header4"));
        conf.accessControlMaxAge = Optional.of(Duration.ofSeconds(123));
        conf.accessControlAllowCredentials = Optional.of(false);
        filter = new CORSFilter(conf);
    }

    @Test
    @DisplayName("Verify with a non-cors requests")
    void nonCorsRequest() {
        var rc = mock(RoutingContext.class);
        var req = mock(HttpServerRequest.class);
        var resp = mock(HttpServerResponse.class);
        doReturn(req).when(rc).request();
        doReturn(resp).when(rc).response();

        var headers = MultiMap.caseInsensitiveMultiMap();
        var respHeaders = MultiMap.caseInsensitiveMultiMap();
        Mockito.when(req.headers()).thenReturn(headers);
        Mockito.when(resp.headers()).thenReturn(respHeaders);

        Mockito.when(req.method()).thenReturn(HttpMethod.GET);
        Mockito.when(req.absoluteURI()).thenReturn("https://something.com/test.html");
        Mockito.when(req.uri()).thenReturn("/test.html");

        filter.handle(rc);

        assertThat(respHeaders).hasSize(1);
        assertThat(respHeaders.get("vary")).isEqualTo("origin");
    }

    @Test
    @DisplayName("Verify with a non-cors request coming from the same origin")
    void sameOriginRequest() {
        var rc = mock(RoutingContext.class);
        var req = mock(HttpServerRequest.class);
        var resp = mock(HttpServerResponse.class);
        doReturn(req).when(rc).request();
        doReturn(resp).when(rc).response();



        var headers = MultiMap.caseInsensitiveMultiMap();
        var respHeaders = MultiMap.caseInsensitiveMultiMap();
        Mockito.when(req.headers()).thenReturn(headers);
        Mockito.when(resp.headers()).thenReturn(respHeaders);

        Mockito.when(req.method()).thenReturn(HttpMethod.GET);
        Mockito.when(req.scheme()).thenReturn("https");
        Mockito.when(req.absoluteURI()).thenReturn("https://domain1.com/test.html");
        Mockito.when(req.uri()).thenReturn("/test.html");

        headers.add(ORIGIN.toString(), "https://domain1.com");

        filter.handle(rc);

        verify(resp).setStatusCode(anyInt());
        assertThat(respHeaders).hasSize(1);
        assertThat(respHeaders.get("vary")).isEqualTo("origin");

//        get("/test")
//                .header(ORIGIN.toString(), "https://domain1.com");
    }

    // MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), "https://domain1.com/test.html");
    //		request.addHeader(HttpHeaders.ORIGIN, "https://domain1.com");
    //		request.setScheme("https");
    //		request.setServerName("domain1.com");
    //		request.setServerPort(443);
    //		MockHttpServletResponse response = new MockHttpServletResponse();
    //
    //		FilterChain filterChain = (filterRequest, filterResponse) -> {
    //			assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN)).isNull();
    //			assertThat(response.getHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS)).isNull();
    //		};
    //		filter.doFilter(request, response, filterChain);


    @Test
    @DisplayName("Handles a preflight CORS request correctly")
    public void corsPreflightTest() {
        String origin = "http://custom.origin.quarkus";
        String methods = "POST";
        String headers = "X-Custom";
        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .options("/test").then()
                .statusCode(204)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET,OPTIONS,POST")
                .header("Access-Control-Allow-Headers", headers);

        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .auth().basic("test", "test")
                .options("/test").then()
                .statusCode(204)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET,OPTIONS,POST")
                .header("Access-Control-Allow-Headers", headers);

        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .auth().basic("test", "wrongpassword")
                .options("/test").then()
                .statusCode(204)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET,OPTIONS,POST")
                .header("Access-Control-Allow-Headers", headers);

        given().header("Origin", origin)
                .header("Access-Control-Request-Method", "GET,OPTIONS,POST")
                .header("Access-Control-Request-Headers", headers)
                .when()
                .auth().basic("user", "user")
                .options("/test").then()
                .statusCode(204)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "GET,OPTIONS,POST")
                .header("Access-Control-Allow-Headers", headers);
    }

    @Test
    @DisplayName("Handles a direct CORS request correctly")
    public void corsNoPreflightTest() {
        String origin = "http://custom.origin.quarkus";
        String methods = "GET,POST";
        String headers = "X-Custom";
        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .get("/test").then()
                .statusCode(401)
                // * because of https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Access-Control-Allow-Origin
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", is(nullValue()))
                .header("Access-Control-Allow-Headers", is(nullValue()));

        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .auth().basic("test", "test")
                .get("/test").then()
                .statusCode(200)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", is(nullValue()))
                .header("Access-Control-Allow-Headers", is(nullValue()))
                .body(Matchers.equalTo("test:/test"));

        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .auth().basic("test", "wrongpassword")
                .get("/test").then()
                .statusCode(401)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", is(nullValue()))
                .header("Access-Control-Allow-Headers", is(nullValue()));

        given().header("Origin", origin)
                .header("Access-Control-Request-Method", methods)
                .header("Access-Control-Request-Headers", headers)
                .when()
                .auth().basic("user", "user")
                .get("/test").then()
                .statusCode(403)
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", is(nullValue()))
                .header("Access-Control-Allow-Headers", is(nullValue()));
    }
}
