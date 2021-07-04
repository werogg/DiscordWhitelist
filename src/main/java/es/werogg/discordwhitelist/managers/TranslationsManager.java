package es.werogg.discordwhitelist.managers;

import es.werogg.discordwhitelist.DiscordWhitelist;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class TranslationsManager {

    private static TranslationsManager translationsManager;
    private File translationsConfigFile;
    private FileConfiguration translationsConfig;

    public FileConfiguration getTranslationsConfig() {
        return translationsConfig;
    }

    public void initTranslationsConfig() {
        translationsConfigFile = new File(DiscordWhitelist.getInstance().getDataFolder(), "translations.yml");
        if (!translationsConfigFile.exists()) {
            translationsConfigFile.getParentFile().mkdirs();
            DiscordWhitelist.getInstance().saveResource("translations.yml", false);
        }

        translationsConfig = new YamlConfiguration();

        try {
            translationsConfig.load(translationsConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static TranslationsManager getInstance() {
        if (translationsManager == null) {
            translationsManager = new TranslationsManager();
        }
        return translationsManager;
    }
}
