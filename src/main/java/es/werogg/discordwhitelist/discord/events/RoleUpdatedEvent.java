package es.werogg.discordwhitelist.discord.events;

import es.werogg.discordwhitelist.DiscordWhitelist;
import es.werogg.discordwhitelist.managers.VerifiedUsersManager;
import es.werogg.discordwhitelist.utils.DiscordVerificationUtil;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class RoleUpdatedEvent extends ListenerAdapter {

    private VerifiedUsersManager verifiedUsersManager = VerifiedUsersManager.getInstance();

    @Override
    public void onGuildMemberRoleAdd(@NotNull GuildMemberRoleAddEvent event) {
        String uuid = verifiedUsersManager.getUUIDByDiscord(event.getMember().getId());
        Player player = DiscordWhitelist.getInstance().getServer().getPlayer(UUID.fromString(uuid));

        if (player == null) {
            return;
        }

        DiscordWhitelist.getInstance().getServer().getScheduler().runTask(DiscordWhitelist.getInstance(), new Runnable() {
            @Override
            public void run() {
                DiscordVerificationUtil.playerCheck(player);
            }
        });

    }

    @Override
    public void onGuildMemberRoleRemove(@NotNull GuildMemberRoleRemoveEvent event) {
        String uuid = verifiedUsersManager.getUUIDByDiscord(event.getMember().getId());
        Player player = DiscordWhitelist.getInstance().getServer().getPlayer(UUID.fromString(uuid));

        if (player == null) {
            return;
        }

        DiscordWhitelist.getInstance().getServer().getScheduler().runTask(DiscordWhitelist.getInstance(), new Runnable() {
            @Override
            public void run() {
                DiscordVerificationUtil.playerCheck(player);
            }
        });
    }
}
