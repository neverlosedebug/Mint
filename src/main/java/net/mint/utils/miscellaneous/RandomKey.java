package net.mint.utils.miscellaneous;

import net.mint.Mint;
import net.mint.services.Services;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;
import java.security.Security;

public final class RandomKey {

    static {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.insertProviderAt(new BouncyCastleProvider(), 1);
        }
    }

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public static byte[] generate(int lengthInBytes) {
        byte[] key = new byte[lengthInBytes];
        SECURE_RANDOM.nextBytes(key);
        return key;
    }

    public static void generateAndSendToChat(int lengthInBytes) {
        try {
            byte[] key = generate(lengthInBytes);
            String hex = Hex.toHexString(key).toUpperCase();
            String b64 = Base64.toBase64String(key);

            Services.CHAT.sendRaw("§a[Mint] §fGenerated §b" + lengthInBytes + "§f-byte key:");
            Services.CHAT.sendRaw("§7HEX: §f" + hex);
            Services.CHAT.sendRaw("§7B64: §f" + b64);

        } catch (Exception e) {
            Mint.getLogger().error("[{}] Failed to generate random key:", Mint.NAME, e);
            Services.CHAT.sendRaw("§c[Mint] §fFailed to generate key. Check logs.");
        }
    }
}