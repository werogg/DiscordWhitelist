package es.werogg.discordwhitelist.spigot.events;

import es.werogg.discordwhitelist.managers.VerifiedUsersManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent playerMoveEvent) {
        Player player = playerMoveEvent.getPlayer();

        if (!VerifiedUsersManager.getInstance().isVerified(player.getUniqueId().toString())) {
            if (playerMoveEvent.getFrom().getBlockX() != playerMoveEvent.getTo().getBlockY()
            || playerMoveEvent.getFrom().getBlockZ() != playerMoveEvent.getTo().getBlockZ())
            playerMoveEvent.setCancelled(true);
        }
    }
}
