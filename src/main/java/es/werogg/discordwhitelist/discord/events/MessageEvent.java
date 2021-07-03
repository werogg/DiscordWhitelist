package es.werogg.discordwhitelist.discord.events;

import es.werogg.discordwhitelist.discord.commands.CommandExecutor;
import es.werogg.discordwhitelist.managers.LogManager;
import es.werogg.discordwhitelist.utils.DiscordCommandParser;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MessageEvent extends ListenerAdapter {

    public HashMap<String, CommandExecutor> commandHashMap;

    public MessageEvent(HashMap<String, CommandExecutor> commandHashMap) {
        this.commandHashMap = commandHashMap;
        LogManager.getInstance().info("Registering commands...");
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Message message = event.getMessage();
        String[] args = DiscordCommandParser.parse(event.getMessage().getContentRaw());
        Member member = event.getMember();
        TextChannel textChannel = event.getChannel();

        // Execute every registered command
        for (Map.Entry<String, CommandExecutor> entry : commandHashMap.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(args[0]))
                entry.getValue().execute(member, textChannel, message, args);
        }
    }
}
