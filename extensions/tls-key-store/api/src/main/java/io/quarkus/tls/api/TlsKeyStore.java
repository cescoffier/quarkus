package io.quarkus.tls.api;

import java.util.Optional;

import io.vertx.core.net.KeyCertOptions;

/**
 * Structure storing the TLS Key store.
 */
public interface TlsKeyStore {

    String getType();

    String getName();

    String getProvider();

    String getKey();

    Optional<String> getCert();

    Optional<String> getAlias();

    Optional<String> getAliasPassword();

    Optional<String> getPassword();

    default KeyCertOptions getVertxKeyStoreOptions() {
        throw new UnsupportedOperationException("Unsupported operation for key store of type " + getType());
    }
}
