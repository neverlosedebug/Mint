package net.mint.modules.impl.client;

import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.settings.types.ModeSetting;
import net.mint.Managers;
import net.minecraft.util.Identifier;

@FeatureInfo(name = "Capes", category = Category.Client)
public final class CapesFeature extends Feature {

    private static final Identifier MINT_CAPE = Identifier.of("mint", "textures/mint.png");
    private static final Identifier PK_CAPE = Identifier.of("mint", "textures/pk.png");
    private static final Identifier EMP_CAPE = Identifier.of("mint", "textures/emp.png");

    private static final Identifier DOT5_CAPE = Identifier.of("mint", "textures/dot5.png");
    private static final Identifier SPOTIFY_CAPE = Identifier.of("mint", "textures/spotify.png");
    private static final Identifier BUTTERFLY_CAPE = Identifier.of("mint", "textures/butterfly.png");
    private static final Identifier HIGHLAND_CAPE = Identifier.of("mint", "textures/highland.png");
    private static final Identifier HLDARK_CAPE = Identifier.of("mint", "textures/hldark.png");

    public final ModeSetting mode = new ModeSetting("Cape", "Choose which cape to display when Capes is enabled.", "Mint", new String[]{"Mint", "Peacekeepers", "Emperium", "Dot5", "Spotify", "Butterfly", "Highland", "HlDark"}) {
        @Override
        public void setValue(String value) {
            super.setValue(value);

            if (isEnabled())
                Managers.BOT.sendMessageToIrc(String.format("[CAPE] UUID: %s, NAME: %s, MODE: %s", mc.player.getUuid(), mc.player.getName().getString(), mode.getValue()));
        }
    };

    @Override
    public void onEnable() {
        if (mc.player == null || Managers.BOT == null)
            return;

        Managers.BOT.sendMessageToIrc(String.format("[CAPE] UUID: %s, NAME: %s, MODE: %s", mc.player.getUuid(), mc.player.getName().getString(), mode.getValue()));
    }

    @Override
    public void onDisable() {
        if (mc.player == null || Managers.BOT == null)
            return;

        Managers.BOT.sendMessageToIrc(String.format("[CAPE] UUID: %s, NAME: %s, MODE: NONE", mc.player.getUuid(), mc.player.getName().getString()));
    }

    public Identifier getCapeTexture() {
        if (!isEnabled()) return null;
        return switch (mode.getValue()) {
            case "Mint" -> MINT_CAPE;
            case "Peacekeepers" -> PK_CAPE;
            case "Emperium" -> EMP_CAPE;
            case "Dot5" -> DOT5_CAPE;
            case "Spotify" -> SPOTIFY_CAPE;
            case "Butterfly" -> BUTTERFLY_CAPE;
            case "Highland" -> HIGHLAND_CAPE;
            case "HlDark" -> HLDARK_CAPE;
            default          -> MINT_CAPE;
        };
    }

    public static Identifier getCapeFor(String mode) {
        return switch (mode) {
            case "Mint" -> MINT_CAPE;
            case "Peacekeepers" -> PK_CAPE;
            case "Emperium" -> EMP_CAPE;
            case "Dot5" -> DOT5_CAPE;
            case "Spotify" -> SPOTIFY_CAPE;
            case "Butterfly" -> BUTTERFLY_CAPE;
            case "Highland" -> HIGHLAND_CAPE;
            case "HlDark" -> HLDARK_CAPE;
            default          -> MINT_CAPE;
        };
    }
}