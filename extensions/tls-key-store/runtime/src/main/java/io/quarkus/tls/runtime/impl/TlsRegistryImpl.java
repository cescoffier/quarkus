package io.quarkus.tls.runtime.impl;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;

import io.quarkus.tls.api.TlsKeyStore;
import io.quarkus.tls.api.TlsRegistry;
import io.quarkus.tls.api.TlsTrustStore;

@ApplicationScoped
@Typed(TlsRegistry.class)
public class TlsRegistryImpl implements TlsRegistry {

    private final Map<String, TlsKeyStore> keyStores = new ConcurrentHashMap<>();
    private final Map<String, TlsTrustStore> trustStores = new ConcurrentHashMap<>();

    private TlsKeyStore defaultKeyStore;
    private TlsTrustStore defaultTrustStore;

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

    @Override
    public void register(String name, TlsKeyStore ks, TlsTrustStore ts) {
        // TODO Synchronization protocol
        if (TlsBucketUtil.isDefault(name)) {
            this.defaultKeyStore = ks;
            this.defaultTrustStore = ts;
        } else {
            keyStores.put(name, ks);
            trustStores.put(name, ts);
        }
    }

    @Override
    public void register(String name, TlsKeyStore ks) {
        // TODO Synchronization protocol
        if (TlsBucketUtil.isDefault(name)) {
            this.defaultKeyStore = ks;
        } else {
            keyStores.put(name, ks);
        }
    }

    @Override
    public void register(String name, TlsTrustStore ts) {
        // TODO Synchronization protocol
        if (TlsBucketUtil.isDefault(name)) {
            this.defaultTrustStore = ts;
        } else {
            trustStores.put(name, ts);
        }
    }

    public Optional<TlsKeyStore> getDefaultKeyStore() {
        return Optional.ofNullable(defaultKeyStore);
    }

}
