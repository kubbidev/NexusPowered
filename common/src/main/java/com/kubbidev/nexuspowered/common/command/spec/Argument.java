package com.kubbidev.nexuspowered.common.command.spec;

import com.kubbidev.nexuspowered.common.locale.LocaleMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;

public class Argument {
    private final String name;
    private final boolean required;
    private final TranslatableComponent description;

    Argument(String name, boolean required, TranslatableComponent description) {
        this.name = name;
        this.required = required;
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public boolean isRequired() {
        return this.required;
    }

    public TranslatableComponent getDescription() {
        return this.description;
    }

    public Component asPrettyString() {
        return (this.required ? LocaleMessage.REQUIRED_ARGUMENT : LocaleMessage.OPTIONAL_ARGUMENT).build(this.name);
    }
}