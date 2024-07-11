package me.kubbidev.nexuspowered.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utilities for working with {@link Component}s and formatted text strings.
 */
public final class Text {

    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static final char SECTION_CHAR = '\u00A7'; // §
    public static final char AMPERSAND_CHAR = '&';

    public static String joinNewline(String... strings) {
        return joinNewline(Arrays.stream(strings));
    }

    public static String joinNewline(Stream<String> strings) {
        return strings.collect(Collectors.joining("\n"));
    }

    public static Component removeItalic(Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static Component fromGson(String input) {
        return GsonComponentSerializer.gson().deserialize(input);
    }

    public static List<Component> fromGson(List<String> input) {
        return input.stream().map(Text::fromGson).collect(Collectors.toList());
    }

    public static String toGson(Component input) {
        return GsonComponentSerializer.gson().serialize(input);
    }

    public static List<String> toGson(List<Component> input) {
        return input.stream().map(Text::toGson).collect(Collectors.toList());
    }

    public static TextComponent fromLegacy(String input, char character) {
        return LegacyComponentSerializer.legacy(character).deserialize(input);
    }

    public static TextComponent fromLegacy(String input) {
        return LegacyComponentSerializer.legacy(SECTION_CHAR).deserialize(input);
    }

    public static String toLegacy(Component component, char character) {
        return LegacyComponentSerializer.legacy(character).serialize(component);
    }

    public static String toLegacy(Component component) {
        return LegacyComponentSerializer.legacy(SECTION_CHAR).serialize(component);
    }

    public static Component fromMiniMessage(String input) {
        return MiniMessage.miniMessage().deserialize(input);
    }

    public static List<Component> fromMiniMessage(List<String> input) {
        return input.stream().map(Text::fromMiniMessage).collect(Collectors.toList());
    }

    public static String toMiniMessage(Component input) {
        return MiniMessage.miniMessage().serialize(input);
    }

    public static List<String> toMiniMessage(List<Component> input) {
        return input.stream().map(Text::toMiniMessage).collect(Collectors.toList());
    }

    public static String colorize(String s) {
        return s == null ? null : translateAlternateColorCodes(AMPERSAND_CHAR, SECTION_CHAR, s);
    }

    public static String decolorize(String s) {
        return s == null ? null : translateAlternateColorCodes(SECTION_CHAR, AMPERSAND_CHAR, s);
    }

    public static String translateAlternateColorCodes(char from, char to, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == from && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = to;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String removeWhitespaces(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private Text() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}