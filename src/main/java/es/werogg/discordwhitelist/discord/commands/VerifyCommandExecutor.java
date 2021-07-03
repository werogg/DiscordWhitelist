package es.werogg.discordwhitelist.discord.commands;

import es.werogg.discordwhitelist.DiscordWhitelist;
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

public class VerifyCommandExecutor implements CommandExecutor {

    @Override
    public void execute(Member m, TextChannel textChannel, Message command, String[] args) {

        if (args.length != 2) {
            textChannel.sendMessage("Invalid usage! Please use !verify **<minecraft-username>**").queue();
            return;
        }

        Player player = DiscordWhitelist.getInstance().getServer().getPlayerExact(args[1]);

        if (player == null) {
            textChannel.sendMessage("Player is not online!").queue();
            return;
        }

        if (VerifiedUsersManager.getInstance().isVerified(player.getUniqueId().toString())) {
            textChannel.sendMessage("User already verified.").queue();
            return;
        }

        ComponentBuilder acceptComponent = new ComponentBuilder()
                .color(ChatColor.GREEN)
                .bold(true)
                .append("[Accept]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/verify "
                        + player.getUniqueId()
                        + " " + m.getUser().getId()
                        + " " + textChannel.getId()
                        + " " + "accept"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to accept!")));

        ComponentBuilder denyComponent = new ComponentBuilder()
                .color(ChatColor.RED)
                .bold(true)
                .append("[Deny]")
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/verify "
                        + player.getUniqueId()
                        + " " + m.getUser().getId()
                        + " " + textChannel.getId()
                        + " " + "deny"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to deny!")));


        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&6----------------------------------------"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eVerification request from &a&b"
                + m.getUser().getAsTag()));

        TextComponent click = new TextComponent("Click one: ");
        click.setColor(ChatColor.YELLOW);
        player.spigot().sendMessage(acceptComponent.getComponent(0), new TextComponent(" - "), denyComponent.getComponent(0));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&6----------------------------------------"));

        VerifiedUsersManager.getInstance().addUserBeingVerified(player.getUniqueId().toString(), m.getUser().getId());

        BukkitScheduler scheduler = DiscordWhitelist.getInstance().getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(DiscordWhitelist.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (VerifiedUsersManager.getInstance().removeUserBeingVerified(player.getUniqueId().toString()))
                    player.sendMessage("Verification expired.");
            }
        }, 1200);
    }

}
