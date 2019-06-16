package io.quarkus.mongo;

import java.util.List;
import java.util.concurrent.CompletionStage;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.reactivestreams.Publisher;

import com.mongodb.ClientSessionOptions;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.reactivestreams.client.ChangeStreamPublisher;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.ListDatabasesPublisher;

public interface ReactiveMongoClient {
    ReactiveMongoDatabase getDatabase(String name);

    void close();

    PublisherBuilder<String> listDatabaseNames();

    Publisher<String> listDatabaseNamesAsPublisher();

    PublisherBuilder<String> listDatabaseNames(ClientSession clientSession);

    Publisher<String> listDatabaseNamesAsPublisher(ClientSession clientSession);

    ListDatabasesPublisher<Document> listDatabasesAsPublisher();

    PublisherBuilder<Document> listDatabases();

    <T> ListDatabasesPublisher<T> listDatabasesAsPublisher(Class<T> clazz);

    <T> PublisherBuilder<T> listDatabases(Class<T> clazz);

    ListDatabasesPublisher<Document> listDatabasesAsPublisher(ClientSession clientSession);

    PublisherBuilder<Document> listDatabases(ClientSession clientSession);

    <T> ListDatabasesPublisher<T> listDatabasesAsPublisher(ClientSession clientSession, Class<T> clazz);

    <T> PublisherBuilder<T> listDatabases(ClientSession clientSession, Class<T> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher();

    PublisherBuilder<ChangeStreamDocument<Document>> watch();

    <T> ChangeStreamPublisher<T> watchAsPublisher(Class<T> clazz);

    <T> PublisherBuilder<ChangeStreamDocument<T>> watch(Class<T> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher(List<? extends Bson> pipeline);

    PublisherBuilder<ChangeStreamDocument<Document>> watch(List<? extends Bson> pipeline);

    <T> ChangeStreamPublisher<T> watchAsPublisher(List<? extends Bson> pipeline, Class<T> clazz);

    <T> PublisherBuilder<ChangeStreamDocument<T>> watch(List<? extends Bson> pipeline, Class<T> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher(ClientSession clientSession);

    PublisherBuilder<ChangeStreamDocument<Document>> watch(ClientSession clientSession);

    <T> ChangeStreamPublisher<T> watchAsPublisher(ClientSession clientSession, Class<T> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline);

    PublisherBuilder<ChangeStreamDocument<Document>> watch(ClientSession clientSession,
            List<? extends Bson> pipeline);

    <T> ChangeStreamPublisher<T> watchAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline,
            Class<T> clazz);

    <T> PublisherBuilder<ChangeStreamDocument<T>> watch(ClientSession clientSession, List<? extends Bson> pipeline,
            Class<T> clazz);

    CompletionStage<ClientSession> startSession();

    CompletionStage<ClientSession> startSession(ClientSessionOptions options);
}
