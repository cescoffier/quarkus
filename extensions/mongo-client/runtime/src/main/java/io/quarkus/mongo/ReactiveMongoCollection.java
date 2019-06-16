package io.quarkus.mongo;

import java.util.List;
import java.util.concurrent.CompletionStage;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.eclipse.microprofile.reactive.streams.operators.PublisherBuilder;

import com.mongodb.MongoNamespace;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.CreateIndexOptions;
import com.mongodb.client.model.DeleteOptions;
import com.mongodb.client.model.DropIndexOptions;
import com.mongodb.client.model.EstimatedDocumentCountOptions;
import com.mongodb.client.model.FindOneAndDeleteOptions;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.InsertManyOptions;
import com.mongodb.client.model.InsertOneOptions;
import com.mongodb.client.model.RenameCollectionOptions;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.WriteModel;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.*;

public interface ReactiveMongoCollection<T> {
    MongoNamespace getNamespace();

    Class getDocumentClass();

    CompletionStage<Long> estimatedDocumentCount();

    CompletionStage<Long> estimatedDocumentCount(EstimatedDocumentCountOptions options);

    CompletionStage<Long> countDocuments();

    CompletionStage<Long> countDocuments(Bson filter);

    CompletionStage<Long> countDocuments(Bson filter, CountOptions options);

    CompletionStage<Long> countDocuments(ClientSession clientSession);

    CompletionStage<Long> countDocuments(ClientSession clientSession, Bson filter);

    CompletionStage<Long> countDocuments(ClientSession clientSession, Bson filter, CountOptions options);

    <D> DistinctPublisher<D> distinctAsPublisher(String fieldName, Class<D> clazz);

    <D> DistinctPublisher<D> distinctAsPublisher(String fieldName, Bson filter, Class<D> clazz);

    <D> DistinctPublisher<D> distinctAsPublisher(ClientSession clientSession, String fieldName, Class<D> clazz);

    <D> DistinctPublisher<D> distinctAsPublisher(ClientSession clientSession, String fieldName, Bson filter, Class<D> clazz);

    <D> PublisherBuilder<D> distinct(String fieldName, Class<D> clazz);

    <D> PublisherBuilder<D> distinct(String fieldName, Bson filter, Class<D> clazz);

    <D> PublisherBuilder<D> distinct(ClientSession clientSession, String fieldName, Class<D> clazz);

    <D> PublisherBuilder<D> distinct(ClientSession clientSession, String fieldName, Bson filter, Class<D> clazz);

    FindPublisher<T> findAsPublisher();

    <D> FindPublisher<D> findAsPublisher(Class<D> clazz);

    FindPublisher<T> findAsPublisher(Bson filter);

    <D> FindPublisher<D> findAsPublisher(Bson filter, Class<D> clazz);

    FindPublisher<T> findAsPublisher(ClientSession clientSession);

    <D> FindPublisher<D> findAsPublisher(ClientSession clientSession, Class<D> clazz);

    FindPublisher<T> findAsPublisher(ClientSession clientSession, Bson filter);

    <D> FindPublisher<D> findAsPublisher(ClientSession clientSession, Bson filter, Class<D> clazz);

    PublisherBuilder<T> find();

    <D> PublisherBuilder<D> find(Class<D> clazz);

    PublisherBuilder<T> find(Bson filter);

    <D> PublisherBuilder<D> find(Bson filter, Class<D> clazz);

    PublisherBuilder<T> find(ClientSession clientSession);

    <D> PublisherBuilder<D> find(ClientSession clientSession, Class<D> clazz);

    PublisherBuilder<T> find(ClientSession clientSession, Bson filter);

    <D> PublisherBuilder<D> find(ClientSession clientSession, Bson filter, Class<D> clazz);

    AggregatePublisher<Document> aggregateAsPublisher(List<? extends Bson> pipeline);

    <D> AggregatePublisher<D> aggregateAsPublisher(List<? extends Bson> pipeline, Class<D> clazz);

    AggregatePublisher<Document> aggregateAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline);

    <D> AggregatePublisher<D> aggregateAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline, Class<D> clazz);

    PublisherBuilder<Document> aggregate(List<? extends Bson> pipeline);

    <D> PublisherBuilder<D> aggregate(List<? extends Bson> pipeline, Class<D> clazz);

    PublisherBuilder<Document> aggregate(ClientSession clientSession, List<? extends Bson> pipeline);

    <D> PublisherBuilder<D> aggregate(ClientSession clientSession, List<? extends Bson> pipeline, Class<D> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher();

    <D> ChangeStreamPublisher<D> watchAsPublisher(Class<D> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher(List<? extends Bson> pipeline);

    <D> ChangeStreamPublisher<D> watchAsPublisher(List<? extends Bson> pipeline, Class<D> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher(ClientSession clientSession);

    <D> ChangeStreamPublisher<D> watchAsPublisher(ClientSession clientSession, Class<D> clazz);

    ChangeStreamPublisher<Document> watchAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline);

    <D> ChangeStreamPublisher<D> watchAsPublisher(ClientSession clientSession, List<? extends Bson> pipeline, Class<D> clazz);

    PublisherBuilder<ChangeStreamDocument<Document>> watch();

    <D> PublisherBuilder<ChangeStreamDocument<D>> watch(Class<D> clazz);

    PublisherBuilder<ChangeStreamDocument<Document>> watch(List<? extends Bson> pipeline);

    <D> PublisherBuilder<ChangeStreamDocument<D>> watch(List<? extends Bson> pipeline, Class<D> clazz);

    PublisherBuilder<ChangeStreamDocument<Document>> watch(ClientSession clientSession);

    <D> PublisherBuilder<ChangeStreamDocument<D>> watch(ClientSession clientSession, Class<D> clazz);

    PublisherBuilder<ChangeStreamDocument<Document>> watch(ClientSession clientSession, List<? extends Bson> pipeline);

    <D> PublisherBuilder<ChangeStreamDocument<D>> watch(ClientSession clientSession, List<? extends Bson> pipeline,
            Class<D> clazz);

    MapReducePublisher<Document> mapReduceAsPublisher(String mapFunction, String reduceFunction);

    <D> MapReducePublisher<D> mapReduceAsPublisher(String mapFunction, String reduceFunction, Class<D> clazz);

    MapReducePublisher<Document> mapReduceAsPublisher(ClientSession clientSession, String mapFunction, String reduceFunction);

    <D> MapReducePublisher<D> mapReduceAsPublisher(ClientSession clientSession, String mapFunction, String reduceFunction,
            Class<D> clazz);

    PublisherBuilder<Document> mapReduce(String mapFunction, String reduceFunction);

    <D> PublisherBuilder<D> mapReduce(String mapFunction, String reduceFunction, Class<D> clazz);

    PublisherBuilder<Document> mapReduce(ClientSession clientSession, String mapFunction, String reduceFunction);

    <D> PublisherBuilder<D> mapReduce(ClientSession clientSession, String mapFunction, String reduceFunction, Class<D> clazz);

    CompletionStage<BulkWriteResult> bulkWrite(List<? extends WriteModel<? extends T>> requests);

    CompletionStage<BulkWriteResult> bulkWrite(List<? extends WriteModel<? extends T>> requests, BulkWriteOptions options);

    CompletionStage<BulkWriteResult> bulkWrite(ClientSession clientSession, List<? extends WriteModel<? extends T>> requests);

    CompletionStage<BulkWriteResult> bulkWrite(ClientSession clientSession, List<? extends WriteModel<? extends T>> requests,
            BulkWriteOptions options);

    CompletionStage<Void> insertOne(T t);

    CompletionStage<Void> insertOne(T t, InsertOneOptions options);

    CompletionStage<Void> insertOne(ClientSession clientSession, T t);

    CompletionStage<Void> insertOne(ClientSession clientSession, T t, InsertOneOptions options);

    CompletionStage<Void> insertMany(List<? extends T> tDocuments);

    CompletionStage<Void> insertMany(List<? extends T> tDocuments, InsertManyOptions options);

    CompletionStage<Void> insertMany(ClientSession clientSession, List<? extends T> tDocuments);

    CompletionStage<Void> insertMany(ClientSession clientSession, List<? extends T> tDocuments, InsertManyOptions options);

    CompletionStage<DeleteResult> deleteOne(Bson filter);

    CompletionStage<DeleteResult> deleteOne(Bson filter, DeleteOptions options);

    CompletionStage<DeleteResult> deleteOne(ClientSession clientSession, Bson filter);

    CompletionStage<DeleteResult> deleteOne(ClientSession clientSession, Bson filter, DeleteOptions options);

    CompletionStage<DeleteResult> deleteMany(Bson filter);

    CompletionStage<DeleteResult> deleteMany(Bson filter, DeleteOptions options);

    CompletionStage<DeleteResult> deleteMany(ClientSession clientSession, Bson filter);

    CompletionStage<DeleteResult> deleteMany(ClientSession clientSession, Bson filter, DeleteOptions options);

    CompletionStage<UpdateResult> replaceOne(Bson filter, T replacement);

    CompletionStage<UpdateResult> replaceOne(Bson filter, T replacement, ReplaceOptions options);

    CompletionStage<UpdateResult> replaceOne(ClientSession clientSession, Bson filter, T replacement);

    CompletionStage<UpdateResult> replaceOne(ClientSession clientSession, Bson filter, T replacement, ReplaceOptions options);

    CompletionStage<UpdateResult> updateOne(Bson filter, Bson update);

    CompletionStage<UpdateResult> updateOne(Bson filter, Bson update, UpdateOptions options);

    CompletionStage<UpdateResult> updateOne(ClientSession clientSession, Bson filter, Bson update);

    CompletionStage<UpdateResult> updateOne(ClientSession clientSession, Bson filter, Bson update, UpdateOptions options);

    CompletionStage<UpdateResult> updateMany(Bson filter, Bson update);

    CompletionStage<UpdateResult> updateMany(Bson filter, Bson update, UpdateOptions options);

    CompletionStage<UpdateResult> updateMany(ClientSession clientSession, Bson filter, Bson update);

    CompletionStage<UpdateResult> updateMany(ClientSession clientSession, Bson filter, Bson update, UpdateOptions options);

    CompletionStage<T> findOneAndDelete(Bson filter);

    CompletionStage<T> findOneAndDelete(Bson filter, FindOneAndDeleteOptions options);

    CompletionStage<T> findOneAndDelete(ClientSession clientSession, Bson filter);

    CompletionStage<T> findOneAndDelete(ClientSession clientSession, Bson filter, FindOneAndDeleteOptions options);

    CompletionStage<T> findOneAndReplace(Bson filter, T replacement);

    CompletionStage<T> findOneAndReplace(Bson filter, T replacement, FindOneAndReplaceOptions options);

    CompletionStage<T> findOneAndReplace(ClientSession clientSession, Bson filter, T replacement);

    CompletionStage<T> findOneAndReplace(ClientSession clientSession, Bson filter, T replacement,
            FindOneAndReplaceOptions options);

    CompletionStage<T> findOneAndUpdate(Bson filter, Bson update);

    CompletionStage<T> findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options);

    CompletionStage<T> findOneAndUpdate(ClientSession clientSession, Bson filter, Bson update);

    CompletionStage<T> findOneAndUpdate(ClientSession clientSession, Bson filter, Bson update, FindOneAndUpdateOptions options);

    CompletionStage<Void> drop();

    CompletionStage<Void> drop(ClientSession clientSession);

    CompletionStage<String> createIndex(Bson key);

    CompletionStage<String> createIndex(Bson key, IndexOptions options);

    CompletionStage<String> createIndex(ClientSession clientSession, Bson key);

    CompletionStage<String> createIndex(ClientSession clientSession, Bson key, IndexOptions options);

    CompletionStage<List<String>> createIndexes(List<IndexModel> indexes);

    CompletionStage<List<String>> createIndexes(List<IndexModel> indexes, CreateIndexOptions createIndexOptions);

    CompletionStage<List<String>> createIndexes(ClientSession clientSession, List<IndexModel> indexes);

    CompletionStage<List<String>> createIndexes(ClientSession clientSession, List<IndexModel> indexes,
            CreateIndexOptions createIndexOptions);

    ListIndexesPublisher<Document> listIndexesAsPublisher();

    <D> ListIndexesPublisher<D> listIndexesAsPublisher(Class<D> clazz);

    ListIndexesPublisher<Document> listIndexesAsPublisher(ClientSession clientSession);

    <D> ListIndexesPublisher<D> listIndexesAsPublisher(ClientSession clientSession, Class<D> clazz);

    PublisherBuilder<Document> listIndexes();

    <D> PublisherBuilder<D> listIndexes(Class<D> clazz);

    PublisherBuilder<Document> listIndexes(ClientSession clientSession);

    <D> PublisherBuilder<D> listIndexes(ClientSession clientSession, Class<D> clazz);

    CompletionStage<Void> dropIndex(String indexName);

    CompletionStage<Void> dropIndex(Bson keys);

    CompletionStage<Void> dropIndex(String indexName, DropIndexOptions dropIndexOptions);

    CompletionStage<Void> dropIndex(Bson keys, DropIndexOptions dropIndexOptions);

    CompletionStage<Void> dropIndex(ClientSession clientSession, String indexName);

    CompletionStage<Void> dropIndex(ClientSession clientSession, Bson keys);

    CompletionStage<Void> dropIndex(ClientSession clientSession, String indexName, DropIndexOptions dropIndexOptions);

    CompletionStage<Void> dropIndex(ClientSession clientSession, Bson keys, DropIndexOptions dropIndexOptions);

    CompletionStage<Void> dropIndexes();

    CompletionStage<Void> dropIndexes(DropIndexOptions dropIndexOptions);

    CompletionStage<Void> dropIndexes(ClientSession clientSession);

    CompletionStage<Void> dropIndexes(ClientSession clientSession, DropIndexOptions dropIndexOptions);

    CompletionStage<Void> renameCollection(MongoNamespace newCollectionNamespace);

    CompletionStage<Void> renameCollection(MongoNamespace newCollectionNamespace, RenameCollectionOptions options);

    CompletionStage<Void> renameCollection(ClientSession clientSession, MongoNamespace newCollectionNamespace);

    CompletionStage<Void> renameCollection(ClientSession clientSession, MongoNamespace newCollectionNamespace,
            RenameCollectionOptions options);
}
