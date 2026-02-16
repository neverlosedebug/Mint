package net.mint.utils.miscellaneous.irc;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.mint.Manager;
import net.mint.Managers;
import net.mint.utils.Globals;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class BotManager extends Manager implements Globals {

    public static BotManager INSTANCE;
    private String authedMintUsername = "Not logged in";
    private String authedMintRank = "User";
    private final Map<UUID, String> playerCapes = new ConcurrentHashMap<>();
    private final Set<UUID> mintUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<UUID, String> mintByUuid = new ConcurrentHashMap<>();

    public BotManager() {
        super("Bot", "protectionn");
    }

    @Override
    public void onInit() {
        INSTANCE = this;
        Managers.BOT = this;
        loadAuthData();
        loadMintUsers();
    }

    private Path getAuthFile() {
        return getMinecraftDir().resolve("mint").resolve("auth.json");
    }

    private Path getMintUsersFile() {
        return getMinecraftDir().resolve("mint").resolve("mintusers.json");
    }

    private Path getLogFile() {
        return getMinecraftDir().resolve("mint").resolve("irc_log.txt");
    }

    private void loadAuthData() {
        try {
            Path authFile = getAuthFile();
            if (!Files.exists(authFile)) {
                createDefaultAuthFile();
                return;
            }

            String content = Files.readString(authFile);
            JsonObject json = new Gson().fromJson(content, JsonObject.class);

            if (json.has("username") && json.get("username").isJsonPrimitive()) {
                this.authedMintUsername = json.get("username").getAsString().trim();
            }

            if (json.has("role") && json.get("role").isJsonPrimitive()) {
                this.authedMintRank = normalizeRank(json.get("role").getAsString().trim());
            }

        } catch (IOException | JsonParseException e) {
            System.err.println("[BotManager] Failed to load auth.json: " + e.getMessage());
            this.authedMintUsername = "Error";
            this.authedMintRank = "Guest";
        }
    }

    private void loadMintUsers() {
        try {
            Path usersFile = getMintUsersFile();
            if (!Files.exists(usersFile)) {
                createDefaultMintUsersFile();
                return;
            }

            String content = Files.readString(usersFile);
            JsonArray jsonArray = new Gson().fromJson(content, JsonArray.class);

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JsonObject userObj = jsonArray.get(i).getAsJsonObject();
                    if (userObj.has("uuid") && userObj.has("name")) {
                        try {
                            UUID uuid = UUID.fromString(userObj.get("uuid").getAsString());
                            String name = userObj.get("name").getAsString();
                            mintUsers.add(uuid);
                            mintByUuid.put(uuid, name);
                        } catch (IllegalArgumentException e) {
                            System.err.println("[BotManager] Invalid UUID in mintusers.json: " + userObj.get("uuid").getAsString());
                        }
                    }
                }
            }
        } catch (IOException | JsonParseException e) {
            System.err.println("[BotManager] Failed to load mintusers.json: " + e.getMessage());
        }
    }

    private void createDefaultAuthFile() {
        try {
            Path authFile = getAuthFile();
            Files.createDirectories(authFile.getParent());
            String defaultContent = "{\n  \"username\": \"Name\",\n  \"role\": \"User\"\n}";
            Files.writeString(authFile, defaultContent);
            System.out.println("[BotManager] Created default auth.json at: " + authFile.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[BotManager] Could not create auth.json: " + e.getMessage());
        }
    }

    private void createDefaultMintUsersFile() {
        try {
            Path usersFile = getMintUsersFile();
            Files.createDirectories(usersFile.getParent());
            String defaultContent = "[\n  {\n    \"uuid\": \"00000000-0000-0000-0000-000000000000\",\n    \"name\": \"ExamplePlayer\"\n  }\n]";
            Files.writeString(usersFile, defaultContent);
            System.out.println("[BotManager] Created default mintusers.json at: " + usersFile.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("[BotManager] Could not create mintusers.json: " + e.getMessage());
        }
    }

    private String normalizeRank(String rank) {
        if (rank == null || rank.isEmpty()) return "User";
        String r = rank.toLowerCase().trim();
        if (r.isEmpty()) return "User";
        return Character.toUpperCase(r.charAt(0)) + r.substring(1);
    }

    public String getAuthedMintUsername() {
        return authedMintUsername;
    }

    public String getAuthedMintRank() {
        return authedMintRank;
    }

    public boolean isAuthed() {
        return !"Not logged in".equals(authedMintUsername) && authedMintUsername != null;
    }

    public boolean isConnected() {
        return true;
    }

    public void sendMessageFromPlayer(UUID playerUUID, String playerName, String message) {
        System.out.println("[IRC] Send from " + playerName + " (" + playerUUID + "): " + message);
        logToFile(playerName, message);
        if (mc.player != null && mc.world != null) {
            mc.player.sendMessage(Text.literal("§7[IRC] §f" + playerName + ": §a" + message), false);
        }
    }

    public void logToFile(String playerName, String message) {
        try {
            Path logFile = getLogFile();
            Files.createDirectories(logFile.getParent());
            String entry = "[" + java.time.LocalDateTime.now() + "] [" + playerName + "]: " + message + System.lineSeparator();
            Files.write(logFile, entry.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("[BotManager] Failed to write log file: " + e.getMessage());
        }
    }

    public boolean isMintUser(UUID uuid) {
        return mintUsers.contains(uuid);
    }

    public String getMintNameFor(UUID uuid) {
        return mintByUuid.get(uuid);
    }

    public void updateMintUser(UUID uuid, String mintName) {
        mintUsers.add(uuid);
        mintByUuid.put(uuid, mintName);
        saveMintUsers();
    }

    public void removeMintUser(UUID uuid) {
        mintUsers.remove(uuid);
        mintByUuid.remove(uuid);
        saveMintUsers();
    }

    private void saveMintUsers() {
        try {
            Path usersFile = getMintUsersFile();
            Files.createDirectories(usersFile.getParent());
            JsonArray jsonArray = new JsonArray();
            for (UUID uuid : mintUsers) {
                JsonObject obj = new JsonObject();
                obj.addProperty("uuid", uuid.toString());
                obj.addProperty("name", mintByUuid.getOrDefault(uuid, "Unknown"));
                jsonArray.add(obj);
            }
            Files.writeString(usersFile, new Gson().toJson(jsonArray));
        } catch (IOException e) {
            System.err.println("[BotManager] Failed to save mintusers.json: " + e.getMessage());
        }
    }

    public void sendPingString() {
        if (mc.player == null) return;
        String message = "§a[IRC] §f" + mc.player.getName().getString() + " §7has pinged their location!";
        System.out.println("[IRC] " + message);
        mc.player.sendMessage(Text.literal(message), false);
        logToFile(mc.player.getName().getString(), "has pinged their location!");
    }

    public void requestPing(String target) {
        if (mc.player == null) return;
        String message = "§a[IRC] §f" + mc.player.getName().getString() + " §7requested a ping from §f" + target + "§7.";
        System.out.println("[IRC] " + message);
        mc.player.sendMessage(Text.literal(message), false);
        logToFile(mc.player.getName().getString(), "requested a ping from " + target + ".");
    }

    public String getPlayerCape(UUID uuid) { return null; }
    public Set<UUID> getmintUsers() { return Collections.unmodifiableSet(mintUsers); }
    public void sendMessageToIrc(String message) { }

    private static Path getMinecraftDir() {
        String os = System.getProperty("os.name").toLowerCase();
        Path userHome = Path.of(System.getProperty("user.home"));
        if (os.contains("win")) {
            return userHome.resolve("AppData").resolve("Roaming").resolve(".minecraft");
        } else if (os.contains("mac")) {
            return userHome.resolve("Library").resolve("Application Support").resolve("minecraft");
        } else {
            return userHome.resolve(".minecraft");
        }
    }
}