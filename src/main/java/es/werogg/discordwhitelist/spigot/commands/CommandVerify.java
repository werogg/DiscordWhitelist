package es.werogg.discordwhitelist.spigot.commands;

import es.werogg.discordwhitelist.DiscordWhitelist;
import es.werogg.discordwhitelist.managers.ConfigManager;
import es.werogg.discordwhitelist.managers.DiscordManager;
import es.werogg.discordwhitelist.managers.TranslationsManager;
import es.werogg.discordwhitelist.managers.VerifiedUsersManager;
import es.werogg.discordwhitelist.utils.DiscordVerificationUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class CommandVerify implements CommandExecutor {

    private ConfigManager configManager = ConfigManager.getInstance();
    private TranslationsManager translationsManager = TranslationsManager.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("verify")) {
            // Check sender is a player
            if (sender instanceof Player) {
                Player player = (Player) sender;

                // Command format -> /verify list
                if (args[0].equalsIgnoreCase("list") && player.hasPermission("discordwhitelist.verify.list")) {
                    HashMap<String, String> map = VerifiedUsersManager.getInstance().getVerifiedPlayers();

                    // Print message
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&6----------------------------------------"));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&eVerified players: "));
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Map.Entry<String, String> entry : map.entrySet()) {
                        Player player1 = DiscordWhitelist.getInstance().getServer().getPlayer(UUID.fromString(entry.getKey()));
                        if (player1 == null) continue;
                        stringBuilder.append(player1.getName()).append(", ");
                    }
                    if (stringBuilder.length() > 2)
                        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a" + stringBuilder.toString()));
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            "&6----------------------------------------"));
                    return true;
                }

                // Command format -> /verify remove <username>
                if (args[0].equalsIgnoreCase("remove") && player.hasPermission("discordwhitelist.verify.remove")) {
                    if (args.length == 1) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("minecraft-verify-remove-player-not-found"))));
                        return false;
                    }
                    OfflinePlayer offlinePlayer = DiscordWhitelist.getInstance().getServer().getOfflinePlayer(args[1]);
                    if (VerifiedUsersManager.getInstance().unregisterUser(offlinePlayer.getUniqueId().toString())) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("minecraft-user-verification-removed"))));
                        return true;
                    }
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("minecraft-user-remove-not-found"))));
                    return false;
                }

                // Command format -> /verify <uuid> <discordId> <channelId> <action>
                // Command not intended for public usage.
                if (args.length != 4) {

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("minecraft-command-format-error"))));
                    return false;
                }

                // TODO: ChannelId is not checked so player can manually modify it and send it to unwanted channel
                UUID uuid = UUID.fromString(args[0]);
                String discordId = args[1];
                String channelId = args[2];
                String action = args[3];

                // Check if user is in verification process
                if (VerifiedUsersManager.getInstance().isUserBeingVerified(uuid.toString(), discordId)) {

                    // Minecraft user accepted verification
                    if (action.equals("accept")) {

                        VerifiedUsersManager.getInstance().registerUser(uuid.toString(), discordId);

                        boolean error = false;
                        try {
                            VerifiedUsersManager.getInstance().saveData();
                        } catch (IOException e) {
                            e.printStackTrace();
                            error = true;
                        }

                        if (error) return false;

                        VerifiedUsersManager.getInstance().removeUserBeingVerified(uuid.toString());
                        DiscordManager.getInstance().getJda().getTextChannelById(channelId)
                                .sendMessage(
                                        new EmbedBuilder()
                                                .setTitle(translationsManager.getTranslationsConfig().getString("discord-player-verified-title"))
                                                .setDescription(translationsManager.getTranslationsConfig().getString("discord-player-verified-description").replace("%player%", player.getName()))
                                                .setColor(new Color(configManager.getRaccept(), configManager.getGaccept(), configManager.getBaccept()))
                                                .setImage("https://crafatar.com/avatars/" + player.getUniqueId())
                                                .setFooter("Plugin made by https://twitch.tv/werogg")
                                                .build()
                                ).queue();

                        player.sendMessage("Your account has been linked to " + Objects.requireNonNull(DiscordManager.getInstance().getJda().getUserById(discordId)).getAsTag());

                        // Check user is whitelisted
                        if (!DiscordVerificationUtil.checkWhitelist(player)) {
                            if (configManager.getSpectating()) {
                                player.setGameMode(GameMode.SPECTATOR);
                            } else {
                                player.kickPlayer("You are not whitelisted...");
                            }
                        } else {
                            player.setGameMode(GameMode.SURVIVAL);
                        }

                        return true;

                    // Minecraft user rejected verification
                    } else {
                        VerifiedUsersManager.getInstance().removeUserBeingVerified(uuid.toString());
                        DiscordManager.getInstance().getJda().getTextChannelById(channelId)
                                .sendMessage(
                                        new EmbedBuilder()
                                                .setTitle(translationsManager.getTranslationsConfig().getString("discord-player-denied-title"))
                                                .setDescription(Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("discord-player-denied-description")).replace("%player%", player.getName()))
                                                .setColor(new Color(configManager.getRdeny(), configManager.getGdeny(), configManager.getBdeny()))
                                                .setImage("https://crafatar.com/avatars/" + player.getUniqueId())
                                                .setFooter("Plugin made by https://twitch.tv/werogg")
                                                .build()
                                ).queue();
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(translationsManager.getTranslationsConfig().getString("minecraft-player-verification-rejected"))));
                        return false;
                    }
                }
                player.sendMessage("You don't have any verification request incoming.");
            }
        }
        return false;
    }
}
