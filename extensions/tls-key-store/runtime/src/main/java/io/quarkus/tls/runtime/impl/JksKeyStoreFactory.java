package io.quarkus.tls.runtime.impl;

import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.runtime.TlsKeyStore;
import io.quarkus.tls.runtime.config.KeyStoreRuntimeConfig;
import io.quarkus.tls.runtime.spi.TlsKeyStoreFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.mutiny.core.Vertx;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;
import java.util.Optional;

@ApplicationScoped
@Typed(TlsKeyStoreFactory.class)
public class JksKeyStoreFactory implements TlsKeyStoreFactory {

    private static final String TYPE = "JKS";
    private final Vertx vertx;

    @Inject
    public JksKeyStoreFactory(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public TlsKeyStore create(String name, KeyStoreRuntimeConfig config) {
        JksKeyStore store = new JksKeyStore(name, config);
        store.validate();
        return store;
    }

    private class JksKeyStore implements TlsKeyStore {

        private final String name;
        private final KeyStoreRuntimeConfig config;

        public JksKeyStore(String name, KeyStoreRuntimeConfig config) {
            this.name = name;
            this.config = config;
        }

        @Override
        public String getType() {
            return JksKeyStoreFactory.this.type();
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
            // Not supported for JKS, everything is in the same file.
            return Optional.empty();
        }

        @Override
        public Optional<String> getAlias() {
            return config.alias;
        }

        @Override
        public Optional<String> getAliasPassword() {
            return config.aliasPassword;
        }

        @Override
        public Optional<String> getPassword() {
            return config.password;
        }

        @Override
        public JksOptions getVertxKeyStoreOptions() {
            return new JksOptions()
                    .setAlias(getAlias().orElse(null))
                    .setAliasPassword(getAliasPassword().orElse(null))
                    .setPath(getKey())
                    .setPassword(getPassword().orElse(null));
        }

        public void validate() {
            if (config.cert.isPresent()) {
                throw new ConfigurationException("The JKS key store " + name + " does not support the `cert` attribute as JKS files contain both the key and the certificate");
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
