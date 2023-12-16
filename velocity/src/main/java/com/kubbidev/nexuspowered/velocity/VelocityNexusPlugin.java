package com.kubbidev.nexuspowered.velocity;

import com.google.inject.Inject;
import com.kubbidev.java.classpath.ClassPathAppender;
import com.kubbidev.java.config.Configuration;
import com.kubbidev.java.logging.LoggerAdapter;
import com.kubbidev.java.logging.Slf4JLoggerAdapter;
import com.kubbidev.nexuspowered.common.NexusEngine;
import com.kubbidev.nexuspowered.common.NexusPlugin;
import com.kubbidev.nexuspowered.common.engine.dependencies.DependencyManager;
import com.kubbidev.nexuspowered.common.engine.scheduler.SchedulerAdapter;
import com.kubbidev.nexuspowered.common.locale.TranslationManager;
import com.kubbidev.nexuspowered.common.sender.Sender;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

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
public abstract class VelocityNexusPlugin<P extends VelocityNexusPlugin<P>> implements NexusPlugin<P> {

    @Inject
    private ProxyServer proxy;

    @Inject
    private Logger velocityLogger;

    @Inject
    @DataDirectory
    private Path configDirectory;

    @Inject
    private PluginContainer container;

    private boolean isEngine;

    // The plugin logger
    private LoggerAdapter logger;

    // Init during enable
    private Configuration configuration;

    private VelocitySenderFactory<P> senderFactory;
    private VelocityCommandManager<P> commandManager;

    private ClassPathAppender classPathAppender;
    private TranslationManager translationManager;

    // Load/enable latches
    private final CountDownLatch loadLatch = new CountDownLatch(1);
    private final CountDownLatch enableLatch = new CountDownLatch(1);

    // The time when the plugin was enabled
    private Instant startTime;

    // Listeners to register
    private final Set<Object> listeners = new HashSet<>();

    @Override
    public boolean isEngine() {
        return this.isEngine;
    }

    @Override
    public VelocityNexusEngine provideEngine() {
        return VelocityNexusEngine.getInstance();
    }

    // Lifecycle

    @SuppressWarnings("unchecked")
    @Subscribe(order = PostOrder.FIRST)
    public void onEnable(ProxyInitializeEvent event) {
        try {
            this.startTime = Instant.now();
            this.logger = new Slf4JLoggerAdapter(velocityLogger);

            // Load sender factory class path appender
            this.senderFactory = new VelocitySenderFactory<>((P) this);
            this.classPathAppender = new VelocityClassPathAppender(this);

            // Load translations
            this.translationManager = new TranslationManager(this);
            this.translationManager.reload();

            this.isEngine = this instanceof NexusEngine;
            if (this.isEngine) {
                if (!provideEngine().enableEngine()) {
                    getLoggerAdapter().severe("NexusEngine failed to load, NexusPowered will be disable!");
                    return;
                }
            } else {
                provideEngine().hookChild(this);
                getLoggerAdapter().info("Powered by: {}", provideEngine().getPluginName());
            }
            load();
        } finally {
            this.loadLatch.countDown();
        }

        // onEnable (equivalent for JavaPlugin in bukkit)
        try {
            loadManagers();
            getLoggerAdapter().info("Done ({}ms!)", (System.currentTimeMillis() - startTime.toEpochMilli()));
        } finally {
            this.enableLatch.countDown();
        }
    }

    @Subscribe(order = PostOrder.LAST)
    public void onDisable(ProxyShutdownEvent event) {

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
        this.commandManager = new VelocityCommandManager<>((P) this);
        this.commandManager.setup();

        // Custom plugin loaders
        enable();
        registerListeners(listeners);

        // Register event listeners for this plugin
        this.listeners.forEach(o -> this.proxy.getEventManager().register(this, o));

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

        this.listeners.forEach(o -> this.proxy.getEventManager().unregisterListener(this, o));
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
    protected abstract void registerListeners(@NotNull Set<Object> listeners);

    // Getters for the injected velocity instances

    public ProxyServer getProxy() {
        return this.proxy;
    }

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

    public VelocitySenderFactory<P> getSenderFactory() {
        return this.senderFactory;
    }

    @Override
    public VelocityCommandManager<P> getCommandManager() {
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
        return Type.VELOCITY;
    }

    @Override
    public @NotNull Instant getStartTime() {
        return this.startTime;
    }

    @Override
    public String getId() {
        return this.container.getDescription().getId();
    }

    @Override
    public String getPluginName() {
        return this.container.getDescription().getName().orElse("@name@");
    }

    @Override
    public String getPluginVersion() {
        return this.container.getDescription().getVersion().orElse("@version@");
    }

    @Override
    public Optional<String> getPluginDescription() {
        return this.container.getDescription().getDescription();
    }

    @Override
    public Optional<String> getUrl() {
        return this.container.getDescription().getUrl();
    }

    @Override
    public List<String> getAuthors() {
        return this.container.getDescription().getAuthors();
    }

    @Override
    public Path getSource() {
        return this.configDirectory.toAbsolutePath();
    }

    @Override
    public File getSourceDirectory() {
        return getSource().toFile();
    }

    @Override
    public String getServerBrand() {
        return this.proxy.getVersion().getName();
    }

    @Override
    public String getServerVersion() {
        return this.proxy.getVersion().getVersion();
    }

    @Override
    public Optional<Player> getPlayer(String username) {
        return this.proxy.getPlayer(username);
    }

    @Override
    public Optional<Player> getPlayer(UUID uniqueId) {
        return this.proxy.getPlayer(uniqueId);
    }

    @Override
    public Optional<UUID> lookupUniqueId(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<String> lookupUsername(UUID uniqueId) {
        return Optional.empty();
    }

    @Override
    public int getPlayerCount() {
        return this.proxy.getPlayerCount();
    }

    @Override
    public Collection<String> getPlayerList() {
        Collection<? extends Player> players = this.proxy.getAllPlayers();
        List<String> list = new ArrayList<>(players.size());

        for (Player player : players) {
            list.add(player.getUsername());
        }
        return list;
    }

    @Override
    public Collection<UUID> getOnlinePlayers() {
        Collection<? extends Player> players = this.proxy.getAllPlayers();
        List<UUID> list = new ArrayList<>(players.size());

        for (Player player : players) {
            list.add(player.getUniqueId());
        }
        return list;
    }

    @Override
    public boolean isPlayerOnline(UUID uniqueId) {
        Player player = this.proxy.getPlayer(uniqueId).orElse(null);
        return player != null && player.isActive();
    }

    @Override
    public boolean isPlayerConnected(UUID uniqueId) {
        return isPlayerOnline(uniqueId);
    }

    @Override
    public Stream<Sender> getOnlineSenders() {
        return Stream.concat(
                Stream.of(getConsoleSender()),
                this.proxy.getAllPlayers().stream().map(p -> getSenderFactory().wrap(p))
        );
    }

    @Override
    public Sender getConsoleSender() {
        return getSenderFactory().wrap(this.proxy.getConsoleCommandSource());
    }
}
