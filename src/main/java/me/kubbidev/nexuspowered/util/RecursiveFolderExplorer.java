package me.kubbidev.nexuspowered.util;

import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class RecursiveFolderExplorer {

    private final Consumer<File>      action;
    private final Consumer<Throwable> exceptionHandler;

    /**
     * Constructor to default log errors.
     *
     * @param action         Action to perform for every file in the directory.
     * @param errorLogPrefix Prefix for the error log.
     */
    public RecursiveFolderExplorer(@NotNull Consumer<File> action, @NotNull String errorLogPrefix) {
        this(action, t -> Log.warn(errorLogPrefix + ": " + t.getMessage()));
    }

    /**
     * Constructor with custom error handling.
     *
     * @param action           Action to perform for every file.
     * @param exceptionHandler Handler for exceptions.
     */
    public RecursiveFolderExplorer(@NotNull Consumer<File> action, @NotNull Consumer<Throwable> exceptionHandler) {
        this.action = action;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Recursively explores directories and processes files.
     *
     * @param file The root directory or file to explore.
     */
    @SuppressWarnings("DataFlowIssue")
    public void explore(@NotNull File file) {
        if (file.isDirectory()) {
            Arrays.asList(file.listFiles()).forEach(this::explore);
        } else {
            try {
                this.action.accept(file);
            } catch (Throwable t) {
                this.exceptionHandler.accept(t);
            }
        }
    }

}