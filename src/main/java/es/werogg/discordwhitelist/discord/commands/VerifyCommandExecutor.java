package es.werogg.discordwhitelist.discord.commands;

import es.werogg.discordwhitelist.DiscordWhitelist;
import es.werogg.discordwhitelist.managers.ConfigManager;
import es.werogg.discordwhitelist.managers.TranslationsManager;
import es.werogg.discordwhitelist.managers.VerifiedUsersManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.List;
import java.util.Objects;

public class VerifyCommandExecutor implements CommandExecutor {
    private TranslationsManager translationsManager = TranslationsManager.getInstance();
    private ConfigManager configManager = ConfigManager.getInstance();

    @Override
    public void execute(Member m, TextChannel textChannel, Message command, String[] args) {
        List<String> channels = configManager.getVerificationChannels();

        // If no channels in config then all guild channels will be valid for introducing !verify command
        if (channels.size() != 0) {
            if (!channels.contains(textChannel.getId())) return;
        }

        // Error if command format doesn't have 2 arguments
        if (args.length != 2) {
            textChannel.sendMessage(Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("discord-bot-wrong-command"))).queue();
            return;
        }

        // Retrieve the given player from the server
        Player player = DiscordWhitelist.getInstance().getServer().getPlayerExact(args[1]);

        // Case player not online
        if (player == null) {
            textChannel.sendMessage(Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("discord-bot-player-not-online"))).queue();
            return;
        }

        // Player is online so we continue checking if player is already verified
        if (VerifiedUsersManager.getInstance().isVerified(player.getUniqueId().toString())) {
            textChannel.sendMessage(Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("discord-bot-player-already-verified"))).queue();
            return;
        }

        // Player not verified build the request and send it
        ComponentBuilder acceptComponent = new ComponentBuilder()
                .color(ChatColor.GREEN)
                .bold(true)
                .append(translationsManager.getTranslationsConfig().getString("minecraft-accept-button"))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/verify "
                        + player.getUniqueId()
                        + " " + m.getUser().getId()
                        + " " + textChannel.getId()
                        + " " + "accept"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translationsManager.getTranslationsConfig().getString("minecraft-accept-hover-button"))));

        ComponentBuilder denyComponent = new ComponentBuilder()
                .color(ChatColor.RED)
                .bold(true)
                .append(translationsManager.getTranslationsConfig().getString("minecraft-deny-button"))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/verify "
                        + player.getUniqueId()
                        + " " + m.getUser().getId()
                        + " " + textChannel.getId()
                        + " " + "deny"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(translationsManager.getTranslationsConfig().getString("minecraft-deny-hover-button"))));


        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&6----------------------------------------"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("minecraft-verification-request"))).replace("%discord_tag%", m.getUser().getAsTag()));

        TextComponent click = new TextComponent("Click one: ");
        click.setColor(ChatColor.YELLOW);
        player.spigot().sendMessage(acceptComponent.getComponent(0), new TextComponent(" - "), denyComponent.getComponent(0));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&6----------------------------------------"));

        VerifiedUsersManager.getInstance().addUserBeingVerified(player.getUniqueId().toString(), m.getUser().getId());

        // In 2 minutes the request will expire for security
        BukkitScheduler scheduler = DiscordWhitelist.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(DiscordWhitelist.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (VerifiedUsersManager.getInstance().removeUserBeingVerified(player.getUniqueId().toString()))
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("minecraft-verification-expired"))));
            }
        }, 1200);
    }

}
