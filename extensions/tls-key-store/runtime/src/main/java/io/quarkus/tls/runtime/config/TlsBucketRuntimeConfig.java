package io.quarkus.tls.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.configuration.ConfigurationException;

import java.util.Optional;

/**
 * Configure a pair of key store / trust store.
 * If must at least contains one element.
 */
@ConfigGroup
public class TlsBucketRuntimeConfig {

    /**
     * Configures a key store.
     */
    @ConfigItem
    public Optional<KeyStoreRuntimeConfig> keyStore;

    /**
     * Configures a trust store.
     */
    public Optional<TrustStoreRuntimeConfig> trustStore;

    /**
     * Validate the basic configuration.
     */
    public void validate(String bucket) {
        if (keyStore.isEmpty()  && trustStore.isEmpty()) {
            throw new ConfigurationException("At least a trust-store or a key-store must be configured in the TLS configuration " + bucket);
        }
        keyStore.ifPresent(cfg -> cfg.validate(bucket));
        trustStore.ifPresent(cfg -> cfg.validate(bucket));
    }

}
