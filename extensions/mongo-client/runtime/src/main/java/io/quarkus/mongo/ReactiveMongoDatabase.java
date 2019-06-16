package io.quarkus.mongo;

import java.util.List;
import java.util.concurrent.CompletionStage;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;
import org.reactivestreams.Publisher;

import com.mongodb.ReadPreference;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.CreateViewOptions;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.reactivestreams.client.AggregatePublisher;
import com.mongodb.reactivestreams.client.ChangeStreamPublisher;
import com.mongodb.reactivestreams.client.ClientSession;
import com.mongodb.reactivestreams.client.ListCollectionsPublisher;

public interface ReactiveMongoDatabase {
    String getName();

    ReactiveMongoCollection<Document> getCollection(String collectionName);

    <T> ReactiveMongoCollection<T> getCollection(String collectionName, Class<T> clazz);

    CompletionStage<Document> runCommand(Bson command);

    CompletionStage<Document> runCommand(Bson command, ReadPreference readPreference);

    <T> CompletionStage<T> runCommand(Bson command, Class<T> clazz);

    <T> CompletionStage<T> runCommand(Bson command, ReadPreference readPreference, Class<T> clazz);

    CompletionStage<Document> runCommand(ClientSession clientSession, Bson command);

    CompletionStage<Document> runCommand(ClientSession clientSession, Bson command, ReadPreference readPreference);

    <T> CompletionStage<T> runCommand(ClientSession clientSession, Bson command, Class<T> clazz);

    <T> CompletionStage<T> runCommand(ClientSession clientSession, Bson command, ReadPreference readPreference, Class<T> clazz);

    CompletionStage<Void> drop();

    CompletionStage<Void> drop(ClientSession clientSession);

    Publisher<String> listCollectionNamesAsPublisher();

    Publisher<String> listCollectionNamesAsPublisher(ClientSession clientSession);

    ListCollectionsPublisher<Document> listCollectionsAsPublisher();

    <T> ListCollectionsPublisher<T> listCollectionsAsPublisher(Class<T> clazz);

    ListCollectionsPublisher<Document> listCollectionsAsPublisher(ClientSession clientSession);

    <T> ListCollectionsPublisher<T> listCollectionsAsPublisher(ClientSession clientSession, Class<T> clazz);

    PublisherBuilder<String> listCollectionNames();

    PublisherBuilder<String> listCollectionNames(ClientSession clientSession);

    PublisherBuilder<Document> listCollections();

    <T> PublisherBuilder<T> listCollections(Class<T> clazz);

    PublisherBuilder<Document> listCollections(ClientSession clientSession);

    <T> PublisherBuilder<T> listCollections(ClientSession clientSession, Class<T> clazz);

    CompletionStage<Void> createCollection(String collectionName);

    CompletionStage<Void> createCollection(String collectionName, CreateCollectionOptions options);

    CompletionStage<Void> createCollection(ClientSession clientSession, String collectionName);

    CompletionStage<Void> createCollection(ClientSession clientSession, String collectionName, CreateCollectionOptions options);

    CompletionStage<Void> createView(String viewName, String viewOn, List<? extends Bson> pipeline);

    CompletionStage<Void> createView(String viewName, String viewOn, List<? extends Bson> pipeline,
            CreateViewOptions createViewOptions);

    CompletionStage<Void> createView(ClientSession clientSession, String viewName, String viewOn,
            List<? extends Bson> pipeline);

    CompletionStage<Void> createView(ClientSession clientSession, String viewName, String viewOn, List<? extends Bson> pipeline,
            CreateViewOptions createViewOptions);

    ChangeStreamPublisher<Document> watchAsPublisher();

    <T> ChangeStreamPublisher<T> watchAsPublisher(Class<T> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher(List<? extends Bson> pipeline);

    <T> ChangeStreamPublisher<T> watchAsPublisher(List<? extends Bson> pipeline, Class<T> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher(ClientSession clientSession);

    <T> ChangeStreamPublisher<T> watchAsPublisher(ClientSession clientSession, Class<T> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline);

    <T> ChangeStreamPublisher<T> watchAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline, Class<T> clazz);

    PublisherBuilder<ChangeStreamDocument<Document>> watch();

    <T> PublisherBuilder<ChangeStreamDocument<T>> watch(Class<T> clazz);

    PublisherBuilder<ChangeStreamDocument<Document>> watch(List<? extends Bson> pipeline);

    <T> PublisherBuilder<ChangeStreamDocument<T>> watchA(List<? extends Bson> pipeline, Class<T> clazz);

    PublisherBuilder<ChangeStreamDocument<Document>> watch(ClientSession clientSession);

    <T> PublisherBuilder<ChangeStreamDocument<T>> watch(ClientSession clientSession, Class<T> clazz);

    PublisherBuilder<ChangeStreamDocument<Document>> watchA(ClientSession clientSession, List<? extends Bson> pipeline);

    <T> PublisherBuilder<ChangeStreamDocument<T>> watch(ClientSession clientSession, List<? extends Bson> pipeline,
            Class<T> clazz);

    AggregatePublisher<Document> aggregateAsPublisher(List<? extends Bson> pipeline);

    <T> AggregatePublisher<T> aggregateAsPublisher(List<? extends Bson> pipeline, Class<T> clazz);

    AggregatePublisher<Document> aggregateAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline);

    <T> AggregatePublisher<T> aggregateAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline, Class<T> clazz);

    PublisherBuilder<Document> aggregate(List<? extends Bson> pipeline);

    <T> PublisherBuilder<T> aggregate(List<? extends Bson> pipeline, Class<T> clazz);

    PublisherBuilder<Document> aggregate(ClientSession clientSession, List<? extends Bson> pipeline);

    <T> PublisherBuilder<T> aggregate(ClientSession clientSession, List<? extends Bson> pipeline, Class<T> clazz);
}
