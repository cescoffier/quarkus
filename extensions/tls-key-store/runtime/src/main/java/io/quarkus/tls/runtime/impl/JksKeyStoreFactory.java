package io.quarkus.tls.runtime.impl;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.runtime.TlsKeyStore;
import io.quarkus.tls.runtime.config.KeyStoreRuntimeConfig;
import io.quarkus.tls.runtime.spi.TlsKeyStoreFactory;
import io.vertx.core.net.JksOptions;
import io.vertx.mutiny.core.Vertx;

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Optional;

public class JksKeyStoreFactory implements TlsKeyStoreFactory {

    private static final String TYPE = "JKS";

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
        public String getPath() {
            return config.path;
        }

        @Override
        public File getFile() {
            return new File(config.path);
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
                    .setPath(getPath())
                    .setPassword(getPassword().orElse(null));
        }

        public void validate() {
            Vertx vertx = Arc.container().instance(Vertx.class).get();
            KeyStore store;
            try {
                store = getVertxKeyStoreOptions().loadKeyStore(vertx.getDelegate());
            } catch (Exception e) {
                throw new ConfigurationException("Unable to read TLS key store " + name + " + configured to " + getPath(), e);
            }

            if (getAlias().isPresent()) {
                try {
                    String alias = getAlias().get();
                    if (!store.containsAlias(alias)  || !store.isKeyEntry(alias)) {
                        throw new ConfigurationException("TLS key store " + name + " configured with an unknown alias: " + alias);
                    }
                } catch (KeyStoreException e) {
                    throw new ConfigurationException("Invalid TLS key store " + name, e);
                }
            }

            if (getAliasPassword().isPresent()) {
                try {
                    if (store.getKey(getAlias().get(), getAliasPassword().get().toCharArray()) == null) {
                        throw new ConfigurationException("Wrong alias / alias-password for key store " + name);
                    }
                } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                    throw new ConfigurationException("Unable to read the key store " + name, e);
                }
            }
        }
    }
}
