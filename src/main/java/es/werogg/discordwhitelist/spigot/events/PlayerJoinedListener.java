package es.werogg.discordwhitelist.spigot.events;

import es.werogg.discordwhitelist.managers.ConfigManager;
import es.werogg.discordwhitelist.managers.DiscordManager;
import es.werogg.discordwhitelist.managers.VerifiedUsersManager;
import es.werogg.discordwhitelist.utils.DiscordVerificationUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinedListener implements Listener {

    private ConfigManager configManager = ConfigManager.getInstance();
    private VerifiedUsersManager verifiedUsersManager = VerifiedUsersManager.getInstance();
    private DiscordManager discordManager = DiscordManager.getInstance();

    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        DiscordVerificationUtil.playerCheck(player);
    }





}
