package io.quarkus.tls.runtime.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigGroup;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.runtime.impl.TlsBucketUtil;

/**
 * Configure a trust store.
 */
@ConfigGroup
public class TrustStoreRuntimeConfig {

    /**
     * The trust store password if any
     */
    @ConfigItem
    public Optional<String> password = Optional.empty();

    /**
     * The trust store type.
     * If not set, a guess is made from the file extension.
     */
    @ConfigItem
    public Optional<String> type = Optional.empty();

    /**
     * The paths to the trust store (certificate) files.
     */
    @ConfigItem
    public List<String> certs;

    /**
     * Extra parameters that will be passed to the trust store factory.
     */
    @ConfigItem
    public Map<String, String> extraParameters = Collections.emptyMap();


    public void validate(String bucket) {
        if (certs.isEmpty()) {
            throw new ConfigurationException("Expected cert-paths to be set in "  + bucket + " trust-store configuration");
        }
        if (type.isEmpty()) {
            type = Optional.ofNullable(TlsBucketUtil.findStoreType(certs.get(0)));
            if (type.isEmpty()) {
                throw new ConfigurationException("Unable to detect the trust store type for the TLS configuration "
                        + bucket + ". You must explicitly set the '"
                        + TlsBucketUtil.getAttribute(bucket, "trust-store", "type") +"' property");
            }
        }
    }
}
