package io.quarkus.tls.api;

import java.util.Optional;

public interface TlsRegistry {

    void register(String name, TlsKeyStore ks, TlsTrustStore ts);

    void register(String name, TlsKeyStore ks);

    void register(String name,TlsTrustStore ts);

    Optional<TlsKeyStore> getDefaultKeyStore();

    Optional<TlsTrustStore> getDefaultTrustStore();

    Optional<TlsKeyStore> getKeyStore(String name);

    Optional<TlsTrustStore> getTrustStore(String name);
}
