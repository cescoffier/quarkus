package io.quarkus.vertx.http.management;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Consumer;

import org.hamcrest.Matchers;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.builder.BuildChainBuilder;
import io.quarkus.builder.BuildContext;
import io.quarkus.builder.BuildStep;
import io.quarkus.test.QuarkusUnitTest;
import io.quarkus.vertx.http.deployment.NonApplicationRootPathBuildItem;
import io.quarkus.vertx.http.deployment.RouteBuildItem;
import io.restassured.RestAssured;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class ManagementCrudRouteTest {

    private static final String APP_PROPS = """
            quarkus.management.enabled=true
            """;

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot((jar) -> jar
                    .addAsResource(new StringAsset(APP_PROPS), "application.properties"))
            .addBuildChainCustomizer(registerCRUDRoutes());

    static Consumer<BuildChainBuilder> registerCRUDRoutes() {
        return new Consumer<BuildChainBuilder>() {
            @Override
            public void accept(BuildChainBuilder builder) {
                builder.addBuildStep(new BuildStep() {
                    @Override
                    public void execute(BuildContext context) {
                        NonApplicationRootPathBuildItem buildItem = context.consume(NonApplicationRootPathBuildItem.class);

                        context.produce(buildItem.routeBuilder()
                                .management()
                                .routeFunction("test", new PostRouterConsumer())
                                .handler(new PostHandler())
                                .build());

                        context.produce(buildItem.routeBuilder()
                                .management()
                                .routeFunction("test", new GetRouterConsumer())
                                .handler(new GetHandler())
                                .build());
                    }
                }).produces(RouteBuildItem.class)
                        .consumes(NonApplicationRootPathBuildItem.class)
                        .build();
            }
        };
    }

    @Test
    public void test() {
        RestAssured.given().get("http://localhost:9001/q/test")
                .then().statusCode(200).body(Matchers.equalTo("OK!"));

        var payload = new JsonObject().put("foo", "bar").encode();
        var o = RestAssured.given()
                .formParam("foo", "bar")
                .post("http://localhost:9001/q/test")
                .then().statusCode(200).extract().body().asString();
        assertThat(payload).isEqualTo(o);
    }

    public static class GetRouterConsumer implements Consumer<Route> {

        @Override
        public void accept(Route route) {
            route.method(HttpMethod.GET);
        }
    }

    public static class PostRouterConsumer implements Consumer<Route> {

        @Override
        public void accept(Route route) {
            route.method(HttpMethod.POST).handler(BodyHandler.create());
        }
    }

    public static class GetHandler implements Handler<RoutingContext> {
        @Override
        public void handle(RoutingContext routingContext) {
            routingContext.end("OK!");
        }
    }

    public static class PostHandler implements Handler<RoutingContext> {
        @Override
        public void handle(RoutingContext routingContext) {
            String v = routingContext.request().getFormAttribute("foo");
            routingContext.end(new JsonObject().put("foo", v).toBuffer());
        }
    }
}
