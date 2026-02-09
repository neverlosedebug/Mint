package net.melbourne.utils.miscellaneous.irc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.melbourne.Manager;
import net.melbourne.Managers;
import net.melbourne.utils.Globals;
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
    private final Set<UUID> melbourneUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<UUID, String> mintByUuid = new ConcurrentHashMap<>();

    public BotManager() {
        super("Bot", "protectionn");
    }

    @Override
    public void onInit() {
        INSTANCE = this;
        Managers.BOT = this;
        loadAuthData();
    }

    private Path getAuthFile() {
        return getMinecraftDir().resolve("melbourne").resolve("auth.json");
    }

    private Path getLogFile() {
        return getMinecraftDir().resolve("melbourne").resolve("irc_log.txt");
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

    public boolean isMelbourneUser(UUID uuid) {
        return melbourneUsers.contains(uuid);
    }

    public String getMintNameFor(UUID uuid) {
        return mintByUuid.get(uuid);
    }

    public void updateMelbourneUser(UUID uuid, String mintName) {
        melbourneUsers.add(uuid);
        mintByUuid.put(uuid, mintName);
    }

    public void removeMelbourneUser(UUID uuid) {
        melbourneUsers.remove(uuid);
        mintByUuid.remove(uuid);
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
    public Set<UUID> getMelbourneUsers() { return Collections.unmodifiableSet(melbourneUsers); }
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