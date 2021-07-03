package es.werogg.discordwhitelist.managers;

import es.werogg.discordwhitelist.DiscordWhitelist;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class VerifiedUsersManager {

    private static VerifiedUsersManager verifiedUsersManager;
    private HashMap<String, String> verifiedUsersMap = new HashMap<>();
    private HashMap<String, String> usersChecking = new HashMap<>();

    public boolean registerUser(String uuid, String discordId) {
        if (!verifiedUsersMap.containsKey(uuid)) {
            verifiedUsersMap.put(uuid, discordId);
            return true;
        }
        return false;
    }

    public boolean unregisterUser(String uuid) {
        if (verifiedUsersMap.containsKey(uuid)) {
            verifiedUsersMap.remove(uuid);
            return true;
        }
        return false;
    }

    public void addUserBeingVerified(String uuid, String discordId) {
        usersChecking.put(uuid, discordId);
    }

    public boolean removeUserBeingVerified(String uuid) {
        if (usersChecking.containsKey(uuid)) {
            usersChecking.remove(uuid);
            return true;
        }
        return false;
    }

    public boolean isUserBeingVerified(String uuid, String discordId) {
        return usersChecking.containsKey(uuid) && usersChecking.get(uuid).equals(discordId);
    }

    public HashMap<String, String> getVerifiedPlayers() {
        return verifiedUsersMap;
    }

    public boolean isVerified(String uuid) {
        return verifiedUsersMap.containsKey(uuid);
    }

    public String getDiscordByUUID(String uuid) {
        return verifiedUsersMap.get(uuid);
    }

    public String getUUIDByDiscord(String discordId) {
        return verifiedUsersMap.entrySet()
                .stream()
                .filter(entry -> discordId.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .findFirst().get();
    }

    public void saveData() throws IOException {
        FileOutputStream fos = new FileOutputStream(DiscordWhitelist.getInstance().getDataFolder().getAbsolutePath()
                + "/verified-players.data");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(verifiedUsersMap);
        oos.close();
        fos.close();
    }

    public void loadData() throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(DiscordWhitelist.getInstance().getDataFolder().getAbsolutePath()
                + "/verified-players.data");
        ObjectInputStream ois = new ObjectInputStream(fis);
        HashMap readObject = (HashMap) ois.readObject();


        // HashMap content instances should be checked
        verifiedUsersMap = readObject;
        ois.close();
        fis.close();
    }

    public static VerifiedUsersManager getInstance() {
        if (verifiedUsersManager == null) {
            verifiedUsersManager = new VerifiedUsersManager();
        }
        return verifiedUsersManager;
    }
}
