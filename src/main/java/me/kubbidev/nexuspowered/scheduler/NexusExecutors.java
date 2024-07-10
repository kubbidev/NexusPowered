package me.kubbidev.nexuspowered.scheduler;

import me.kubbidev.nexuspowered.internal.LoaderUtils;
import me.kubbidev.nexuspowered.internal.exception.NexusExceptions;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Provides common {@link Executor} instances.
 */
public final class NexusExecutors {
    private static final Executor SYNC_BUKKIT = new BukkitSyncExecutor();
    private static final Executor ASYNC_BUKKIT = new BukkitAsyncExecutor();
    private static final NexusAsyncExecutor ASYNC_NEXUS = new NexusAsyncExecutor();

    public static Executor sync() {
        return SYNC_BUKKIT;
    }

    public static ScheduledExecutorService asyncNexus() {
        return ASYNC_NEXUS;
    }

    public static Executor asyncBukkit() {
        return ASYNC_BUKKIT;
    }

    public static void shutdown() {
        ASYNC_NEXUS.cancelRepeatingTasks();
    }

    private static final class BukkitSyncExecutor implements Executor {
        @Override
        public void execute(@NotNull Runnable runnable) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(LoaderUtils.getPlugin(), NexusExceptions.wrapSchedulerTask(runnable));
        }
    }

    private static final class BukkitAsyncExecutor implements Executor {
        @Override
        public void execute(@NotNull Runnable runnable) {
            Bukkit.getScheduler().runTaskAsynchronously(LoaderUtils.getPlugin(), NexusExceptions.wrapSchedulerTask(runnable));
        }
    }

    @Deprecated
    public static Runnable wrapRunnable(Runnable runnable) {
        return NexusExceptions.wrapSchedulerTask(runnable);
    }

    private NexusExecutors() {
    }

}