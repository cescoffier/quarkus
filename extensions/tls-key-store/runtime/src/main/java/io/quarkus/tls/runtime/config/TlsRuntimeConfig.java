package io.quarkus.tls.runtime.config;

import io.quarkus.runtime.annotations.ConfigDocMapKey;
import io.quarkus.runtime.annotations.ConfigDocSection;
import io.quarkus.runtime.annotations.ConfigItem;
import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.quarkus.runtime.configuration.ConfigurationException;
import io.quarkus.tls.runtime.impl.TlsBucketUtil;

import java.util.Map;

@ConfigRoot(name = "tls", phase = ConfigPhase.RUN_TIME)
public class TlsRuntimeConfig {

    /**
     * The default TLS configuration bucket.
     */
    @ConfigItem(name = ConfigItem.PARENT)
    public TlsBucketRuntimeConfig defaultBucket;

    /**
     * Additional named TLS configuration buckets.
     */
    @ConfigDocSection
    @ConfigDocMapKey("bucket-name")
    @ConfigItem(name = ConfigItem.PARENT)
    public Map<String, TlsBucketRuntimeConfig> namedBuckets;

    public TlsBucketRuntimeConfig getTlsBucketRuntimeConfig(String bucketName) {
        if (TlsBucketUtil.isDefault(bucketName)) {
            return defaultBucket;
        }

        TlsBucketRuntimeConfig dataSourceRuntimeConfig = namedBuckets.get(bucketName);
        if (dataSourceRuntimeConfig == null) {
            throw new ConfigurationException("Unknown TLS configuration: " + bucketName);
        }

        return dataSourceRuntimeConfig;
    }

}
