package io.quarkus.tls.runtime;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import io.vertx.core.net.TrustOptions;

/**
 * Structure storing the TLS Trust store.
 */
public interface TlsTrustStore {

    String getType();

    String getName();

    String getProvider();

    String getPath();

    default List<String> getPaths() {
        if (getPath() != null) {
            return List.of(getPath());
        }
        return Collections.emptyList();
    }

    File getFile();

    default List<File> getFiles() {
        if (getFile() != null) {
            return List.of(getFile());
        }
        return Collections.emptyList();
    }


    Optional<String> getPassword();

    default TrustOptions getVertxTrustStoreOptions() {
        throw new UnsupportedOperationException("Unsupported operation for key store of type " + getType());
    }
}
