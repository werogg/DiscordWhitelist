package es.werogg.discordwhitelist.spigot.events;

import es.werogg.discordwhitelist.DiscordWhitelist;
import es.werogg.discordwhitelist.managers.ConfigManager;
import es.werogg.discordwhitelist.managers.DiscordManager;
import es.werogg.discordwhitelist.managers.VerifiedUsersManager;
import es.werogg.discordwhitelist.utils.DiscordVerificationUtil;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;

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
