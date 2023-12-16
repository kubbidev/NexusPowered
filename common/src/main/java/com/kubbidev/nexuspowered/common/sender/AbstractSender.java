package com.kubbidev.nexuspowered.common.sender;

import com.kubbidev.java.util.TriState;
import com.kubbidev.nexuspowered.common.NexusPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Simple implementation of {@link Sender} using a {@link SenderFactory}
 *
 * @param <T> the command sender type
 */
public final class AbstractSender<T> implements Sender {
    private final NexusPlugin<?> plugin;
    private final SenderFactory<?, T> factory;
    private final T sender;

    private final UUID uniqueId;
    private final String name;

    private final boolean isConsole;

    AbstractSender(NexusPlugin<?> plugin, SenderFactory<?, T> factory, T sender) {
        this.plugin = plugin;
        this.factory = factory;
        this.sender = sender;
        this.uniqueId = factory.getUniqueId(this.sender);
        this.name = factory.getName(this.sender);
        this.isConsole = this.factory.isConsole(this.sender);
    }

    @Override
    public NexusPlugin<?> getPlugin() {
        return this.plugin;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void sendMessage(Component message) {
        if (isConsole()) {
            for (Component line : splitNewlines(message)) {
                this.factory.sendMessage(this.sender, line);
            }
        } else {
            this.factory.sendMessage(this.sender, message);
        }
    }

    @Override
    public void sendMessage(String message) {
        this.factory.sendMessage(this.sender, message);
    }

    @Override
    public TriState getPermissionValue(String permission) {
        return (isConsole() && this.factory.consoleHasAllPermissions()) ? TriState.TRUE : this.factory.getPermissionValue(this.sender, permission);
    }

    @Override
    public boolean hasPermission(String permission) {
        return (isConsole() && this.factory.consoleHasAllPermissions()) || this.factory.hasPermission(this.sender, permission);
    }

    @Override
    public void performCommand(String commandLine) {
        this.factory.performCommand(this.sender, commandLine);
    }

    @Override
    public boolean isConsole() {
        return this.isConsole;
    }

    @Override
    public Locale getLocale() {
        return this.factory.getLocale(this.sender);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> S getHandle() {
        return (S) this.sender;
    }

    @Override
    public boolean isValid() {
        return isConsole() || this.plugin.isPlayerOnline(this.uniqueId);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof AbstractSender<?> that)) return false;
        return this.getUniqueId().equals(that.getUniqueId());
    }

    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }

    // A small utility method which splits components built using
    // > join(newLine(), components...)
    // back into separate components.
    static Iterable<Component> splitNewlines(Component message) {

        if (message instanceof TextComponent component

                && message.style().isEmpty() && !message.children().isEmpty() && component.content().isEmpty()) {

            LinkedList<List<Component>> split = new LinkedList<>();
            split.add(new ArrayList<>());

            for (Component child : message.children()) {
                if (Component.newline().equals(child)) {
                    split.add(new ArrayList<>());
                } else {

                    Iterator<Component> splitChildren = splitNewlines(child).iterator();
                    if (splitChildren.hasNext()) {
                        split.getLast().add(splitChildren.next());
                    }
                    while (splitChildren.hasNext()) {
                        split.add(new ArrayList<>());
                        split.getLast().add(splitChildren.next());
                    }
                }
            }

            return split.stream().map(input -> switch (input.size()) {
                case 0 -> Component.empty();
                case 1 -> input.get(0);
                default -> Component.join(JoinConfiguration.separator(Component.empty()), input);
            }).collect(Collectors.toList());
        }
        return Collections.singleton(message);
    }
}