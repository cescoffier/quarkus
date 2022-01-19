package io.quarkus.tls.runtime;

import io.vertx.core.net.KeyCertOptions;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Structure storing the TLS Key store.
 */
public interface TlsKeyStore {

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

    Optional<String> getAlias();

    Optional<String> getAliasPassword();

    Optional<String> getPassword();

    default KeyCertOptions getVertxKeyStoreOptions() {
        throw new UnsupportedOperationException("Unsupported operation for key store of type " + getType());
    }
}
