package me.kubbidev.nexuspowered.internal.properties;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

final class NexusPropertiesImpl {

    private static final String     FILESYSTEM_DIRECTORY_NAME = "config";
    private static final String     FILESYSTEM_FILE_NAME      = "nexuspowered.properties";
    private static final Properties PROPERTIES                = new Properties();

    static {
        Path path = Optional.ofNullable(System.getProperty(systemPropertyName(FILESYSTEM_DIRECTORY_NAME)))
            .map(Path::of)
            .orElseGet(() -> Path.of(FILESYSTEM_DIRECTORY_NAME, FILESYSTEM_FILE_NAME));
        if (Files.isRegularFile(path)) {
            try (InputStream is = Files.newInputStream(path)) {
                PROPERTIES.load(is);
            } catch (IOException e) {
                print(e); // Well, that's awkward.
            }
        }
    }

    @SuppressWarnings("CallToPrintStackTrace") // We don't have any better options
    private static void print(Throwable e) {
        e.printStackTrace();
    }

    private NexusPropertiesImpl() {
    }

    @VisibleForTesting
    static String systemPropertyName(String name) {
        return String.join(".", "me", "kubbidev", "nexuspowered", name);
    }

    static <T> NexusProperties.Property<T> property(String name, Function<String, T> parser, @Nullable T defaultValue) {
        return new PropertyImpl<>(name, parser, defaultValue);
    }

    private static final class PropertyImpl<T> implements NexusProperties.Property<T> {

        private final String              name;
        private final Function<String, T> parser;
        @Nullable
        private final T                   defaultValue;
        private       boolean             valueCalculated;
        @Nullable
        private       T                   value;

        PropertyImpl(String name, Function<String, T> parser, @Nullable T defaultValue) {
            this.name = name;
            this.parser = parser;
            this.defaultValue = defaultValue;
        }

        @Override
        public @Nullable T value() {
            if (!this.valueCalculated) {
                String property = systemPropertyName(this.name);
                String value = System.getProperty(property, PROPERTIES.getProperty(this.name));
                if (value != null) {
                    this.value = this.parser.apply(value);
                }
                if (this.value == null) {
                    this.value = this.defaultValue;
                }
                this.valueCalculated = true;
            }
            return this.value;
        }

        @Override
        public String toString() {
            return this.name;
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }
    }
}