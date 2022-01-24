package io.quarkus.tls.runtime.impl;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import io.netty.handler.ssl.PemPrivateKey;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.runtime.TlsKeyStore;
import io.quarkus.tls.runtime.config.KeyStoreRuntimeConfig;
import io.quarkus.tls.runtime.spi.TlsKeyStoreFactory;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.mutiny.core.Vertx;

@ApplicationScoped
@Typed(TlsKeyStoreFactory.class)
public class PemKeyStoreFactory implements TlsKeyStoreFactory {

    private static final String TYPE = "PEM";
    private final Vertx vertx;

    @Inject
    public PemKeyStoreFactory(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public TlsKeyStore create(String name, KeyStoreRuntimeConfig config) {
        PemKeyStore store = new PemKeyStore(name, config);
        store.validate();
        return store;
    }

    private class PemKeyStore implements TlsKeyStore {

        private final String name;
        private final KeyStoreRuntimeConfig config;

        public PemKeyStore(String name, KeyStoreRuntimeConfig config) {
            this.name = name;
            this.config = config;
        }

        @Override
        public String getType() {
            return PemKeyStoreFactory.this.type();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getProvider() {
            return null;
        }

        @Override
        public String getKey() {
            return config.key;
        }

        @Override
        public Optional<String> getCert() {
            return config.cert;
        }

        @Override
        public Optional<String> getAlias() {
            // Not supported by PEM file
            return Optional.empty();
        }

        @Override
        public Optional<String> getAliasPassword() {
            // Not supported by PEM file
            return Optional.empty();
        }

        @Override
        public Optional<String> getPassword() {
            // Not supported by PEM file
            return Optional.empty();
        }

        @Override
        public PemKeyCertOptions getVertxKeyStoreOptions() {
            return new PemKeyCertOptions()
                    .addKeyPath(getKey())
                    .addCertPath(getCert().orElseThrow());
        }

        public void validate() {
            // PEM specificities:
            // - no password or alias
            // - cert and key must be set
            if (config.password.isPresent()) {
                throw new ConfigurationException("Invalid PEM configuration " + name + ", PEM do not support `password`");
            }
            if (config.alias.isPresent()) {
                throw new ConfigurationException("Invalid PEM configuration " + name + ", PEM do not support `alias`");
            }

            if (getCert().isEmpty()) {
                throw new ConfigurationException("Invalid PEM configuration " + name + ", `cert` must be set");
            }

            VertxValidationUtil.validateKeyStore(name, this, () -> {
                try {
                    return getVertxKeyStoreOptions().loadKeyStore(vertx.getDelegate());
                } catch (Exception e) {
                    throw new ConfigurationException("Unable to load key store " + name, e);
                }
            });
        }
    }
}
