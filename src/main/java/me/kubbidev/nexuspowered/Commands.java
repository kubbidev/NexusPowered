package me.kubbidev.nexuspowered;

import me.kubbidev.nexuspowered.command.argument.ArgumentParserRegistry;
import me.kubbidev.nexuspowered.command.argument.SimpleParserRegistry;
import me.kubbidev.nexuspowered.command.functional.FunctionalCommandBuilder;
import me.kubbidev.nexuspowered.util.Numbers;
import me.kubbidev.nexuspowered.time.DurationParser;
import me.kubbidev.nexuspowered.util.Players;
import me.kubbidev.nexuspowered.util.annotation.NotNullByDefault;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * A functional command handling utility.
 */
@NotNullByDefault
public final class Commands {

    // Global argument parsers
    private static final ArgumentParserRegistry PARSER_REGISTRY;

    @NotNull
    public static ArgumentParserRegistry parserRegistry() {
        return PARSER_REGISTRY;
    }

    static {
        PARSER_REGISTRY = new SimpleParserRegistry();

        // setup default argument parsers
        PARSER_REGISTRY.register(String.class, Optional::of);
        PARSER_REGISTRY.register(Number.class, Numbers::parse);
        PARSER_REGISTRY.register(Integer.class, Numbers::parseIntegerOpt);
        PARSER_REGISTRY.register(Long.class, Numbers::parseLongOpt);
        PARSER_REGISTRY.register(Float.class, Numbers::parseFloatOpt);
        PARSER_REGISTRY.register(Double.class, Numbers::parseDoubleOpt);
        PARSER_REGISTRY.register(Byte.class, Numbers::parseByteOpt);
        PARSER_REGISTRY.register(Boolean.class, s -> {
            if (s.equalsIgnoreCase("true")) return Optional.of(true);
            if (s.equalsIgnoreCase("false")) return Optional.of(false);
            return Optional.empty();
        });
        PARSER_REGISTRY.register(UUID.class, s -> {
            try {
                return Optional.of(UUID.fromString(s));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        });
        PARSER_REGISTRY.register(Player.class, s -> {
            try {
                return Players.get(UUID.fromString(s));
            } catch (IllegalArgumentException e) {
                return Players.get(s);
            }
        });
        PARSER_REGISTRY.register(OfflinePlayer.class, s -> {
            try {
                return Players.getOffline(UUID.fromString(s));
            } catch (IllegalArgumentException e) {
                return Players.getOffline(s);
            }
        });
        PARSER_REGISTRY.register(World.class, Nexus::world);
        PARSER_REGISTRY.register(Duration.class, DurationParser::parseSafely);
    }

    /**
     * Creates and returns a new command builder.
     *
     * @return a command builder
     */
    public static FunctionalCommandBuilder<CommandSender> create() {
        return FunctionalCommandBuilder.newBuilder();
    }

    private Commands() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}