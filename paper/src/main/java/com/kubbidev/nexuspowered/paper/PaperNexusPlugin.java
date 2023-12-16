package com.kubbidev.nexuspowered.paper;

import com.kubbidev.java.classpath.ClassPathAppender;
import com.kubbidev.java.classpath.ReflectionClassPathAppender;
import com.kubbidev.java.config.Configuration;
import com.kubbidev.java.logging.LoggerAdapter;
import com.kubbidev.java.logging.Slf4JLoggerAdapter;
import com.kubbidev.java.util.StringUtil;
import com.kubbidev.nexuspowered.common.NexusEngine;
import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.engine.dependencies.DependencyManager;
import com.kubbidev.nexuspowered.common.engine.scheduler.SchedulerAdapter;
import com.kubbidev.nexuspowered.common.locale.TranslationManager;
import com.kubbidev.nexuspowered.common.sender.Sender;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

/**
 * The NexusPlugin class represents a plugin within the NexusPowered system.
 * Plugins extend this class to integrate with the NexusPowered functionality.
 */
public abstract class PaperNexusPlugin<P extends PaperNexusPlugin<P>> extends JavaPlugin implements NexusPlugin<P> {

    private boolean isEngine;

    // The plugin logger
    private LoggerAdapter logger;

    // Init during enable
    private Configuration configuration;

    private PaperSenderFactory<P> senderFactory;
    private PaperCommandManager<P> commandManager;

    private ClassPathAppender classPathAppender;
    private TranslationManager translationManager;

    // Load/enable latches
    private final CountDownLatch loadLatch = new CountDownLatch(1);
    private final CountDownLatch enableLatch = new CountDownLatch(1);

    // The time when the plugin was enabled
    private Instant startTime;

    // Listeners to register
    private final Set<Listener> listeners = new HashSet<>();

    @Override
    public boolean isEngine() {
        return this.isEngine;
    }

    @Override
    public PaperNexusEngine provideEngine() {
        return PaperNexusEngine.getInstance();
    }

    // Lifecycle

    @SuppressWarnings("unchecked")
    @Override
    public void onLoad() {
        super.onLoad();
        try {
            this.startTime = Instant.now();
            this.logger = new Slf4JLoggerAdapter(getSLF4JLogger());

            // Load sender factory class path appender
            this.senderFactory = new PaperSenderFactory<>((P) this);
            this.classPathAppender = new ReflectionClassPathAppender(getClassLoader());

            // Load translations
            this.translationManager = new TranslationManager(this);
            this.translationManager.reload();

            this.isEngine = this instanceof NexusEngine;
            if (this.isEngine) {
                if (!provideEngine().enableEngine()) {
                    getLoggerAdapter().severe("NexusEngine failed to load, NexusPowered will be disable!");
                    getServer().getPluginManager().disablePlugin(this);
                    return;
                }
            } else {
                provideEngine().hookChild(this);
                getLoggerAdapter().info("Powered by: {}", provideEngine().getPluginName());
            }
            saveDefaultConfig();
            load();
        } finally {
            this.loadLatch.countDown();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        try {
            loadManagers();
            getLoggerAdapter().info("Done ({}ms!)", (System.currentTimeMillis() - startTime.toEpochMilli()));
        } finally {
            this.enableLatch.countDown();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        // Current time for later stats
        long start = System.currentTimeMillis();

        getLoggerAdapter().info("Starting shutdown process...");
        unloadManagers();

        if (this.classPathAppender != null)
            this.classPathAppender.close();

        // Inform of how long it took
        this.logger.info("Everything has been shut down in {}ms!", (System.currentTimeMillis() - start));
        this.logger.info("Goodbye!");
    }

    /**
     * Load plugin managers
     */
    @SuppressWarnings("unchecked")
    protected void loadManagers() {
        // Load configuration
        this.logger.info("Loading configuration.");
        // Setup configuration before any other managers
        this.configuration = new Configuration(
                provideConfigurationAdapter(),
                provideConfigKeys());

        this.logger.info("Loading commands.");
        // Register plugin commands
        this.commandManager = new PaperCommandManager<>((P) this);
        this.commandManager.setup();

        // Custom plugin loaders
        enable();
        registerListeners(listeners);

        // Register event listeners for this plugin
        this.listeners.forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));

        // Everything loaded successfully
        this.logger.info("Initialization finished.");
    }

    /**
     * Unload plugin managers
     */
    protected void unloadManagers() {
        disable();
        // Close command manager
        if (this.commandManager != null)
            this.commandManager.shutdown();

        this.listeners.forEach(HandlerList::unregisterAll);
        this.listeners.clear();
    }

    @Override
    public void reload() {
        this.configuration.reload();
        this.translationManager.reload();
    }

    /**
     * Registers event listeners for this plugin.
     * Implementations should use this method to register any event listeners.
     */
    protected abstract void registerListeners(@NotNull Set<Listener> listeners);

    @Override
    public LoggerAdapter getLoggerAdapter() {
        return this.logger;
    }

    @Override
    public SchedulerAdapter getSchedulerAdapter() {
        return provideEngine().getSchedulerAdapter();
    }

    @Override
    public DependencyManager getDependencyManager() {
        return provideEngine().getDependencyManager();
    }

    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    public PaperSenderFactory<P> getSenderFactory() {
        return this.senderFactory;
    }

    @Override
    public PaperCommandManager<P> getCommandManager() {
        return this.commandManager;
    }

    @Override
    public ClassPathAppender getClassPathAppender() {
        return this.classPathAppender;
    }

    @Override
    public TranslationManager getTranslationManager() {
        return this.translationManager;
    }

    @Override
    public CountDownLatch getLoadLatch() {
        return this.loadLatch;
    }

    @Override
    public CountDownLatch getEnableLatch() {
        return this.enableLatch;
    }

    @Override
    public @NotNull Type getType() {
        return Type.PAPER;
    }

    @Override
    public @NotNull Instant getStartTime() {
        return this.startTime;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public String getId() {
        return StringUtil.noSpace(getPluginMeta().getName()
                .replace("_", "-")
                .replace(" ", "")
                .toLowerCase(Locale.ROOT));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public String getPluginName() {
        return getPluginMeta().getName();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public String getPluginVersion() {
        return getPluginMeta().getVersion();
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Optional<String> getPluginDescription() {
        return Optional.ofNullable(getPluginMeta().getDescription());
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public Optional<String> getUrl() {
        return Optional.ofNullable(getPluginMeta().getWebsite());
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public List<String> getAuthors() {
        return getPluginMeta().getAuthors();
    }

    @Override
    public Path getSource() {
        return getDataFolder().toPath().toAbsolutePath();
    }

    @Override
    public File getSourceDirectory() {
        return getDataFolder();
    }

    @Override
    public String getServerBrand() {
        return getServer().getName();
    }

    @Override
    public String getServerVersion() {
        return getServer().getVersion() + " - " + getServer().getBukkitVersion();
    }

    @Override
    public Optional<Player> getPlayer(String username) {
        return Optional.ofNullable(getServer().getPlayer(username));
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        return Optional.ofNullable(getServer().getPlayer(uniqueId));
    }

    @Override
    public Optional<UUID> lookupUniqueId(String username) {
        return Optional.ofNullable(getServer().getOfflinePlayerIfCached(username)).map(OfflinePlayer::getUniqueId);
    }

    @Override
    public Optional<String> lookupUsername(UUID uniqueId) {
        return Optional.of(getServer().getOfflinePlayer(uniqueId)).map(OfflinePlayer::getName);
    }

    @Override
    public int getPlayerCount() {
        return getServer().getOnlinePlayers().size();
    }

    @Override
    public Collection<String> getPlayerList() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        List<String> list = new ArrayList<>(players.size());

        for (Player player : players) {
            list.add(player.getName());
        }
        return list;
    }

    @Override
    public Collection<UUID> getOnlinePlayers() {
        Collection<? extends Player> players = getServer().getOnlinePlayers();
        List<UUID> list = new ArrayList<>(players.size());

        for (Player player : players) {
            list.add(player.getUniqueId());
        }
        return list;
    }

    @Override
    public boolean isPlayerOnline(UUID uniqueId) {
        Player player = getServer().getPlayer(uniqueId);
        return player != null && player.isOnline();
    }

    @Override
    public boolean isPlayerConnected(UUID uniqueId) {
        Player player = getServer().getPlayer(uniqueId);
        return player != null && player.isConnected();
    }

    @Override
    public Stream<Sender> getOnlineSenders() {
        List<Player> players = new ArrayList<>(getServer().getOnlinePlayers());
        return Stream.concat(
                Stream.of(getConsoleSender()),
                players.stream().map(p -> getSenderFactory().wrap(p))
        );
    }

    @Override
    public Sender getConsoleSender() {
        return getSenderFactory().wrap(getServer().getConsoleSender());
    }

    /**
     * Gets a namespaceKey with the identifier of this plugin
     *
     * @param key of this namespace
     * @return a namespaceKey with this plugin as namespace
     */
    public final @NotNull NamespacedKey key(@NotNull String key) {
        return new NamespacedKey(this, key);
    }
}
