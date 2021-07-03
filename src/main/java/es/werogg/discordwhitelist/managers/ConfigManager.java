package es.werogg.discordwhitelist.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private static ConfigManager configManager;
    private Plugin plugin;
    private FileConfiguration fileConfiguration;
    private LogManager logManager = LogManager.getInstance();

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
        fileConfiguration = plugin.getConfig();
    }

    public void loadInitial() {
        File config = new File(plugin.getDataFolder() + File.separator + "config.yml");

        logManager.info("Trying to load the config file...");

        if (!config.exists()) {
            logManager.warn("A config file was not found, creating a new one...");
            plugin.getConfig().options().copyDefaults(true);
            plugin.saveConfig();
        }

        logManager.info("Config file successfully loaded!");
    }

    public String getToken() {
        return fileConfiguration.getString("discord-token");
    }

    public String getVerifyChannel() {
        return fileConfiguration.getString("verify-channel");
    }

    public Boolean getWhitelist() {
        return fileConfiguration.getBoolean("whitelist-on");
    }

    public Boolean getVerified() {
        return fileConfiguration.getBoolean("verified-on");
    }

    public Boolean getSpectating() {
        return fileConfiguration.getBoolean("enable-spectating");
    }

    public List<String> getGuilds() {
        return fileConfiguration.getStringList("server-ids");
    }

    public List<String> getWhitelistRoles() {
       return fileConfiguration.getStringList("whitelisted-roles");
    }

    public static ConfigManager getInstance() {
        if (configManager == null) {
            configManager = new ConfigManager();
        }
        return configManager;
    }
}