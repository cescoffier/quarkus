package io.quarkus.netty.runtime.graal;

import static io.netty.handler.codec.http.HttpHeaderValues.DEFLATE;
import static io.netty.handler.codec.http.HttpHeaderValues.GZIP;
import static io.netty.handler.codec.http.HttpHeaderValues.X_DEFLATE;
import static io.netty.handler.codec.http.HttpHeaderValues.X_GZIP;

import java.util.function.BooleanSupplier;

import com.oracle.svm.core.annotate.*;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.compression.*;
import io.netty.handler.codec.http2.CompressorHttp2ConnectionEncoder;
import io.netty.handler.codec.http2.Http2Exception;

public class HttpContentCompressorSubstitutions {

    @TargetClass(className = "io.netty.handler.codec.compression.ZstdEncoder", onlyWith = IsZstdAbsent.class)
    public static final class ZstdEncoderFactorySubstitution {

        @Substitute
        protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Substitute
        protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
            throw new UnsupportedOperationException();
        }

        @Substitute
        public void flush(final ChannelHandlerContext ctx) {
            throw new UnsupportedOperationException();
        }
    }

    @TargetClass(className = "io.netty.handler.codec.compression.BrotliEncoder", onlyWith = IsBrotliAbsent.class)
    public static final class BrEncoderFactorySubstitution {

        @Substitute
        protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Substitute
        protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) {
            throw new UnsupportedOperationException();
        }
    }

    // TODO move brotli subs

    @TargetClass(Brotli.class)
    public static final class BrotliSubstitute { // TODO Only if brotli not available
        @Substitute
        public static boolean isAvailable() {
            return false;
        }
    }

    //    @TargetClass(HttpContentCompressor.class)
    //    public static final class HttpContentCompressorSubstitute {
    //
    //        @Alias
    //        boolean supportsCompressionOptions;
    //
    //        @Substitute
    //        @Delete
    //        BrotliOptions brotliOptions;
    //
    //        @Alias
    //        GzipOptions gzipOptions;
    //
    //        @Alias
    //        DeflateOptions deflateOptions;
    //
    //        @Substitute
    //        @Delete
    //        ZstdOptions zstdOptions;
    //
    //        @Alias
    //        int compressionLevel;
    //        @Alias
    //        int windowBits;
    //        @Alias
    //        int memLevel;
    //        @Alias
    //        int contentSizeThreshold;
    //        @Alias
    //        ChannelHandlerContext ctx;
    //        @Alias
    //        Map factories;
    //
    //        @Substitute
    //        @TargetElement(name = "<init>")
    //        public HttpContentCompressorSubstitute(int contentSizeThreshold, CompressionOptions... compressionOptions) {
    //            this.contentSizeThreshold = ObjectUtil.checkPositiveOrZero(contentSizeThreshold, "contentSizeThreshold");
    //            GzipOptions gzipOptions = null;
    //            DeflateOptions deflateOptions = null;
    //            ZstdOptions zstdOptions = null;
    //            if (compressionOptions == null || compressionOptions.length == 0) {
    //                gzipOptions = StandardCompressionOptions.gzip();
    //                deflateOptions = StandardCompressionOptions.deflate();
    //            } else {
    //                ObjectUtil.deepCheckNotNull("compressionOptions", compressionOptions);
    //                for (CompressionOptions compressionOption : compressionOptions) {
    //                    if (compressionOption instanceof GzipOptions) {
    //                        gzipOptions = (GzipOptions) compressionOption;
    //                    } else if (compressionOption instanceof DeflateOptions) {
    //                        deflateOptions = (DeflateOptions) compressionOption;
    //                    } else {
    //                        throw new IllegalArgumentException("Unsupported " + CompressionOptions.class.getSimpleName() +
    //                                ": " + compressionOption);
    //                    }
    //                }
    //            }
    //
    //            this.gzipOptions = gzipOptions;
    //            this.deflateOptions = deflateOptions;
    //
    //            this.factories = new HashMap<>();
    //
    //            if (this.gzipOptions != null) {
    //                this.factories.put("gzip", new GzipEncoderFactorySubstitute());
    //            }
    //            if (this.deflateOptions != null) {
    //                this.factories.put("deflate", new DeflateEncoderFactorySubstitute());
    //            }
    //
    //            this.compressionLevel = -1;
    //            this.windowBits = -1;
    //            this.memLevel = -1;
    //            supportsCompressionOptions = true;
    //        }
    //
    //        @Substitute
    //        protected String determineEncoding(String acceptEncoding) {
    //            float starQ = -1.0f;
    //            float brQ = -1.0f;
    //            float zstdQ = -1.0f;
    //            float gzipQ = -1.0f;
    //            float deflateQ = -1.0f;
    //            for (String encoding : acceptEncoding.split(",")) {
    //                float q = 1.0f;
    //                int equalsPos = encoding.indexOf('=');
    //                if (equalsPos != -1) {
    //                    try {
    //                        q = Float.parseFloat(encoding.substring(equalsPos + 1));
    //                    } catch (NumberFormatException e) {
    //                        // Ignore encoding
    //                        q = 0.0f;
    //                    }
    //                }
    //                if (encoding.contains("*")) {
    //                    starQ = q;
    //                } else if (encoding.contains("br") && q > brQ) {
    //                    brQ = q;
    //                } else if (encoding.contains("zstd") && q > zstdQ) {
    //                    zstdQ = q;
    //                } else if (encoding.contains("gzip") && q > gzipQ) {
    //                    gzipQ = q;
    //                } else if (encoding.contains("deflate") && q > deflateQ) {
    //                    deflateQ = q;
    //                }
    //            }
    //            if (brQ > 0.0f || zstdQ > 0.0f || gzipQ > 0.0f || deflateQ > 0.0f) {
    //                if (gzipQ != -1.0f && gzipQ >= deflateQ && this.gzipOptions != null) {
    //                    return "gzip";
    //                } else if (deflateQ != -1.0f && this.deflateOptions != null) {
    //                    return "deflate";
    //                }
    //            }
    //            if (starQ > 0.0f) {
    //                if (gzipQ == -1.0f && this.gzipOptions != null) {
    //                    return "gzip";
    //                }
    //                if (deflateQ == -1.0f && this.deflateOptions != null) {
    //                    return "deflate";
    //                }
    //            }
    //            return null;
    //        }
    //    }
    //
    //    @TargetClass(value = HttpContentCompressor.class, innerClass = "GzipEncoderFactory")
    //    public static final class GzipEncoderFactorySubstitute {
    //
    //    }
    //
    //    @TargetClass(value = HttpContentCompressor.class, innerClass = "DeflateEncoderFactory")
    //    public static final class DeflateEncoderFactorySubstitute {
    //
    //    }
    //
    //    @TargetClass(value = CompressorHttp2ConnectionEncoder.class)
    //    public static final class CompressorHttp2ConnectionEncoderSubstitute {
    //
    //        @Delete
    //        BrotliOptions brotliOptions;
    //        @Alias
    //        GzipOptions gzipCompressionOptions;
    //        @Alias
    //        DeflateOptions deflateOptions;
    //        @Delete
    //        ZstdOptions zstdOptions;
    //
    //        @Substitute
    //        static CompressionOptions[] defaultCompressionOptions() {
    //            return new CompressionOptions[] { StandardCompressionOptions.gzip(), StandardCompressionOptions.deflate() };
    //        }
    //
    //    }

    @TargetClass(CompressorHttp2ConnectionEncoder.class)
    public static final class CompressorHttp2ConnectionSubstitute {

        @Substitute
        protected EmbeddedChannel newContentCompressor(ChannelHandlerContext ctx, CharSequence contentEncoding)
                throws Http2Exception {
            if (GZIP.contentEqualsIgnoreCase(contentEncoding) || X_GZIP.contentEqualsIgnoreCase(contentEncoding)) {
                return newCompressionChannel(ctx, ZlibWrapper.GZIP);
            }
            if (DEFLATE.contentEqualsIgnoreCase(contentEncoding) || X_DEFLATE.contentEqualsIgnoreCase(contentEncoding)) {
                return newCompressionChannel(ctx, ZlibWrapper.ZLIB);
            }
            // 'identity' or unsupported
            return null;
        }

        @Alias
        private EmbeddedChannel newCompressionChannel(final ChannelHandlerContext ctx, ZlibWrapper wrapper) {
            throw new UnsupportedOperationException();
        }
    }

    public static class IsZstdAbsent implements BooleanSupplier {

        private boolean zstdAbsent;

        public IsZstdAbsent() {
            try {
                Class.forName("com.github.luben.zstd.Zstd");
                zstdAbsent = false;
            } catch (ClassNotFoundException e) {
                zstdAbsent = true;
            }
        }

        @Override
        public boolean getAsBoolean() {
            return zstdAbsent;
        }
    }

    public static class IsBrotliAbsent implements BooleanSupplier {

        private boolean brotliAbsent;

        public IsBrotliAbsent() {
            try {
                Class.forName("com.aayushatharva.brotli4j.encoder.Encoder");
                brotliAbsent = false;
            } catch (ClassNotFoundException e) {
                brotliAbsent = true;
            }
        }

        @Override
        public boolean getAsBoolean() {
            return brotliAbsent;
        }
    }
}
