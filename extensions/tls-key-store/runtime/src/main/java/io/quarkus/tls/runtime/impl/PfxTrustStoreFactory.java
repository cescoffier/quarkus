package io.quarkus.tls.runtime.impl;

import java.io.File;
import java.util.Optional;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.runtime.TlsTrustStore;
import io.quarkus.tls.runtime.config.TrustStoreRuntimeConfig;
import io.quarkus.tls.runtime.spi.TlsTrustStoreFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PfxOptions;
import io.vertx.mutiny.core.Vertx;

public class PfxTrustStoreFactory implements TlsTrustStoreFactory {

    private static final String TYPE = "P12";

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public TlsTrustStore create(String name, TrustStoreRuntimeConfig config) {
        PfxTrustStore store = new PfxTrustStore(name, config);
        store.validate();
        return store;
    }

    private class PfxTrustStore implements TlsTrustStore {

        private final String name;
        private final TrustStoreRuntimeConfig config;

        public PfxTrustStore(String name, TrustStoreRuntimeConfig config) {
            this.name = name;
            this.config = config;
        }

        @Override
        public String getType() {
            return PfxTrustStoreFactory.this.type();
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
        public String getPath() {
            return config.path.orElseThrow();
        }

        @Override
        public File getFile() {
            return new File(config.path.orElseThrow());
        }

        @Override
        public Optional<String> getPassword() {
            return config.password;
        }

        @Override
        public PfxOptions getVertxTrustStoreOptions() {
            return new PfxOptions()
                    .setPath(getPath())
                    .setPassword(getPassword().orElse(null));
        }

        public void validate() {
            if (config.path.isEmpty()) {
                throw new ConfigurationException("The " + TlsBucketUtil.getAttribute(name, "trust-store", "path") + " must be set for P12 trust store");
            }

            Vertx vertx = Arc.container().instance(Vertx.class).get();
            try {
                // Just verify it can be loaded.
                getVertxTrustStoreOptions().loadKeyStore(vertx.getDelegate());
            } catch (Exception e) {
                throw new ConfigurationException("Unable to read TLS key store " + name + " + configured to " + getPath(), e);
            }

        }
    }
}
