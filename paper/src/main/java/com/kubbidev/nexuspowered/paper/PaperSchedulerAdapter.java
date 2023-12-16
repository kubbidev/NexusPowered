package com.kubbidev.nexuspowered.paper;

import com.kubbidev.nexuspowered.common.engine.scheduler.AbstractJavaScheduler;
import com.kubbidev.nexuspowered.common.engine.scheduler.SchedulerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;

public class PaperSchedulerAdapter extends AbstractJavaScheduler implements SchedulerAdapter {

    private final Executor sync;

    public PaperSchedulerAdapter(PaperNexusEngine plugin) {
        super(plugin);
        this.sync = r -> plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, r);
    }

    @Override
    public @NotNull Executor sync() {
        return this.sync;
    }
}
