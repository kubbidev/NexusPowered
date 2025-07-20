package me.kubbidev.nexuspowered.util.logger;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.LegacyAbstractLogger;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

public final class BukkitLoggerFactory {

    private BukkitLoggerFactory() {
    }

    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    public static Logger getLogger(String name) {
        return new BukkitLoggerAdapter(name);
    }

    private static class BukkitLoggerAdapter extends LegacyAbstractLogger {

        static final           char SP              = ' ';
        protected static final int  LOG_LEVEL_TRACE = LocationAwareLogger.TRACE_INT;
        protected static final int  LOG_LEVEL_DEBUG = LocationAwareLogger.DEBUG_INT;
        protected static final int  LOG_LEVEL_INFO  = LocationAwareLogger.INFO_INT;
        protected static final int  LOG_LEVEL_WARN  = LocationAwareLogger.WARN_INT;
        protected static final int  LOG_LEVEL_ERROR = LocationAwareLogger.ERROR_INT;

        public BukkitLoggerAdapter(String name) {
            this.name = name.substring(name.lastIndexOf(".") + 1);
        }

        /**
         * Are {@code trace} messages currently enabled?
         */
        @Override
        public boolean isTraceEnabled() {
            return true;
        }

        /**
         * Are {@code debug} messages currently enabled?
         */
        @Override
        public boolean isDebugEnabled() {
            return true;
        }

        /**
         * Are {@code info} messages currently enabled?
         */
        @Override
        public boolean isInfoEnabled() {
            return true;
        }

        /**
         * Are {@code warn} messages currently enabled?
         */
        @Override
        public boolean isWarnEnabled() {
            return true;
        }

        /**
         * Are {@code error} messages currently enabled?
         */
        @Override
        public boolean isErrorEnabled() {
            return true;
        }

        /**
         * BukkitLogger's implementation of
         * {@link org.slf4j.helpers.AbstractLogger#handleNormalizedLoggingCall(Level, Marker, String, Object[],
         * Throwable)}
         *
         * @param level     The SLF4J level for this event
         * @param marker    The marker to be used for this event, may be null
         * @param s         The message pattern which will be parsed and formatted
         * @param arguments The array of arguments to be formatted, may be null
         * @param throwable The exception whose stack trace should be logged, may be null
         */
        @Override
        protected void handleNormalizedLoggingCall(Level level, Marker marker, String s, Object[] arguments,
                                                   Throwable throwable) {
            List<Marker> markers = null;

            if (marker != null) {
                markers = new ArrayList<>();
                markers.add(marker);
            }

            this.innerHandleNormalizedLoggingCall(level, markers, s, arguments, throwable);
        }

        private void innerHandleNormalizedLoggingCall(Level level, List<Marker> markers, String s, Object[] arguments,
                                                      Throwable t) {
            StringBuilder buf = new StringBuilder(32);
            buf.append(this.name).append(" - ");

            if (markers != null) {
                buf.append(SP);
                for (Marker marker : markers) {
                    buf.append(marker.getName()).append(SP);
                }
            }

            String formattedMessage = BukkitLoggerAdapter.basicArrayFormat(s, arguments);

            // Append the message
            buf.append(formattedMessage);

            Bukkit.getLogger().log(this.getCurrentLogLevel(level.toInt()), buf.toString(), t);
        }

        public static String basicArrayFormat(String messagePattern, Object[] arguments) {
            FormattingTuple formattingTuple = MessageFormatter.arrayFormat(messagePattern, arguments, null);
            return formattingTuple.getMessage();
        }

        @Override
        protected String getFullyQualifiedCallerName() {
            return null;
        }

        protected java.util.logging.Level getCurrentLogLevel(int levelInt) {
            return switch (levelInt) {
                case LOG_LEVEL_TRACE -> java.util.logging.Level.FINE;
                case LOG_LEVEL_DEBUG -> java.util.logging.Level.CONFIG;
                case LOG_LEVEL_INFO -> java.util.logging.Level.INFO;
                case LOG_LEVEL_WARN -> java.util.logging.Level.WARNING;
                case LOG_LEVEL_ERROR -> java.util.logging.Level.SEVERE;
                default -> throw new IllegalStateException("Unrecognized level [" + levelInt + "]");
            };
        }
    }
}
