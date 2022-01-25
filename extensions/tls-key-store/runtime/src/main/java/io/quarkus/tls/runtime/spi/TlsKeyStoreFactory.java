package io.quarkus.tls.runtime.spi;

import io.quarkus.tls.api.TlsKeyStore;
import io.quarkus.tls.runtime.config.KeyStoreRuntimeConfig;

public interface TlsKeyStoreFactory {

    String type();

    TlsKeyStore create(String name, KeyStoreRuntimeConfig config);

}
