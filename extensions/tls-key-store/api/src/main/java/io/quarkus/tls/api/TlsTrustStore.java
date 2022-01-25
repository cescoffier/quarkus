package io.quarkus.tls.api;

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

    List<String> getCertificatePaths();

    Optional<String> getPassword();

    default TrustOptions getVertxTrustStoreOptions() {
        throw new UnsupportedOperationException("Unsupported operation for key store of type " + getType());
    }
}
