package me.kubbidev.nexuspowered.signprompt;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents an object which can accept input from players using signs.
 */
public interface SignPromptFactory {

    /**
     * Opens a sign prompt.
     *
     * @param player          the player to open the prompt for
     * @param lines           the lines to fill the sign with initially
     * @param responseHandler the response handler.
     */
    void openPrompt(@NotNull Player player, @NotNull List<Component> lines, @NotNull ResponseHandler responseHandler);

    /**
     * Functional interface for handling responses to an active sign prompt.
     */
    @FunctionalInterface
    interface ResponseHandler {

        /**
         * Handles the response
         *
         * @param lines the response content
         * @return the response
         */
        @NotNull
        Response handleResponse(@NotNull List<String> lines);

    }

    /**
     * Encapsulates a response to the players input.
     */
    enum Response {

        /**
         * Marks that the response was accepted
         */
        ACCEPTED,

        /**
         * Marks that the response was not accepted, the player will be prompted
         * for another input.
         */
        TRY_AGAIN

    }
}