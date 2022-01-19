package io.quarkus.tls.runtime.impl;

import java.util.Collection;

public final class TlsBucketUtil {

    public static final String DEFAULT_BUCKET_NAME = "<default>";

    public static boolean isDefault(String dataSourceName) {
        return DEFAULT_BUCKET_NAME.equals(dataSourceName);
    }

    public static boolean hasDefault(Collection<String> dataSourceNames) {
        return dataSourceNames.contains(DEFAULT_BUCKET_NAME);
    }

    private TlsBucketUtil() {
    }

    public static String findStoreType(String path) {
        if (path.equals(".jks")) {
            return "jks";
        }
        if (path.endsWith(".p12")  || path.endsWith(".pfx")) {
            return "p12";
        }
        if (path.endsWith(".pem")) {
            return "pem";
        }
        return null;
    }

    public static String getAttribute(String bucket, String keyOrTrust, String attribute) {
        if (bucket.equals(DEFAULT_BUCKET_NAME)) {
            return "quarkus.tls." + keyOrTrust + "." + attribute;
        } else {
            return "quarkus.tls." + bucket + "." + keyOrTrust + "." + attribute;
        }
    }
}
