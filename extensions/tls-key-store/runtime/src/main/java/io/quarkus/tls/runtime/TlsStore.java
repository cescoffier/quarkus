package io.quarkus.tls.runtime;

import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.runtime.config.KeyStoreRuntimeConfig;
import io.quarkus.tls.runtime.config.TlsBucketRuntimeConfig;
import io.quarkus.tls.runtime.config.TrustStoreRuntimeConfig;
import io.quarkus.tls.runtime.impl.TlsBucketUtil;
import io.quarkus.tls.runtime.spi.TlsKeyStoreFactory;
import io.quarkus.tls.runtime.spi.TlsTrustStoreFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;

@ApplicationScoped
public class TlsStore {

    // TODO Registration API that others extension can use - like an Acme extension.

    private final Map<String, TlsKeyStore> keyStores = new HashMap<>();
    private final Map<String, TlsTrustStore> trustStores = new HashMap<>();

    private TlsKeyStore defaultKeyStore;
    private TlsTrustStore defaultTrustStore;

    @Inject
    Instance<TlsKeyStoreFactory> keyStoreFactories;

    @Inject
    Instance<TlsTrustStoreFactory> trustStoreFactories;

    /**
     * Receives the user configuration.
     *
     * This method validates the input, looks for the store factory and create / store the objects.
     * @param defaultBucket the default bucket, may be null
     * @param others the other named buckets, may be empty
     */
    void load(TlsBucketRuntimeConfig defaultBucket, Map<String, TlsBucketRuntimeConfig> others) {
        if (defaultBucket != null) {
            defaultBucket.validate(TlsBucketUtil.DEFAULT_BUCKET_NAME);
            if (defaultBucket.keyStore.isPresent()) {
                KeyStoreRuntimeConfig config = defaultBucket.keyStore.get();
                String type = config.type.orElseThrow();
                TlsKeyStoreFactory factory = lookForKeyStoreFactory(type);
                if (factory == null) {
                    throw new ConfigurationException("Unsupported key store type '" + type + "' in the default TLS key store");
                }
                defaultKeyStore = factory.create(TlsBucketUtil.DEFAULT_BUCKET_NAME, config);
            }
            if (defaultBucket.trustStore.isPresent()) {
                TrustStoreRuntimeConfig config = defaultBucket.trustStore.get();
                String type = config.type.orElseThrow();
                TlsTrustStoreFactory factory = lookForTrustStoreFactory(type);
                if (factory == null) {
                    throw new ConfigurationException("Unsupported key store type '" + type + "' in the default TLS trust store");
                }
                defaultTrustStore = factory.create(TlsBucketUtil.DEFAULT_BUCKET_NAME, config);
            }
        }

        for (Map.Entry<String, TlsBucketRuntimeConfig> entry : others.entrySet()) {
            entry.getValue().validate(entry.getKey());
            if (entry.getValue().keyStore.isPresent()) {
                KeyStoreRuntimeConfig config = entry.getValue().keyStore.get();
                String type = config.type.orElseThrow();
                TlsKeyStoreFactory factory = lookForKeyStoreFactory(type);
                if (factory == null) {
                    throw new ConfigurationException("Unsupported key store type '" + type + "' in the " + entry.getKey() + " TLS key store");
                }
                keyStores.put(entry.getKey(), factory.create(TlsBucketUtil.DEFAULT_BUCKET_NAME, config));
            }
            if (entry.getValue().trustStore.isPresent()) {
                TrustStoreRuntimeConfig config = entry.getValue().trustStore.get();
                String type = config.type.orElseThrow();
                TlsTrustStoreFactory factory = lookForTrustStoreFactory(type);
                if (factory == null) {
                    throw new ConfigurationException("Unsupported key store type '" + type + "' in the " + entry.getKey() + " TLS trust store");
                }
                trustStores.put(entry.getKey(), factory.create(TlsBucketUtil.DEFAULT_BUCKET_NAME, config));
            }
        }
    }

    public Optional<TlsTrustStore> getTrustStore(String name) {
        if (TlsBucketUtil.isDefault(name)) {
            return Optional.ofNullable(defaultTrustStore);
        }
        return Optional.ofNullable(trustStores.get(name));
    }

    public Optional<TlsTrustStore> getDefaultTrustStore() {
        return Optional.ofNullable(defaultTrustStore);
    }

    public Optional<TlsKeyStore> getKeyStore(String name) {
        if (TlsBucketUtil.isDefault(name)) {
            return Optional.ofNullable(defaultKeyStore);
        }
        return Optional.ofNullable(keyStores.get(name));
    }

    public Optional<TlsKeyStore> getDefaultKeyStore() {
        return Optional.ofNullable(defaultKeyStore);
    }

    private TlsKeyStoreFactory lookForKeyStoreFactory(String type) {
        for (TlsKeyStoreFactory factory : keyStoreFactories) {
            if (factory.type().equalsIgnoreCase(type)) {
                return factory;
            }
        }
        return null;
    }

    private TlsTrustStoreFactory lookForTrustStoreFactory(String type) {
        for (TlsTrustStoreFactory factory : trustStoreFactories) {
            if (factory.type().equalsIgnoreCase(type)) {
                return factory;
            }
        }
        return null;
    }


}
