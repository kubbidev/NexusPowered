package com.kubbidev.nexuspowered.common.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ComponentUtils {

    public static final MiniMessage SERIALIZER = MiniMessage.miniMessage();
    public static final MiniMessage DESERIALIZER = MiniMessage.builder()
            .strict(true)
            .build();

    private ComponentUtils() {
        // Private constructor to prevent instantiation of this utility class.
    }

    /**
     * Parses a MiniMessage-formatted string into an Adventure Component.
     *
     * @param message The MiniMessage-formatted string to parse.
     * @return The parsed Adventure Component.
     */
    public static @NotNull Component fromMiniMessage(String message) {
        return SERIALIZER.deserialize(message);
    }

    /**
     * Parses an Adventure Component into a MiniMessage-formatted.
     *
     * @param message The Adventure Component to parse.
     * @return The parsed MiniMessage-formatted string.
     */
    public static @NotNull String toMiniMessage(Component message) {
        return DESERIALIZER.serialize(message);
    }

    /**
     * Removes the italic decoration from an Adventure Component if present.
     *
     * @param component The Adventure Component to modify.
     * @return The modified Adventure Component without the italic decoration.
     */
    public static @NotNull Component removeItalic(@Nullable Component component) {
        return component != null ? component.decoration(TextDecoration.ITALIC, false) : Component.empty();
    }
}