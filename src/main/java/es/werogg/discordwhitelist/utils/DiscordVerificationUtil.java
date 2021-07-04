package es.werogg.discordwhitelist.utils;

import es.werogg.discordwhitelist.DiscordWhitelist;
import es.werogg.discordwhitelist.managers.ConfigManager;
import es.werogg.discordwhitelist.managers.DiscordManager;
import es.werogg.discordwhitelist.managers.TranslationsManager;
import es.werogg.discordwhitelist.managers.VerifiedUsersManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;

public class DiscordVerificationUtil {
    private static VerifiedUsersManager verifiedUsersManager = VerifiedUsersManager.getInstance();
    private static ConfigManager configManager = ConfigManager.getInstance();
    private static DiscordManager discordManager = DiscordManager.getInstance();
    private static TranslationsManager translationsManager = TranslationsManager.getInstance();

    public static boolean checkWhitelist(Player player) {
        boolean whitelisted = false;
        String discordId = verifiedUsersManager.getDiscordByUUID(player.getUniqueId().toString());
        if (discordId != null) {
            for (String guildId : configManager.getGuilds()) {
                if (whitelisted) break;
                Guild guild  = discordManager.getJda().getGuildById(guildId);
                for (String roleId : configManager.getWhitelistRoles()) {
                    if (whitelisted) break;
                    List<Role> userRoles = guild.getMemberById(discordId).getRoles();
                    for (Role role : userRoles) {
                        if (role.getId().equalsIgnoreCase(roleId)) {
                            whitelisted = true;
                            break;
                        }
                    }
                }
            }
            return whitelisted;
        }
        verifyPlayer(player);
        return false;
    }

    public static void verifyPlayer(Player player) {
        if (!verifiedUsersManager.isVerified(player.getUniqueId().toString())) {

            ComponentBuilder discordComponent = new ComponentBuilder()
                    .color(net.md_5.bungee.api.ChatColor.GREEN)
                    .bold(true)
                    .append(translationsManager.getTranslationsConfig().getString("minecraft-discord-button"))
                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, configManager.getDiscordLink()))
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translationsManager.getTranslationsConfig().getString("minecraft-discord-hover-button"))));

            TextComponent click = new TextComponent(translationsManager.getTranslationsConfig().getString("minecraft-join-the-discord"));
            click.setColor(net.md_5.bungee.api.ChatColor.YELLOW);

            BukkitScheduler scheduler = DiscordWhitelist.getInstance().getServer().getScheduler();

            scheduler.scheduleSyncRepeatingTask(DiscordWhitelist.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (VerifiedUsersManager.getInstance().isVerified(player.getUniqueId().toString())) return;
                    player.spigot().sendMessage(click, new TextComponent(" "), discordComponent.getComponent(0));
                }
            }, 100, 300);

            scheduler.scheduleSyncDelayedTask(DiscordWhitelist.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (!verifiedUsersManager.isVerified(player.getUniqueId().toString())) {
                        player.kickPlayer("Please join discord to get the !verify");
                    }
                }
            }, configManager.getTimeoutTime() * 20L);
        }
    }

    public static void playerCheck(Player player) {
        if (configManager.getWhitelist()) {
            if (!DiscordVerificationUtil.checkWhitelist(player)) {
                if (verifiedUsersManager.isVerified(player.getUniqueId().toString())) {
                    if (configManager.getSpectating()) {
                        player.setGameMode(GameMode.SPECTATOR);
                    } else {
                        player.kickPlayer(ChatColor.translateAlternateColorCodes('&', "You are not whitelisted..."));
                    }
                }
            } else {
                player.setGameMode(GameMode.SURVIVAL);
            }
        } else {
            if (configManager.getVerified()) {
                DiscordVerificationUtil.verifyPlayer(player);
            }
        }
    }
}
