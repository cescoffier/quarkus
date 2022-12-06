package io.quarkus.vertx.http.runtime.cors;

import java.util.HashSet;
import java.util.Locale;

import org.jboss.logging.Logger;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;

public class CORSFilter implements Handler<RoutingContext> {

    // This is set in the recorder at runtime.
    // Must be static because the filter is created(deployed) at build time and runtime config is still not available
    final CORSConfig corsConfig;
    private final CorsHandler corsHandler;

    public CORSFilter(CORSConfig corsConfig) {
        this.corsConfig = corsConfig;

        var maybeTheListOfOrigins = corsConfig.origins;
        CorsHandler handler;
        if (maybeTheListOfOrigins.isEmpty() || maybeTheListOfOrigins.get().isEmpty()) {
            handler = CorsHandler.create();
        } else {
            // The list of origins is configured.
            var list = maybeTheListOfOrigins.get();
            if (list.size() == 1) {
                handler = CorsHandler.create(list.get(0));
            } else {
                // check if list contains "*", if so, just use "*"
                if (list.contains("*")) {
                    Logger.getLogger(CORSFilter.class.getName())
                            .warnf("The CORS list of origin is mixing '*' and explicit origins: %s - using '*'", list);
                    handler = CorsHandler.create();
                } else {
                    handler = CorsHandler.create();
                    for (String o : list) {
                        handler.addOrigin(o);
                    }
                }
            }
        }

        if (corsConfig.methods.isPresent() && !corsConfig.methods.get().isEmpty()) {
            for (String method : corsConfig.methods.get()) {
                handler.allowedMethod(HttpMethod.valueOf(method.toUpperCase(Locale.ROOT)));
            }
        }

        if (corsConfig.headers.isPresent() && !corsConfig.headers.get().isEmpty()) {
            handler.allowedHeaders(new HashSet<>(corsConfig.headers.get()));
        }

        if (corsConfig.exposedHeaders.isPresent() && !corsConfig.exposedHeaders.get().isEmpty()) {
            handler.exposedHeaders(new HashSet<>(corsConfig.exposedHeaders.get()));
        }

        if (corsConfig.accessControlAllowCredentials.isPresent() && corsConfig.accessControlAllowCredentials.get()) {
            handler.allowCredentials(true);
        }

        if (corsConfig.accessControlMaxAge.isPresent()) {
            handler.maxAgeSeconds((int) corsConfig.accessControlMaxAge.get().toSeconds());
        }

        // TODO Private network?

        this.corsHandler = handler;
    }

    @Override
    public void handle(RoutingContext event) {
        corsHandler.handle(event);
    }
}
