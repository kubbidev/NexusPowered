package com.kubbidev.nexuspowered.velocity;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.kubbidev.java.util.Iterators;
import com.kubbidev.nexuspowered.common.engine.scheduler.SchedulerAdapter;
import com.kubbidev.nexuspowered.common.engine.scheduler.SchedulerTask;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class VelocitySchedulerAdapter implements SchedulerAdapter {
    private final VelocityNexusPlugin<?> plugin;

    private final Executor executor;
    private final Set<ScheduledTask> tasks = Collections.newSetFromMap(new WeakHashMap<>());

    public VelocitySchedulerAdapter(VelocityNexusPlugin<?> plugin) {
        this.plugin = plugin;
        this.executor = r -> plugin.getProxy().getScheduler().buildTask(plugin, r).schedule();
    }

    @Override
    public @NotNull Executor async() {
        return this.executor;
    }

    @Override
    public @NotNull Executor sync() {
        return this.executor;
    }

    @Override
    public SchedulerTask asyncLater(Runnable task, long delay, TimeUnit unit) {
        ScheduledTask t = this.plugin.getProxy().getScheduler().buildTask(this.plugin, task)
                .delay((int) delay, unit)
                .schedule();

        this.tasks.add(t);
        return t::cancel;
    }

    @Override
    public SchedulerTask asyncRepeating(Runnable task, long interval, TimeUnit unit) {
        ScheduledTask t = this.plugin.getProxy().getScheduler().buildTask(this.plugin, task)
                .delay((int) interval, unit)
                .repeat((int) interval, unit)
                .schedule();

        this.tasks.add(t);
        return t::cancel;
    }

    @Override
    public void shutdownScheduler() {
        Iterators.tryIterate(this.tasks, ScheduledTask::cancel);
    }

    @Override
    public void shutdownExecutor() {
        // do nothing
    }
}
