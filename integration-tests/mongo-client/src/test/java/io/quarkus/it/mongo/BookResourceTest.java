package io.quarkus.it.mongo;

import static io.restassured.RestAssured.get;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.svenkubiak.embeddedmongodb.EmbeddedMongo;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import io.restassured.mapper.TypeRef;
import io.restassured.response.Response;

@QuarkusTest
class BookResourceTest {

    private static Jsonb jsonb;

    @BeforeAll
    public static void giveMeAMapper() {
        jsonb = JsonbBuilder.create();
        ObjectMapper mapper = new ObjectMapper() {
            @Override
            public Object deserialize(ObjectMapperDeserializationContext context) {
                return jsonb.fromJson(context.getDataToDeserialize().asString(), context.getType());
            }

            @Override
            public Object serialize(ObjectMapperSerializationContext context) {
                return jsonb.toJson(context.getObjectToSerialize());
            }
        };
        RestAssured.objectMapper(mapper);
    }

    @AfterAll
    public static void releaseMapper() throws Exception {
        jsonb.close();
    }

    @BeforeAll
    public static void startup() {
        EmbeddedMongo.DB.port(28018).start();
    }

    @AfterAll
    public static void shutdown() {
        EmbeddedMongo.DB.stop();
    }

    @Test
    public void testBlockingClient() {
        callTheEndpoint("/books");
    }

    private void callTheEndpoint(String endpoint) {
        List<Book> list = get(endpoint).as(new TypeRef<List<Book>>() {
        });
        Assertions.assertEquals(0, list.size());

        Book book1 = new Book().setAuthor("Victor Hugo").setTitle("Les Misérables")
                .setCategories(Arrays.asList("long", "very long"))
                .setDetails(new BookDetail().setRating(3).setSummary("A very long book"));
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(book1)
                .post(endpoint)
                .andReturn();
        Assertions.assertEquals(202, response.statusCode());

        Book book2 = new Book().setAuthor("Victor Hugo").setTitle("Notre-Dame de Paris")
                .setCategories(Arrays.asList("long", "quasimodo"))
                .setDetails(new BookDetail().setRating(4).setSummary("quasimodo and esmeralda"));
        response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(book2)
                .post(endpoint)
                .andReturn();
        Assertions.assertEquals(202, response.statusCode());

        list = get(endpoint).as(new TypeRef<List<Book>>() {
        });
        Assertions.assertEquals(2, list.size());

        Book book3 = new Book().setAuthor("Charles Baudelaire").setTitle("Les fleurs du mal")
                .setCategories(Collections.singletonList("poem"))
                .setDetails(new BookDetail().setRating(2).setSummary("Les Fleurs du mal is a volume of poetry."));
        response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(book3)
                .post(endpoint)
                .andReturn();
        Assertions.assertEquals(202, response.statusCode());

        list = get(endpoint).as(new TypeRef<List<Book>>() {
        });
        Assertions.assertEquals(3, list.size());

        list = get(endpoint + "/Victor Hugo").as(new TypeRef<List<Book>>() {
        });
        Assertions.assertEquals(2, list.size());
    }

    @Test
    public void testReactiveClients() {
        callTheEndpoint("/reactive-books");
    }

}
