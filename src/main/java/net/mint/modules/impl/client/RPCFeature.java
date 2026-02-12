package net.mint.modules.impl.client;

import net.mint.Mint;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.TickEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.utils.discord.DiscordEventHandlers;
import net.mint.utils.discord.DiscordRichPresence;
import net.mint.utils.discord.RPC;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@FeatureInfo(name = "RPC", category = Category.Client)
public class RPCFeature extends Feature {

    private static final String APPLICATION_ID = "1471234138672726161";

    private final RPC discordRpc = RPC.INSTANCE;
    private DiscordRichPresence presence;
    private ScheduledExecutorService executor;
    private boolean started = false;

    @Override
    public void onEnable() {
        if (getNull()) return;
        startRpc();
    }

    @Override
    public void onDisable() {
        stopRpc();
    }

    private void startRpc() {
        if (started) return;
        started = true;

        DiscordEventHandlers handlers = new DiscordEventHandlers();
        discordRpc.Discord_Initialize(APPLICATION_ID, handlers, true, "");

        presence = new DiscordRichPresence();
        presence.startTimestamp = System.currentTimeMillis() / 1000L;
        presence.largeImageText = Mint.NAME + " " + Mint.MOD_VERSION;
        presence.largeImageKey = "https://i.postimg.cc/4xnmcHF8/icon.png";

        discordRpc.Discord_UpdatePresence(presence);

        executor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "Mint-RPC-Thread");
            t.setDaemon(true);
            return t;
        });

        executor.scheduleAtFixedRate(this::updatePresence, 0, 2, TimeUnit.SECONDS);
    }

    private void updatePresence() {
        if (!isEnabled() || getNull()) {
            return;
        }

        presence.details = getDetails();
        presence.state = Mint.NAME + " " + Mint.MOD_VERSION;
        discordRpc.Discord_UpdatePresence(presence);
        discordRpc.Discord_RunCallbacks();
    }

    private String getDetails() {
        if (mc.currentScreen instanceof TitleScreen) {
            return "In main menu";
        } else if (mc.currentScreen instanceof MultiplayerScreen || mc.currentScreen instanceof AddServerScreen) {
            return "Choosing a server";
        } else if (mc.getCurrentServerEntry() != null) {
            return "Playing on " + mc.getCurrentServerEntry().address;
        } else if (mc.isInSingleplayer()) {
            return "In singleplayer world";
        }
        return "Exploring...";
    }

    private void stopRpc() {
        if (!started) return;
        started = false;

        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }

        try {
            discordRpc.Discord_ClearPresence();
            discordRpc.Discord_Shutdown();
        } catch (Exception ignored) {}
    }

    @SubscribeEvent
    public void onTick(TickEvent event) {
        if (isEnabled() && !started && !getNull()) {
            startRpc();
        }
    }
}