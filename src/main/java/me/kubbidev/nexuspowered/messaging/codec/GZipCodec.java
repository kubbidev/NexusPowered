package me.kubbidev.nexuspowered.messaging.codec;

import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * A {@link Codec} wrapper using GZip.
 *
 * @param <M> the message type
 */
public class GZipCodec<M> implements Codec<M> {
    private final Codec<M> delegate;

    public GZipCodec(Codec<M> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte[] encode(M message) throws EncodingException {
        byte[] in = this.delegate.encode(message);

        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut)) {
            gzipOut.write(in);
        } catch (IOException e) {
            throw new EncodingException(e);
        }
        return byteOut.toByteArray();
    }

    @Override
    public M decode(byte[] buf) throws EncodingException {
        byte[] uncompressed;
        try (GZIPInputStream gzipIn = new GZIPInputStream(new ByteArrayInputStream(buf))) {
            uncompressed = ByteStreams.toByteArray(gzipIn);
        } catch (IOException e) {
            throw new EncodingException(e);
        }
        return this.delegate.decode(uncompressed);
    }
}