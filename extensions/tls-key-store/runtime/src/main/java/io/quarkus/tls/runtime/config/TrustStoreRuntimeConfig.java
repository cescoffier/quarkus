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
     * The path to the trust store file.
     * For trust store composed of multiple files (like a set of {@code PEM} files), use {@link #paths}
     *
     * You cannot use {@code #path} and {@link #paths} together.
     */
    @ConfigItem
    public Optional<String> path = Optional.empty();

    /**
     * The paths to the trust store files.
     * This attribute is used for trust store composed of multiple files (like a set of {@code PEM} files).
     * If your key store has a single file, use {@link #path}
     *
     * You cannot use {@link #path} and {@code paths} together.
     */
    @ConfigItem
    public Optional<List<String>> paths = Optional.empty();

    /**
     * Extra parameters that will be passed to the trust store factory.
     */
    @ConfigItem
    public Map<String, String> extraParameters = Collections.emptyMap();


    public void validate(String bucket) {
        if (path.isPresent()  && paths.isPresent()) {
            throw new ConfigurationException("The TLS trust store " + bucket + " cannot contain `path` and `paths`");
        }
        if (path.isEmpty()  && paths.isEmpty()) {
            throw new ConfigurationException("The TLS trust store " + bucket + " must contain either `path` or `paths`");
        }
        if (type.isEmpty()) {
            if (path.isPresent()) {
                type = Optional.ofNullable(TlsBucketUtil.findStoreType(path.get()));
            } else if (! paths.get().isEmpty())  {
                type = Optional.ofNullable(TlsBucketUtil.findStoreType(paths.get().get(0)));
            }
            if (type.isEmpty()) {
                throw new ConfigurationException("Unable to detect the trust store type for the TLS configuration "
                        + bucket + ". You must explicitly set the '"
                        + TlsBucketUtil.getAttribute(bucket, "trust-store", "type") +"' property");
            }
        }
    }
}
