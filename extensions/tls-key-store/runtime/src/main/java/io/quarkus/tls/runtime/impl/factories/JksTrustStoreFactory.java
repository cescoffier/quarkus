package io.quarkus.tls.runtime.impl.factories;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.api.TlsTrustStore;
import io.quarkus.tls.runtime.config.TrustStoreRuntimeConfig;
import io.quarkus.tls.runtime.impl.TlsBucketUtil;
import io.quarkus.tls.runtime.spi.TlsTrustStoreFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.mutiny.core.Vertx;

@ApplicationScoped
@Typed(TlsTrustStoreFactory.class)
public class JksTrustStoreFactory implements TlsTrustStoreFactory {

    private static final String TYPE = "JKS";

    private final Vertx vertx;

    @Inject
    public JksTrustStoreFactory(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public TlsTrustStore create(String name, TrustStoreRuntimeConfig config) {
        JksTrustStore store = new JksTrustStore(name, config);
        store.validate();
        return store;
    }

    private class JksTrustStore implements TlsTrustStore {

        private final String name;
        private final TrustStoreRuntimeConfig config;

        public JksTrustStore(String name, TrustStoreRuntimeConfig config) {
            this.name = name;
            this.config = config;
        }

        @Override
        public String getType() {
            return JksTrustStoreFactory.this.type();
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
        public List<String> getCertificatePaths() {
            return config.certs;
        }

        @Override
        public Optional<String> getPassword() {
            return config.password;
        }

        @Override
        public JksOptions getVertxTrustStoreOptions() {
            return new JksOptions()
                    .setPath(getCertificatePaths().get(0))
                    .setPassword(getPassword().orElse(null));
        }

        public void validate() {
            if (config.certs.isEmpty()) {
                throw new ConfigurationException("The " + TlsBucketUtil.getAttribute(name, "trust-store", "cert-paths")
                        + " must be set for JKS trust store");
            }

            if (config.certs.size() > 1) {
                throw new ConfigurationException("The " + TlsBucketUtil.getAttribute(name, "trust-store", "cert-paths")
                        + " must contain a single path for JKS trust store");
            }

            try {
                // Just verify it can be loaded.
                getVertxTrustStoreOptions().loadKeyStore(vertx.getDelegate());
            } catch (Exception e) {
                throw new ConfigurationException(
                        "Unable to read TLS key store " + name + " + configured to " + getCertificatePaths(), e);
            }

        }
    }
}
