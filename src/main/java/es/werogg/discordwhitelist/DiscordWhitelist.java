package es.werogg.discordwhitelist;

import es.werogg.discordwhitelist.managers.*;
import es.werogg.discordwhitelist.spigot.commands.CommandVerify;
import es.werogg.discordwhitelist.discord.commands.VerifyCommandExecutor;
import es.werogg.discordwhitelist.spigot.events.PlayerJoinedListener;
import es.werogg.discordwhitelist.spigot.events.PlayerMoveListener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class DiscordWhitelist extends JavaPlugin {

    private final ConfigManager configManager = ConfigManager.getInstance();
    private final LogManager logManager = LogManager.getInstance();
    private final DiscordManager discordManager = DiscordManager.getInstance();
    private final VerifiedUsersManager verifiedUsersManager = VerifiedUsersManager.getInstance();
    private final TranslationsManager translationsManager = TranslationsManager.getInstance();
    private static DiscordWhitelist discordWhitelist;

    @Override
    public void onEnable() {
        // Singleton instance assigned
        discordWhitelist = this;

        // General logger assigned
        logManager.setLogger(this.getLogger());

        // Set plugin to config manager and do load checks
        configManager.setPlugin(this);
        configManager.loadInitial();

        // Initialize translations
        translationsManager.initTranslationsConfig();

        // Discord commands must be registered before initializing the bot
        registerDiscordCommands();

        // Initializing websocket connection with bot
        try {
            discordManager.initBot(configManager.getToken());
        } catch (LoginException e) {
            e.printStackTrace(); // Will be reached if bot connection troubles
        }

        // Minecraft commands registering
        registerCommands();
        registerEventListeners();

        // Load verified users
        try {
            VerifiedUsersManager.getInstance().loadData();
        } catch (IOException | ClassNotFoundException e) {
            if (e instanceof FileNotFoundException) {
                logManager.warn("Data file not found, a new one will be created when a user is verified.");
            } else {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        discordManager.close();
        try {
            verifiedUsersManager.saveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerEventListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerJoinedListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
    }

    private void registerDiscordCommands() {
        discordManager.registerCommand("!verify", new VerifyCommandExecutor());
    }

    private void registerCommands() {
        Objects.requireNonNull(this.getCommand("verify")).setExecutor(new CommandVerify());
    }

    public static DiscordWhitelist getInstance() {
        return discordWhitelist;
    }


}
