package es.werogg.discordwhitelist.utils;

public class DiscordCommandParser {

    public static String[] parse(String command) {
        String[] args = command.split(" ");

        return args;
    }

}
