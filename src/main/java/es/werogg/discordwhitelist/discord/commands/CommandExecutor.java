package es.werogg.discordwhitelist.discord.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public interface CommandExecutor {
    void execute (Member m, TextChannel textChannel, Message command, String[] args);
}
