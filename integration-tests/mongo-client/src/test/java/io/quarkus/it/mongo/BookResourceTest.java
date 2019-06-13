package io.quarkus.it.mongo;

import java.util.Arrays;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import de.svenkubiak.embeddedmongodb.EmbeddedMongo;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import io.restassured.response.Response;

@QuarkusTest
class BookResourceTest {

    @BeforeAll
    static void giveMeAMapper() {
        final Jsonb jsonb = JsonbBuilder.create();
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

    @BeforeAll
    static void startup() {
        EmbeddedMongo.DB.port(28018).start();
    }

    @AfterAll
    static void shutdown() {
        EmbeddedMongo.DB.stop();
    }

    @Test
    public void test() {
        String string = RestAssured.get("/books").asString();
        System.out.println(string);

        Book book = new Book().setAuthor("Victor Hugo").setTitle("Les Misérables")
                .setCategories(Arrays.asList("long", "very long"))
                .setDetails(new BookDetail().setRating(3).setSummary("A very long book"));
        Response response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(book)
                .post("/books")
                .andReturn();
        System.out.println(response.statusCode());

        book = new Book().setAuthor("Victor Hugo").setTitle("Notre-Dame de Paris")
                .setCategories(Arrays.asList("long", "quasimodo"))
                .setDetails(new BookDetail().setRating(4).setSummary("quasimodo and esmeralda"));
        response = RestAssured
                .given()
                .header("Content-Type", "application/json")
                .body(book)
                .post("/books")
                .andReturn();
        System.out.println(response.statusCode());

        string = RestAssured.get("/books").asString();
        System.out.println(string);

    }

}
