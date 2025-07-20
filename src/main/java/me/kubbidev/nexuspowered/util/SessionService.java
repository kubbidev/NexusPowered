package me.kubbidev.nexuspowered.util;

import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.UUID;
import me.kubbidev.nexuspowered.gson.GsonProvider;
import me.kubbidev.nexuspowered.util.logger.BukkitLoggerFactory;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public final class SessionService {

    private static final Logger LOGGER            = BukkitLoggerFactory.getLogger(SessionService.class);
    private static final String FROM_UUID_URL     = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";
    private static final String FROM_USERNAME_URL = "https://api.mojang.com/users/profiles/minecraft/%s";

    private SessionService() {
    }

    /**
     * Gets a {@link JsonObject} with the response from the mojang API
     *
     * @param uuid The UUID as a {@link UUID}
     * @return The {@link JsonObject} or {@code null} if the mojang API is down or the UUID is invalid
     */
    @Blocking
    public static @Nullable JsonObject fromUuid(@NotNull UUID uuid) {
        return fromUuid(uuid.toString());
    }

    /**
     * Gets a {@link JsonObject} with the response from the mojang API
     *
     * @param uuid The UUID as a {@link String}
     * @return The {@link JsonObject} or {@code null} if the mojang API is down or the UUID is invalid
     */
    @Blocking
    public static @Nullable JsonObject fromUuid(@NotNull String uuid) {
        try {
            return retrieve(FROM_UUID_URL.formatted(uuid));
        } catch (IOException | URISyntaxException e) {
            return null;
        }
    }

    /**
     * Gets a {@link JsonObject} with the response from the mojang API
     *
     * @param username The username as a {@link String}
     * @return The {@link JsonObject} or {@code null} if the mojang API is down or the username is invalid
     */
    @Blocking
    public static @Nullable JsonObject fromUsername(@NotNull String username) {
        try {
            return retrieve(FROM_USERNAME_URL.formatted(username));
        } catch (IOException | URISyntaxException e) {
            return null;
        }
    }

    /**
     * Gets the JsonObject from a URL, expects a mojang player URL so the errors might not make sense if it is not
     *
     * @param url The url to retrieve
     * @return The {@link JsonObject} of the result
     * @throws IOException with the text detailing the exception
     */
    private static @NotNull JsonObject retrieve(@NotNull String url) throws IOException, URISyntaxException {
        JsonObject response = SessionService.get(url);
        if (!response.has("errorMessage")) {
            return response;
        } else {
            throw new IOException(response.get("errorMessage").getAsString());
        }
    }

    public static @NotNull JsonObject get(String url) throws IOException, URISyntaxException {
        return get(new URI(url).toURL());
    }


    public static @NotNull JsonObject get(URL url) throws IOException {
        Objects.requireNonNull(url);
        LOGGER.debug("Connecting to {}", url);
        return readInputStream((HttpURLConnection) url.openConnection());
    }

    private static @NotNull JsonObject readInputStream(HttpURLConnection connection) throws IOException {
        InputStream inputStream;
        int status = connection.getResponseCode();
        if (status >= 200 && status <= 299) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }

        BufferedReader in = new BufferedReader(
            new InputStreamReader(inputStream));

        StringBuilder response = new StringBuilder();
        String currentLine;

        while ((currentLine = in.readLine()) != null) {
            response.append(currentLine);
        }

        in.close();
        if (response.isEmpty()) {
            throw new IOException("The Mojang API is down");
        } else {
            return GsonProvider.readObject(response.toString());
        }
    }
}
