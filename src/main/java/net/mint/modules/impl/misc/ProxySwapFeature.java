package net.mint.modules.impl.misc;

import com.google.gson.*;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.BooleanSetting;
import net.mint.settings.types.NumberSetting;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.CookieStorage;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@FeatureInfo(name = "ProxySwap", category = Category.Misc)
public class ProxySwapFeature extends Feature {

    public final BooleanSetting autoReconnect = new BooleanSetting("Auto Reconnect", "Automatically reconnect after switching proxy", true);
    public final NumberSetting reconnectDelay = new NumberSetting("Reconnect Delay", "Delay before reconnecting (seconds)", 2.0f, 0.5f, 10.0f);
    public final BooleanSetting rotateOrder = new BooleanSetting("Rotate Order", "Use proxies in order (otherwise random)", true);
    public final BooleanSetting bypassBan = new BooleanSetting("Bypass Ban", "Try to reconnect even if banned", true);

    private ServerInfo lastServer;
    private long reconnectAtMs = -1L;
    private boolean isReconnecting = false;
    private int currentProxyIndex = 0;
    private List<String> proxies = new ArrayList<>();

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (!isEnabled()) return;
        if (getNull()) {
            resetState();
            return;
        }

        ServerInfo cur = mc.getCurrentServerEntry();
        if (cur != null) {
            lastServer = cur;
        }

        boolean disconnected = mc.currentScreen instanceof DisconnectedScreen;

        if (!disconnected) {
            if (!isReconnecting) {
                resetState();
            } else {
                if (mc.currentScreen instanceof ConnectScreen || getNull()) {
                    return;
                }
                resetState();
            }
            return;
        }

        if (lastServer == null) return;

        if (reconnectAtMs < 0L && autoReconnect.getValue()) {
            String disconnectReason = "";
            if (mc.currentScreen instanceof DisconnectedScreen ds) {
                Text reasonText = ds.getNarratedTitle();
                if (reasonText != null) {
                    disconnectReason = reasonText.getString().toLowerCase();
                }
            }

            boolean isBanned = bypassBan.getValue() && (disconnectReason.contains("banned") || disconnectReason.contains("бан"));
            boolean isKicked = disconnectReason.contains("kicked") || disconnectReason.contains("disconnected") || disconnectReason.contains("connection lost") || disconnectReason.isEmpty();

            if (!isBanned && !isKicked) {
                resetState();
                return;
            }

            List<String> loadedProxies = loadProxies();
            if (loadedProxies.isEmpty()) {
                sendChatMessage("§c[ProxySwap] No proxies found in proxies.json!");
                resetState();
                return;
            }

            this.proxies = loadedProxies;

            String newProxy = getNextProxy();
            if (newProxy == null) {
                sendChatMessage("§c[ProxySwap] Failed to select new proxy.");
                resetState();
                return;
            }

            sendChatMessage("§a[ProxySwap] " + (isBanned ? "Banned" : "Kicked") + "! Switching to proxy: " + newProxy);

            lastServer.address = newProxy;

            reconnectAtMs = System.currentTimeMillis() + (long) (reconnectDelay.getValue().doubleValue() * 1000.0);
            isReconnecting = true;
        }

        if (!isReconnecting) return;

        long now = System.currentTimeMillis();
        long leftMs = reconnectAtMs - now;

        if (leftMs <= 0L) {
            isReconnecting = false;
            attemptReconnect(mc.currentScreen);
            resetState();
        } else {
            int leftSec = (int) Math.max(0L, (leftMs + 999L) / 1000L);
            if (mc.inGameHud != null) {
                mc.inGameHud.setOverlayMessage(Text.literal("§bProxySwap: §fSwitching proxy... Reconnecting in §e" + leftSec + "s"), false);
            }
        }
    }

    private String getNextProxy() {
        if (proxies.isEmpty()) return null;

        String selected;
        if (rotateOrder.getValue()) {
            selected = proxies.get(currentProxyIndex % proxies.size());
            currentProxyIndex++;
        } else {
            int randomIndex = (int) (Math.random() * proxies.size());
            selected = proxies.get(randomIndex);
        }
        return selected;
    }

    private void attemptReconnect(Screen parent) {
        if (lastServer == null) return;

        try {
            ServerAddress addr = ServerAddress.parse(lastServer.address);
            ConnectScreen.connect(parent, mc, addr, lastServer, false, (CookieStorage) null);
            sendChatMessage("§a[ProxySwap] Connecting...");
        } catch (Exception e) {
            sendChatMessage("§c[ProxySwap] Connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void resetState() {
        reconnectAtMs = -1L;
        isReconnecting = false;
    }

    @Override
    public void onEnable() {
        resetState();
        loadProxies();
    }

    @Override
    public void onDisable() {
        resetState();
        if (mc.inGameHud != null) {
            mc.inGameHud.setOverlayMessage(Text.literal(""), false);
        }
    }

    private Path getProxiesFile() {
        return getMinecraftDir().resolve("mint").resolve("proxies.json");
    }

    private List<String> loadProxies() {
        Path file = getProxiesFile();
        if (!Files.exists(file)) {
            createDefaultProxiesFile();
            return new ArrayList<>();
        }
        try {
            String content = Files.readString(file);
            JsonArray jsonArray = new Gson().fromJson(content, JsonArray.class);
            List<String> list = new ArrayList<>();
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (jsonArray.get(i).isJsonPrimitive()) {
                        list.add(jsonArray.get(i).getAsString());
                    }
                }
            }
            return list;
        } catch (IOException | JsonParseException e) {
            sendChatMessage("§c[ProxySwap] Error reading proxies.json: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void createDefaultProxiesFile() {
        try {
            Path file = getProxiesFile();
            Files.createDirectories(file.getParent());
            String defaultContent = "[\n  \"ip:port\",\n  \"user:pass@ip:port\"\n]";
            Files.writeString(file, defaultContent);
            System.out.println("[ProxySwap] Created default proxies.json at: " + file.toAbsolutePath());
            sendChatMessage("§e[ProxySwap] Created default proxies.json. Please edit it with your proxies.");
        } catch (IOException e) {
            System.err.println("[ProxySwap] Could not create proxies.json: " + e.getMessage());
        }
    }

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

    private void sendChatMessage(String msg) {
        if (mc.player != null) {
            mc.player.sendMessage(Text.literal(msg), false);
        } else {
            System.out.println(msg);
        }
    }
}