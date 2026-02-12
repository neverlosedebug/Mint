package net.mint.modules.impl.misc;

import net.mint.Mint;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.PlayerUpdateEvent;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.ModeSetting;
import net.mint.settings.types.NumberSetting;
import net.mint.settings.types.TextSetting;

@FeatureInfo(name = "Robotics", category = Category.Misc)
public class RoboticsFeature extends Feature {
    public ModeSetting role = new ModeSetting("Role", "Role", "Host", new String[]{"Host", "Server"});
    public TextSetting hostIp = new TextSetting("HostIP", "Host IP", "127.0.0.1", () -> role.getValue().equals("Server"));
    public NumberSetting port = new NumberSetting("Port", "Port", 4444, 1024, 65535);

    @Override
    public String getInfo() {
        return role.getValue().equals("Host") ? "Host (local only)" : "Server (disabled)";
    }

    @Override
    public void onEnable() {

        Mint.getLogger().info("Robotics: Network functionality disabled for security");
    }

    @Override
    public void onDisable() {

    }

    public void syncToggle(Feature feature, boolean state) {

    }

    @SubscribeEvent
    public void onUpdate(PlayerUpdateEvent event) {

    }

    @SubscribeEvent
    public void onTravel(net.mint.events.impl.PlayerTravelEvent event) {

    }
}