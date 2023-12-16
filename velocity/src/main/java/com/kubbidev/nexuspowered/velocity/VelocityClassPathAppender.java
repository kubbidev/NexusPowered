package com.kubbidev.nexuspowered.velocity;

import com.kubbidev.java.classpath.ClassPathAppender;

import java.nio.file.Path;

public class VelocityClassPathAppender implements ClassPathAppender {
    private final VelocityNexusPlugin<?> plugin;

    public VelocityClassPathAppender(VelocityNexusPlugin<?> plugin) {
        this.plugin = plugin;
    }

    @Override
    public void addJarToClasspath(Path file) {
        this.plugin.getProxy().getPluginManager().addToClasspath(this.plugin, file);
    }
}