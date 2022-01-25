package io.quarkus.tls.runtime.spi;


import io.quarkus.tls.api.TlsTrustStore;
import io.quarkus.tls.runtime.config.TrustStoreRuntimeConfig;

public interface TlsTrustStoreFactory {

    String type();

    TlsTrustStore create(String name, TrustStoreRuntimeConfig config);

}
