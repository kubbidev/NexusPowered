package com.kubbidev.nexuspowered.common.command.util;

import com.kubbidev.nexuspowered.common.locale.LocaleMessage;
import com.kubbidev.nexuspowered.common.command.abstraction.Command;
import com.kubbidev.nexuspowered.common.command.abstraction.CommandException;
import com.kubbidev.nexuspowered.common.sender.Sender;

public abstract class ArgumentException extends CommandException {

    public static class DetailedUsage extends ArgumentException {
        @Override
        protected void handle(Sender sender) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void handle(Sender sender, String label, Command<?, ?> command) {
            command.sendDetailedUsage(sender, label);
        }
    }

    public static class PastDate extends ArgumentException {
        @Override
        protected void handle(Sender sender) {
            LocaleMessage.PAST_DATE_ERROR.send(sender);
        }
    }

    public static class InvalidDate extends ArgumentException {
        private final String invalidDate;

        public InvalidDate(String invalidDate) {
            this.invalidDate = invalidDate;
        }

        @Override
        protected void handle(Sender sender) {
            LocaleMessage.ILLEGAL_DATE_ERROR.send(sender, this.invalidDate);
        }
    }

    public static class InvalidPriority extends ArgumentException {
        private final String invalidPriority;

        public InvalidPriority(String invalidPriority) {
            this.invalidPriority = invalidPriority;
        }

        @Override
        public void handle(Sender sender) {
            LocaleMessage.META_INVALID_PRIORITY.send(sender, this.invalidPriority);
        }
    }
}