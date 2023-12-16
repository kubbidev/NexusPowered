package com.kubbidev.nexuspowered.common.locale;

import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.sender.Sender;
import com.kubbidev.nexuspowered.common.util.ComponentUtils;
import com.kubbidev.nexuspowered.common.util.duration.DurationFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static net.kyori.adventure.text.Component.*;
import static net.kyori.adventure.text.event.HoverEvent.showText;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

/**
 * A collection of formatted messages used by the plugin.
 */
public interface LocaleMessage {

    DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd '@' HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    TextComponent OPEN_BRACKET = text('(');
    TextComponent CLOSE_BRACKET = text(')');
    TextComponent FULL_STOP = text('.');
    TextComponent COMMA = text(':');
    TextComponent EXCLAMATION = text('!');

    Component PREFIX_COMPONENT = text()
            .color(GRAY)
            .append(text('['))
            .append(text()
                    .decorate(BOLD)
                    .color(GOLD)
                    .content(NexusPlugin.PREFIX)
            )
            .append(text(']'))
            .build();

    static TextComponent prefixed(String message) {
        return prefixed(ComponentUtils.fromMiniMessage(message));
    }

    static TextComponent prefixed(ComponentLike component) {
        return text()
                .append(PREFIX_COMPONENT)
                .append(space())
                .append(component)
                .build();
    }

    Args1<NexusPlugin<?>> STARTUP_BANNER = plugin -> {
        Component infoLine1 = text()
                .append(text(NexusPlugin.getEngineName()))
                .append(space())
                .append(text("v" + plugin.getPluginVersion()))
                .build();

        Component infoLine2 = text()
                .append(text("Running on "))
                .append(text(plugin.getType().getFriendlyName()))
                .append(text(" - "))
                .append(text(plugin.getServerBrand()))
                .build();


//" _______                              "
//" \      \   ____ ___  _____ __  ______"
//" /   |   \_/ __ \\  \/  /  |  \/  ___/"
//"/    |    \  ___/ >    <|  |  /\___ \ "
//"\____|__  /\___  >__/\_ \____//____  >"
//"        \/     \/      \/          \/ "

        return joinNewline(
                text(" _______                              "),
                text(" \\      \\   ____ ___  _____ __  ______"),
                text(" /   |   \\_/ __ \\\\  \\/  /  |  \\/  ___/"),
                text("/    |    \\  ___/ >    <|  |  /\\___ \\ "),
                text()
                        .append(text("\\____|__  /\\___  >__/\\_ \\____//____  >"))
                        .append(space())
                        .append(infoLine1),
                text()
                        .append(text("        \\/     \\/      \\/          \\/ "))
                        .append(space())
                        .append(infoLine2),
                empty()
        );
    };

    Args1<NexusPlugin<?>> INFO = plugin -> joinNewline(
            prefixed(translatable()
                    .key("nexuspowered.command.info.running-plugin")
                    .color(GOLD)
                    .args(
                            text(plugin.getPluginName(), YELLOW),
                            text(plugin.getPluginVersion(), YELLOW),
                            text(plugin.getAuthor(), YELLOW))
                    .append(FULL_STOP)),
            prefixed(text()
                    .color(GRAY)
                    .append(text("-  ", WHITE))
                    .append(translatable("nexuspowered.command.info.platform-key"))
                    .append(text(": "))
                    .append(text(plugin.getType().getFriendlyName(), WHITE))),
            prefixed(text()
                    .color(GRAY)
                    .append(text("-  ", WHITE))
                    .append(translatable("nexuspowered.command.info.server-brand-key"))
                    .append(text(": "))
                    .append(text(plugin.getServerBrand(), WHITE))),
            prefixed(text()
                    .color(GRAY)
                    .append(text("-  ", WHITE))
                    .append(translatable("nexuspowered.command.info.server-version-key"))
                    .append(text(':'))),
            prefixed(text()
                    .color(WHITE)
                    .append(text("     "))
                    .append(text(plugin.getServerVersion()))),
            prefixed(text()
                    .color(YELLOW)
                    .append(text("-  ", WHITE))
                    .append(translatable("nexuspowered.command.info.instance-key"))
                    .append(COMMA)),
            prefixed(text()
                    .color(GRAY)
                    .append(text("     "))
                    .append(translatable("nexuspowered.command.info.online-players-key"))
                    .append(text(": "))
                    .append(text(plugin.getPlayerCount(), WHITE))),
            prefixed(text()
                    .color(GRAY)
                    .append(text("     "))
                    .append(translatable("nexuspowered.command.info.uptime-key"))
                    .append(text(": "))
                    .append(text().color(WHITE).append(DurationFormatter.LONG.format(Duration.between(plugin.getStartTime(), Instant.now()))))),
            prefixed(text()),
            prefixed(text()
                    .color(DARK_GRAY)
                    .append(text("Powered by "))
                    .append(text(NexusPlugin.getEngineName()))
                    .append(FULL_STOP))
    );

    Args3<String, String, Component> HELP = (mainLabel, subLabel, description) -> prefixed(text()
            .append(text("» /" + mainLabel + " " + subLabel + (subLabel.isEmpty() ? "" : " "), GOLD))
            .append(text("- ", GRAY))
            .append(description)
            .append(FULL_STOP)
            .color(WHITE)
    );

    Args1<List<NexusPlugin<?>>> PLUGIN = (plugins) -> {

        TextComponent.Builder listBuilder = text();
        for (int i = 0; i < plugins.size(); ++i) {

            NexusPlugin<?> plugin = plugins.get(i);
            listBuilder.append(() -> {

                String name = plugin.getPluginName();
                String version = plugin.getPluginVersion();

                TextComponent.Builder hoverText = text()
                        .append(text()
                                .color(GRAY)
                                .append(text(name))
                                .append(space())
                                .append(text(version))
                        );

                plugin.getUrl().ifPresent(website -> {
                    hoverText.append(newline());
                    hoverText.append(translatable("nexuspowered.command.plugins.url-key", GRAY));
                    hoverText.append(COMMA);
                    hoverText.append(space());
                    hoverText.append(text(website, AQUA));
                });

                hoverText.append(newline());
                hoverText.append(translatable("nexuspowered.command.plugins.authors-key", GRAY));
                hoverText.append(COMMA);
                hoverText.append(space());
                hoverText.append(text(plugin.getAuthor()));

                plugin.getPluginDescription().ifPresent(description -> {
                    hoverText.append(newline());
                    hoverText.append(newline());
                    hoverText.append(text(description));
                });

                return text(plugin.getId(), GRAY)
                        .hoverEvent(showText(hoverText));
            });
            if (i + 1 < plugins.size()) {
                listBuilder.append(text(", ", NamedTextColor.GRAY));
            }
        }
        return prefixed(text()
                .append(text()
                        .append(translatable("nexuspowered.command.plugins.plugins-key"))
                        .append(COMMA)
                        .color(GOLD)
                )
                .append(space())
                .append(listBuilder)
        );
    };

    Args0 NO_PLUGIN_INSTALL = () -> translatable()
            .key("nexuspowered.command.plugins.no-plugins-install")
            .color(RED)
            .append(FULL_STOP)
            .build();

    Args0 RELOAD = () -> prefixed(translatable()
            .key("nexuspowered.command.reload.success")
            .append(FULL_STOP)
            .append(space())
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(translatable("nexuspowered.command.reload.restart-note"))
                    .append(CLOSE_BRACKET)
            )
    );

    Args1<String> REQUIRED_ARGUMENT = name -> text()
            .color(DARK_GRAY)
            .append(text('<'))
            .append(text(name, GRAY))
            .append(text('>'))
            .build();

    Args1<String> OPTIONAL_ARGUMENT = name -> text()
            .color(DARK_GRAY)
            .append(text('['))
            .append(text(name, GRAY))
            .append(text(']'))
            .build();

    Args1<String> USER_NOT_FOUND = id -> prefixed(translatable()
            // "&cA user for &4{}&c could not be found."
            .key("nexuspowered.command.misc.loading.error.user-not-found")
            .color(RED)
            .args(text(id, DARK_RED))
            .append(FULL_STOP)
    );

    Args2<String, Component> COMMAND_USAGE_DETAILED_HEADER = (name, usage) -> joinNewline(
            // "&3&lCommand Usage &3- &b{}"
            // "&b> &7{}"
            prefixed(text()
                    .append(translatable("nexuspowered.commandsystem.usage.usage-header", DARK_AQUA, BOLD))
                    .append(text(" - ", DARK_AQUA))
                    .append(text(name, AQUA))),
            prefixed(text()
                    .append(text('>', AQUA))
                    .append(space())
                    .append(text().color(GRAY).append(usage)))
    );

    Args0 COMMAND_USAGE_DETAILED_ARGS_HEADER = () -> prefixed(translatable()
            // "&3Arguments:"
            .key("nexuspowered.commandsystem.usage.arguments-header")
            .color(DARK_AQUA)
            .append(text(':'))
    );

    Args2<Component, Component> COMMAND_USAGE_DETAILED_ARG = (arg, usage) -> prefixed(text()
            // "&b- {}&3 -> &7{}"
            .append(text('-', AQUA))
            .append(space())
            .append(arg)
            .append(text(" -> ", DARK_AQUA))
            .append(text().color(GRAY).append(usage))
    );

    Args0 COMMAND_NOT_RECOGNISED = () -> prefixed(translatable()
            // "&cCommand not recognised."
            .key("nexuspowered.commandsystem.command-not-recognised")
            .color(RED)
            .append(FULL_STOP)
    );

    Args0 COMMAND_PLAYER_ONLY = () -> prefixed(translatable()
            // "&cThis command is for players only."
            .key("nexuspowered.commandsystem.player-only")
            .color(RED)
            .append(FULL_STOP)
    );

    Args0 COMMAND_NO_PERMISSION = () -> prefixed(translatable()
            // "&cYou do not have permission to use this command!"
            .key("nexuspowered.commandsystem.no-permission")
            .color(RED)
            .append(EXCLAMATION)
    );

    Args2<String, String> MAIN_COMMAND_USAGE_HEADER = (name, usage) -> prefixed(text()
            // "&b{} Sub Commands: &7({} ...)"
            .color(AQUA)
            .append(text(name))
            .append(space())
            .append(translatable("nexuspowered.commandsystem.usage.sub-commands-header"))
            .append(text(": "))
            .append(text()
                    .color(GRAY)
                    .append(OPEN_BRACKET)
                    .append(text(usage))
                    .append(text(" ..."))
                    .append(CLOSE_BRACKET)
            ));

    Args1<String> ILLEGAL_DATE_ERROR = invalid -> prefixed(translatable()
            // "&cCould not parse date &4{}&c."
            .key("nexuspowered.command.misc.date-parse-error")
            .color(RED)
            .args(text(invalid, DARK_RED))
            .append(FULL_STOP)
    );

    Args0 PAST_DATE_ERROR = () -> prefixed(translatable()
            // "&cYou cannot set a date in the past!"
            .key("nexuspowered.command.misc.date-in-past-error")
            .color(RED)
            .append(EXCLAMATION)
    );

    Args1<String> META_INVALID_PRIORITY = invalid -> prefixed(translatable()
            // "&cInvalid priority &4{}&c. Expected a number."
            .key("nexuspowered.command.misc.invalid-priority")
            .color(RED)
            .args(text(invalid, DARK_RED))
            .append(FULL_STOP)
            .append(space())
            .append(translatable("nexuspowered.command.misc.expected-number"))
            .append(FULL_STOP)
    );

    Args0 ALREADY_EXECUTING_COMMAND = () -> prefixed(translatable()
            // "&7Another command is being executed, waiting for it to finish..."
            .key("nexuspowered.commandsystem.already-executing-command")
            .color(GRAY)
    );

    static Component joinNewline(ComponentLike... components) {
        return join(JoinConfiguration.newlines(), components);
    }

    interface Args0 {

        Component build();

        default void send(Sender sender) {
            sender.sendMessage(build());
        }
    }

    interface Args1<A0> {

        Component build(A0 arg0);

        default void send(Sender sender, A0 arg0) {
            sender.sendMessage(build(arg0));
        }
    }

    interface Args2<A0, A1> {

        Component build(A0 arg0, A1 arg1);

        default void send(Sender sender, A0 arg0, A1 arg1) {
            sender.sendMessage(build(arg0, arg1));
        }
    }

    interface Args3<A0, A1, A2> {

        Component build(A0 arg0, A1 arg1, A2 arg2);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2) {
            sender.sendMessage(build(arg0, arg1, arg2));
        }
    }

    interface Args4<A0, A1, A2, A3> {

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3));
        }
    }

    interface Args5<A0, A1, A2, A3, A4> {

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4));
        }
    }

    interface Args6<A0, A1, A2, A3, A4, A5> {

        Component build(A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5);

        default void send(Sender sender, A0 arg0, A1 arg1, A2 arg2, A3 arg3, A4 arg4, A5 arg5) {
            sender.sendMessage(build(arg0, arg1, arg2, arg3, arg4, arg5));
        }
    }
}
