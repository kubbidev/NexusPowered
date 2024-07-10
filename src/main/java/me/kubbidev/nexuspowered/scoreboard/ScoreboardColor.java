package me.kubbidev.nexuspowered.scoreboard;

import com.google.common.base.Preconditions;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * All supported color values for chat.
 */
public enum ScoreboardColor {

    /**
     * The standard {@code black} colour.
     */
    BLACK('0', 0x00) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.BLACK;
        }
    },

    /**
     * The standard {@code dark_blue} colour.
     */
    DARK_BLUE('1', 0x1) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.DARK_BLUE;
        }
    },

    /**
     * The standard {@code dark_green} colour.
     */
    DARK_GREEN('2', 0x2) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.DARK_GREEN;
        }
    },

    /**
     * The standard {@code dark_aqua} colour.
     */
    DARK_AQUA('3', 0x3) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.DARK_AQUA;
        }
    },

    /**
     * The standard {@code dark_red} colour.
     */
    DARK_RED('4', 0x4) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.DARK_RED;
        }
    },

    /**
     * The standard {@code dark_purple} colour.
     */
    DARK_PURPLE('5', 0x5) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.DARK_PURPLE;
        }
    },

    /**
     * The standard {@code gold} colour.
     */
    GOLD('6', 0x6) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.GOLD;
        }
    },

    /**
     * The standard {@code gray} colour.
     */
    GRAY('7', 0x7) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.GRAY;
        }
    },

    /**
     * The standard {@code dark_gray} colour.
     */
    DARK_GRAY('8', 0x8) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.DARK_GRAY;
        }
    },

    /**
     * The standard {@code blue} colour.
     */
    BLUE('9', 0x9) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.BLUE;
        }
    },

    /**
     * The standard {@code green} colour.
     */
    GREEN('a', 0xA) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.GREEN;
        }
    },

    /**
     * The standard {@code aqua} colour.
     */
    AQUA('b', 0xB) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.AQUA;
        }
    },

    /**
     * The standard {@code red} colour.
     */
    RED('c', 0xC) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.RED;
        }
    },

    /**
     * The standard {@code light_purple} colour.
     */
    LIGHT_PURPLE('d', 0xD) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.LIGHT_PURPLE;
        }
    },

    /**
     * The standard {@code yellow} colour.
     */
    YELLOW('e', 0xE) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.YELLOW;
        }
    },

    /**
     * The standard {@code white} colour.
     */
    WHITE('f', 0xF) {
        @Override
        public @NotNull NamedTextColor asTextColor() {
            return NamedTextColor.WHITE;
        }
    },

    /**
     * A decoration which makes text obfuscated/unreadable.
     */
    OBFUSCATED('k', 0x10, true),

    /**
     * A decoration which makes text appear bold.
     */
    BOLD('l', 0x11, true),

    /**
     * A decoration which makes text have a strike through it.
     */
    STRIKETHROUGH('m', 0x12, true),

    /**
     * A decoration which makes text have an underline.
     */
    UNDERLINE('n', 0x13, true),

    /**
     * A decoration which makes text appear in italics.
     */
    ITALIC('o', 0x14, true),

    /**
     * The standard {@code reset} formatting.
     */
    RESET('r', 0x15);

    /**
     * Map containing all color associated to their index.
     */
    private static final Map<Integer, ScoreboardColor> VALUES = new HashMap<>();

    static {
        for (ScoreboardColor color : values()) {
            VALUES.put(color.index, color);
        }
    }

    /**
     * The special character which prefixes all chat colour codes.
     * <p>
     * Use this if you need to dynamically convert colour codes from your custom format.
     */
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static final char COLOR_CHAR = '\u00A7';

    private final char code;
    private final int index;
    private final boolean isFormat;

    ScoreboardColor(char code, int index) {
        this(code, index, false);
    }

    ScoreboardColor(char code, int index, boolean isFormat) {
        this.code = code;
        this.index = index;
        this.isFormat = isFormat;
    }

    @Nullable
    public NamedTextColor asTextColor() {
        Preconditions.checkArgument(this.isColor(), this.name() + " is not a color");
        return null;
    }

    /**
     * Gets the char value associated with this color.
     *
     * @return A char value of this color code
     */
    public char getChar() {
        return this.code;
    }

    /**
     * Checks if this code is a format code as opposed to a color code.
     *
     * @return whether this ChatColor is a format code
     */
    public boolean isFormat() {
        return this.isFormat;
    }

    /**
     * Checks if this code is a color code as opposed to a format code.
     *
     * @return whether this ChatColor is a color code
     */
    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }

    @Override
    public String toString() {
        return new String(new char[]{COLOR_CHAR, this.code});
    }

    /**
     * Gets the color represented by the specified index.
     *
     * @param index to check
     * @return Associative {@link ScoreboardColor} with the given index,
     * or null if it doesn't exist
     */
    @Nullable
    public static ScoreboardColor getByIndex(int index) {
        return VALUES.get(index);
    }

    /**
     * Translates a string using an alternate color code character into a string that uses the
     * internal {@link ScoreboardColor#COLOR_CHAR} color code character.
     * <p>
     * The alternate color code character will only be replaced if it is immediately
     * followed by 0-9, A-F, a-f, K-O, k-o, R or r.
     *
     * @param altColorChar    The alternate color code character to replace. Ex: {@code &}
     * @param textToTranslate Text containing the alternate color code character.
     * @return Text containing the {@link ScoreboardColor#COLOR_CHAR} color code character.
     */
    @NotNull
    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        Preconditions.checkArgument(textToTranslate != null, "Cannot translate null text");

        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = ScoreboardColor.COLOR_CHAR;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }
}