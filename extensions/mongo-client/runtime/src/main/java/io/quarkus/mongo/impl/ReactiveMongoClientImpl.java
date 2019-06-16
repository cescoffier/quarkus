package io.quarkus.mongo.impl;

import static io.quarkus.mongo.impl.Wrappers.toCompletionStage;
import static io.quarkus.mongo.impl.Wrappers.toPublisherBuilder;

import java.util.List;
import java.util.concurrent.CompletionStage;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.reactivestreams.Publisher;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.reactivestreams.client.*;

import io.quarkus.mongo.ReactiveMongoDatabase;

public class ReactiveMongoClientImpl implements io.quarkus.mongo.ReactiveMongoClient {

    private final MongoClient client;

    public ReactiveMongoClientImpl(MongoClient client) {
        this.client = client;
    }

    @Override
    public ReactiveMongoDatabase getDatabase(String name) {
        return new ReactiveMongoDatabaseImpl(client.getDatabase(name));
    }

    @Override
    public void close() {
        client.close();
    }

    @Override
    public PublisherBuilder<String> listDatabaseNames() {
        return toPublisherBuilder(client.listDatabaseNames());
    }

    @Override
    public Publisher<String> listDatabaseNamesAsPublisher() {
        return client.listDatabaseNames();
    }

    @Override
    public PublisherBuilder<String> listDatabaseNames(ClientSession clientSession) {
        return toPublisherBuilder(client.listDatabaseNames(clientSession));
    }

    @Override
    public Publisher<String> listDatabaseNamesAsPublisher(ClientSession clientSession) {
        return client.listDatabaseNames(clientSession);
    }

    @Override
    public ListDatabasesPublisher<Document> listDatabasesAsPublisher() {
        return client.listDatabases();
    }

    @Override
    public PublisherBuilder<Document> listDatabases() {
        return toPublisherBuilder(client.listDatabases());
    }

    @Override
    public <T> ListDatabasesPublisher<T> listDatabasesAsPublisher(Class<T> clazz) {
        return client.listDatabases(clazz);
    }

    @Override
    public <T> PublisherBuilder<T> listDatabases(Class<T> clazz) {
        return toPublisherBuilder(client.listDatabases(clazz));
    }

    @Override
    public ListDatabasesPublisher<Document> listDatabasesAsPublisher(ClientSession clientSession) {
        return client.listDatabases(clientSession);
    }

    @Override
    public PublisherBuilder<Document> listDatabases(ClientSession clientSession) {
        return toPublisherBuilder(client.listDatabases(clientSession));
    }

    @Override
    public <T> ListDatabasesPublisher<T> listDatabasesAsPublisher(ClientSession clientSession, Class<T> clazz) {
        return client.listDatabases(clientSession, clazz);
    }

    @Override
    public <T> PublisherBuilder<T> listDatabases(ClientSession clientSession, Class<T> clazz) {
        return toPublisherBuilder(client.listDatabases(clientSession, clazz));
    }

    @Override
    public ChangeStreamPublisher<Document> watchAsPublisher() {
        return client.watch();
    }

    @Override
    public PublisherBuilder<ChangeStreamDocument<Document>> watch() {
        return toPublisherBuilder(client.watch());
    }

    @Override
    public <T> ChangeStreamPublisher<T> watchAsPublisher(Class<T> clazz) {
        return client.watch(clazz);
    }

    @Override
    public <T> PublisherBuilder<ChangeStreamDocument<T>> watch(Class<T> clazz) {
        return toPublisherBuilder(client.watch(clazz));
    }

    @Override
    public ChangeStreamPublisher<Document> watchAsPublisher(List<? extends Bson> pipeline) {
        return client.watch(pipeline);
    }

    @Override
    public PublisherBuilder<ChangeStreamDocument<Document>> watch(List<? extends Bson> pipeline) {
        return toPublisherBuilder(client.watch(pipeline));
    }

    @Override
    public <T> ChangeStreamPublisher<T> watchAsPublisher(List<? extends Bson> pipeline, Class<T> clazz) {
        return client.watch(pipeline, clazz);
    }

    @Override
    public <T> PublisherBuilder<ChangeStreamDocument<T>> watch(List<? extends Bson> pipeline, Class<T> clazz) {
        return toPublisherBuilder(client.watch(pipeline, clazz));
    }

    @Override
    public ChangeStreamPublisher<Document> watchAsPublisher(ClientSession clientSession) {
        return client.watch(clientSession);
    }

    @Override
    public PublisherBuilder<ChangeStreamDocument<Document>> watch(ClientSession clientSession) {
        return toPublisherBuilder(client.watch(clientSession));
    }

    @Override
    public <T> ChangeStreamPublisher<T> watchAsPublisher(ClientSession clientSession, Class<T> clazz) {
        return client.watch(clientSession, clazz);
    }

    @Override
    public ChangeStreamPublisher<Document> watchAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline) {
        return client.watch(clientSession, pipeline);
    }

    @Override
    public PublisherBuilder<ChangeStreamDocument<Document>> watch(ClientSession clientSession,
            List<? extends Bson> pipeline) {
        return toPublisherBuilder(client.watch(clientSession, pipeline));
    }

    @Override
    public <T> ChangeStreamPublisher<T> watchAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline,
            Class<T> clazz) {
        return client.watch(clientSession, pipeline, clazz);
    }

    @Override
    public <T> PublisherBuilder<ChangeStreamDocument<T>> watch(ClientSession clientSession, List<? extends Bson> pipeline,
            Class<T> clazz) {
        return toPublisherBuilder(client.watch(clientSession, pipeline, clazz));
    }

    @Override
    public CompletionStage<ClientSession> startSession() {
        return toCompletionStage(client.startSession());
    }

    @Override
    public CompletionStage<ClientSession> startSession(ClientSessionOptions options) {
        return toCompletionStage(client.startSession(options));
    }
}
