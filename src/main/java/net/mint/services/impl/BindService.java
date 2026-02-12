package net.mint.services.impl;

import net.mint.Managers;
import net.mint.Mint;
import net.mint.services.Service;
import net.mint.events.SubscribeEvent;
import net.mint.events.impl.KeyboardEvent;
import net.mint.events.impl.MouseEvent;
import net.mint.modules.Feature;

public class BindService extends Service {

    public BindService() {
        super("Bind", "Handles key input functions within the client");
        Mint.EVENT_HANDLER.subscribe(this);
    }

    @SubscribeEvent
    public void onKeyboard(KeyboardEvent event) {
        for (Feature feature : Managers.FEATURE.getFeatures()) {
            if (feature.getBind().getValue() == event.getKey()) {
                if (event.isAction()) {
                    switch (feature.getBindMode().getValue()) {
                        case "Toggle":
                            feature.setEnabled(!feature.isEnabled());
                            break;
                        case "Hold":
                            feature.setEnabled(true);
                            break;
                    }
                } else {
                    if (feature.getBindMode().getValue().equals("Hold"))
                        feature.setEnabled(false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onMouse(MouseEvent event) {
        for (Feature feature : Managers.FEATURE.getFeatures()) {
            if (feature.getBind().getValue() == (-event.getButton() - 1)) {
                feature.setEnabled(!feature.isEnabled());
            }
        }
    }
}