package com.kubbidev.nexuspowered.common.engine.dependencies;

/**
 * Exception thrown if a dependency cannot be downloaded.
 */
public class DependencyDownloadException extends Exception {

    public DependencyDownloadException(String message) {
        super(message);
    }

    public DependencyDownloadException(Throwable cause) {
        super(cause);
    }
}