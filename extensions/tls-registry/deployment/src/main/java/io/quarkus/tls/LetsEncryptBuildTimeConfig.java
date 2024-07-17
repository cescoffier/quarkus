package io.quarkus.tls;

import java.util.Optional;

import io.quarkus.runtime.annotations.ConfigPhase;
import io.quarkus.runtime.annotations.ConfigRoot;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

@ConfigMapping(prefix = "quarkus.tls.lets-encrypt")
@ConfigRoot(phase = ConfigPhase.BUILD_TIME)
public interface LetsEncryptBuildTimeConfig {

    /**
     * Set to {@code true} to enable let's encrypt support.
     */
    @WithDefault("false")
    boolean enabled();

    /**
     * The root path for the Let's Encrypt challenge.
     */
    Optional<String> rootPath();

}
