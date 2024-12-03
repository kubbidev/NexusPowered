package me.kubbidev.nexuspowered.messaging.codec;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import me.kubbidev.nexuspowered.gson.GsonProvider;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of {@link Codec} using {@link Gson}.
 *
 * @param <M> the message type
 */
public class GsonCodec<M> implements Codec<M> {
    private final Gson gson;
    private final TypeToken<M> type;

    public GsonCodec(Gson gson, TypeToken<M> type) {
        this.gson = gson;
        this.type = type;
    }

    public GsonCodec(TypeToken<M> type) {
        this(GsonProvider.normal(), type);
    }

    @Override
    public byte[] encode(M message) throws EncodingException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        try (Writer writer = new OutputStreamWriter(byteOut, StandardCharsets.UTF_8)) {
            this.gson.toJson(message, this.type.getType(), writer);
        } catch (IOException e) {
            throw new EncodingException(e);
        }

        return byteOut.toByteArray();
    }

    @Override
    public M decode(byte[] buf) throws EncodingException {
        ByteArrayInputStream byteIn = new ByteArrayInputStream(buf);
        try (Reader reader = new InputStreamReader(byteIn, StandardCharsets.UTF_8)) {
            return this.gson.fromJson(reader, this.type.getType());
        } catch (IOException e) {
            throw new EncodingException(e);
        }
    }
}