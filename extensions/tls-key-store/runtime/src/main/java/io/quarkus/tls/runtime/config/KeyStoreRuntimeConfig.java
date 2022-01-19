package io.quarkus.tls.runtime.config;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.runtime.impl.TlsBucketUtil;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Configure a key store.
 */
@ConfigGroup
public class KeyStoreRuntimeConfig {

    /**
     * The key store password if any
     */
    @ConfigItem
    public Optional<String> password = Optional.empty();

    /**
     * The key store type.
     * If not set, a guess is made from the file extension.
     */
    @ConfigItem
    public Optional<String> type = Optional.empty();

    /**
     * The path to the key store file.
     */
    @ConfigItem
    public String path;

    /**
     * The key alias if the key store contains multiple keys.
     */
    @ConfigItem
    public Optional<String> alias = Optional.empty();

    /**
     * The key alias password if the key store contains multiple keys and if {@link  #alias} is set.
     */
    @ConfigItem
    public Optional<String> aliasPassword = Optional.empty();

    /**
     * Extra parameters that will be passed to the key store factory.
     */
    @ConfigItem
    public Map<String, String> extraParameters = Collections.emptyMap();


    public void validate(String bucket) {
        // If type is not set, tries to detect it from the file name.
        if (type.isEmpty()) {
            type = Optional.ofNullable(TlsBucketUtil.findStoreType(path));
            if (type.isEmpty()) {
                throw new ConfigurationException("Unable to detect the key store type for the TLS configuration "
                        + bucket + ". You must explicitly set the '"
                        + TlsBucketUtil.getAttribute(bucket, "key-store", "type") +"' property");
            }
        }

        if (aliasPassword.isPresent()  && alias.isEmpty()) {
            throw new ConfigurationException("The TLS key store configuration " + bucket + " contains " +
                    "an 'alias-password' without 'alias'");
        }
    }
}
