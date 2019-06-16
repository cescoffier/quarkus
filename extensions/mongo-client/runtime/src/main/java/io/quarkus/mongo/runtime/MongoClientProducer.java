package io.quarkus.mongo.runtime;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import com.mongodb.client.MongoClient;

import io.quarkus.mongo.ReactiveMongoClient;

@ApplicationScoped
public class MongoClientProducer {

    private MongoClient client;
    private ReactiveMongoClient reactiveMongoClient;

    @Singleton
    @Produces
    public MongoClient client() {
        return this.client;
    }

    @Singleton
    @Produces
    public ReactiveMongoClient axle() {
        return this.reactiveMongoClient;
    }

    public void initialize(MongoClient client, ReactiveMongoClient reactiveMongoClient) {
        this.client = client;
        this.reactiveMongoClient = reactiveMongoClient;
    }
}
