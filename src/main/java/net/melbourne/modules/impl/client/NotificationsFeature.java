package net.melbourne.modules.impl.client;

import net.melbourne.Managers;
import net.melbourne.services.Services;
import net.melbourne.events.SubscribeEvent;
import net.melbourne.events.impl.PlayerDeathEvent;
import net.melbourne.events.impl.PlayerPopEvent;
import net.melbourne.events.impl.AddEntityEvent;
import net.melbourne.modules.Category;
import net.melbourne.modules.Feature;
import net.melbourne.modules.FeatureInfo;
import net.melbourne.settings.types.BooleanSetting;
import net.melbourne.settings.types.NumberSetting;
import net.melbourne.utils.miscellaneous.ColorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.awt.*;
import java.net.URL;

@FeatureInfo(name = "Notifications", category = Category.Client)
public class NotificationsFeature extends Feature {

    public final BooleanSetting totemPops = new BooleanSetting("TotemPops", "Show totem pop and death notifications.", true);
    public final BooleanSetting moduleToggles = new BooleanSetting("ModuleToggles", "Show module enable/disable notifications.", true);
    public final BooleanSetting pingSpike = new BooleanSetting("LatencySpike", "Detects sudden latency increases.", true);
    public final NumberSetting spikeThreshold = new NumberSetting("LatencyThreshold", "Ping increase required to trigger.", 100, 1, 500, () -> pingSpike.getValue());

    // хуйню сморозил
    public final BooleanSetting windowsNotifs = new BooleanSetting("WindowsNotifications", "Enable system tray notifications when the game is unfocused.", false);
    public final BooleanSetting visualRange = new BooleanSetting("VisualRange", "Notify when a player enters render distance.", false, () -> windowsNotifs.getValue());
    public final BooleanSetting includeFriends = new BooleanSetting("IncludeFriends", "Also notify for friends entering range.", false, () -> visualRange.getValue() && windowsNotifs.getValue());

    @SubscribeEvent
    public void onPlayerPop(PlayerPopEvent event) {
        if (getNull() || !totemPops.getValue()) return;

        boolean isSelf = event.getPlayer() == mc.player;
        String name = isSelf ? "You" : event.getPlayer().getName().getString();
        String amount = String.valueOf(event.getPops());
        String totems = event.getPops() == 1 ? "totem" : "totems";

        Text nameText = Text.literal(name).styled(s -> s.withColor(ColorUtils.getGlobalColor().getRGB()));
        Text amountText = Text.literal(amount).styled(s -> s.withColor(ColorUtils.getGlobalColor().getRGB()));

        Text msg = Text.literal("")
                .append(nameText)
                .append(Text.literal(" §7has popped §s"))
                .append(amountText)
                .append(Text.literal(" §7" + totems + "§s"))
                .append(Text.literal("§7."));

        Services.CHAT.sendPop(event.getPlayer().getUuid(), msg, true);

        if (isSelf && windowsNotifs.getValue() && !mc.isWindowFocused()) {
            sendWindowsNotification("You have been popped", "Totems in total: " + (event.getPops() - 1), TrayIcon.MessageType.WARNING);
        }
    }

    @SubscribeEvent
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (getNull() || !totemPops.getValue()) return;

        int pops = Services.WORLD.getPoppedTotems().getOrDefault(event.getPlayer().getUuid(), 0);
        if (pops <= 0) return;

        boolean isSelf = event.getPlayer() == mc.player;
        String name = isSelf ? "You" : event.getPlayer().getName().getString();
        String totems = pops == 1 ? "totem" : "totems";

        Text nameText = Text.literal(name).styled(s -> s.withColor(ColorUtils.getGlobalColor().getRGB()));
        Text amountText = Text.literal(String.valueOf(pops)).styled(s -> s.withColor(ColorUtils.getGlobalColor().getRGB()));

        Text msg = Text.literal("")
                .append(nameText)
                .append(Text.literal(" §7has died after popping §s"))
                .append(amountText)
                .append(Text.literal(" §7" + totems + "§s"))
                .append(Text.literal("§7."));

        Services.CHAT.sendPop(event.getPlayer().getUuid(), msg, true);

        if (isSelf && windowsNotifs.getValue() && !mc.isWindowFocused()) {
            sendWindowsNotification("You have died", "After popping " + pops + " " + totems, TrayIcon.MessageType.ERROR);
        }
    }

    @SubscribeEvent
    public void onAddEntity(AddEntityEvent event) {
        if (getNull() || !windowsNotifs.getValue() || !visualRange.getValue()) return;

        Entity entity = event.getEntity();
        if (!(entity instanceof PlayerEntity player) || player == mc.player) return;

        String playerName = player.getName().getString();
        if (playerName == null || playerName.isEmpty()) return;

        if (Managers.FRIEND.isFriend(playerName) && !includeFriends.getValue()) return;

        if (mc.isWindowFocused()) return;

        sendWindowsNotification(playerName + " entered visual range", "", TrayIcon.MessageType.INFO);
    }

    // иди нахуй // todo
    private void sendWindowsNotification(String title, String message, TrayIcon.MessageType type) {
        try {
            if (!SystemTray.isSupported()) return;

            URL iconUrl = getClass().getClassLoader().getResource("assets/melbourne/icon/icon.png");
            if (iconUrl == null) return;

            Image image = Toolkit.getDefaultToolkit().createImage(iconUrl);
            PopupMenu popup = new PopupMenu(); // обязательно!
            TrayIcon trayIcon = new TrayIcon(image, "", popup);
            trayIcon.setImageAutoSize(true);

            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
            trayIcon.displayMessage(title, message, type);
            tray.remove(trayIcon);

        } catch (Exception ignored) {

        }
    }
}