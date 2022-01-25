package io.quarkus.tls.runtime.impl.factories;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.api.TlsKeyStore;
import io.quarkus.tls.runtime.config.KeyStoreRuntimeConfig;
import io.quarkus.tls.runtime.spi.TlsKeyStoreFactory;
import io.vertx.core.net.PfxOptions;
import io.vertx.mutiny.core.Vertx;

@ApplicationScoped
@Typed(TlsKeyStoreFactory.class)
public class PfxKeyStoreFactory implements TlsKeyStoreFactory {

    private static final String TYPE = "P12";
    private final Vertx vertx;

    @Inject
    public PfxKeyStoreFactory(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public TlsKeyStore create(String name, KeyStoreRuntimeConfig config) {
        PfxKeyStore store = new PfxKeyStore(name, config);
        store.validate();
        return store;
    }

    private class PfxKeyStore implements TlsKeyStore {

        private final String name;
        private final KeyStoreRuntimeConfig config;

        public PfxKeyStore(String name, KeyStoreRuntimeConfig config) {
            this.name = name;
            this.config = config;
        }

        @Override
        public String getType() {
            return PfxKeyStoreFactory.this.type();
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
        public PfxOptions getVertxKeyStoreOptions() {
            return new PfxOptions()
                    .setAlias(getAlias().orElse(null))
                    .setAliasPassword(getAliasPassword().orElse(null))
                    .setPath(getKey())
                    .setPassword(getPassword().orElse(null));
        }

        public void validate() {
            if (config.cert.isPresent()) {
                throw new ConfigurationException("The P12 key store " + name
                        + " does not support the `cert` attribute as P12 key stores contain both the key and the certificate");
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
