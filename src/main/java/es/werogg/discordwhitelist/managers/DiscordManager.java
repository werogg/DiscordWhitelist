package es.werogg.discordwhitelist.managers;

import es.werogg.discordwhitelist.DiscordWhitelist;
import es.werogg.discordwhitelist.discord.commands.CommandExecutor;
import es.werogg.discordwhitelist.discord.events.MessageEvent;
import es.werogg.discordwhitelist.discord.events.RoleUpdatedEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class DiscordManager {

    private static DiscordManager discordManager;
    private LogManager logManager = LogManager.getInstance();
    private JDA jda;
    public static HashMap<String, CommandExecutor> commandHashMap = new HashMap<>();

    public void initBot(String token) throws LoginException {
        logManager.info("Bot being initialized...");
        jda = JDABuilder.create(token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGE_REACTIONS, GatewayIntent.DIRECT_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_BANS, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_INVITES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_VOICE_STATES ).build();
        jda.getPresence().setPresence(Activity.streaming("TWITCH.TV/WEROGG", "https://twitch.tv/werogg"), true);
        jda.addEventListener(new MessageEvent(commandHashMap));
        jda.addEventListener(new RoleUpdatedEvent());
    }

    public void registerCommand(String string, CommandExecutor commandExecutor) {
        commandHashMap.put(string, commandExecutor);
    }

    public JDA getJda() {
        return jda;
    }

    public void close() {
        jda.shutdown();
    }

    public static DiscordManager getInstance() {
        if (discordManager == null) {
            discordManager = new DiscordManager();
        }
        return discordManager;
    }

}
