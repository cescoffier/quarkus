package io.quarkus.tls.runtime.impl;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.runtime.TlsKeyStore;
import io.quarkus.tls.runtime.config.KeyStoreRuntimeConfig;
import io.vertx.core.net.KeyStoreOptions;
import io.vertx.mutiny.core.Vertx;

import javax.enterprise.inject.spi.Producer;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Optional;
import java.util.function.Supplier;

public class VertxValidationUtil {

    private VertxValidationUtil() {
        // Avoid direct instantiation
    }

    static void validateKeyStore(String name, TlsKeyStore obj, Supplier<KeyStore> producer) {
        KeyStore store = producer.get();

        Optional<String> alias  = obj.getAlias();
        if (alias .isPresent()) {
            try {
                String value = alias.get();
                if (!store.containsAlias(value)  || !store.isKeyEntry(value)) {
                    throw new ConfigurationException("TLS key store " + name + " configured with an unknown alias: " + alias);
                }
            } catch (KeyStoreException e) {
                throw new ConfigurationException("Invalid TLS key store " + name, e);
            }
        }

        Optional<String> password = obj.getAliasPassword();
        if (password.isPresent()  && alias.isPresent()) {
            try {
                if (store.getKey(alias.get(), password.get().toCharArray()) == null) {
                    throw new ConfigurationException("Wrong alias / alias-password for key store " + name);
                }
            } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
                throw new ConfigurationException("Unable to read the key store " + name, e);
            }
        }
    }
}
