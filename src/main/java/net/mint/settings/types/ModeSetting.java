package net.mint.settings.types;

import lombok.Getter;
import lombok.Setter;
import net.mint.Mint;
import net.mint.events.impl.SettingChangeEvent;
import net.mint.interfaces.Nameable;
import net.mint.settings.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@Getter
@Setter
public class ModeSetting extends Setting implements Nameable {
    private String value;
    private final String defaultValue;
    private List<String> modes;

    public ModeSetting(String name, String description, String value, String[] modes) {
        super(name, description, true);
        this.value = value;
        this.defaultValue = value;
        this.modes = new ArrayList<>(Arrays.asList(modes));
    }

    public ModeSetting(String name, String description, String value, String[] modes, boolean visibility) {
        super(name, description, visibility);
        this.value = value;
        this.defaultValue = value;
        this.modes = new ArrayList<>(Arrays.asList(modes));
    }

    public ModeSetting(String name, String description, String value, String[] modes, Supplier<Boolean> visibility) {
        super(name, description, visibility);
        this.value = value;
        this.defaultValue = value;
        this.modes = new ArrayList<>(Arrays.asList(modes));
    }

    public boolean equalsValue(String value) {
        return this.value.equalsIgnoreCase(value);
    }

    public void setValue(String value) {
        this.value = value;
        Mint.EVENT_HANDLER.post(new SettingChangeEvent(this));
    }

    public void resetValue() {
        this.value = defaultValue;
        Mint.EVENT_HANDLER.post(new SettingChangeEvent(this));
    }

    public void setModes(String[] newModes) {
        this.modes = new ArrayList<>(Arrays.asList(newModes));
        if (!this.modes.contains(value) && !this.modes.isEmpty()) {
            this.value = this.modes.get(0);
        }
    }

    @Override
    public String getName() {
        return name;
    }
}