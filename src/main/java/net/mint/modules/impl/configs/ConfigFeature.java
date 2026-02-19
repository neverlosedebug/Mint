package net.mint.modules.impl.configs;

import net.mint.Managers;
import net.mint.modules.Category;
import net.mint.modules.Feature;
import net.mint.modules.FeatureInfo;
import net.mint.services.Services;
import net.mint.settings.types.ModeSetting;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@FeatureInfo(name = "ConfigSelector", category = Category.Configs)
public class ConfigFeature extends Feature {

    public ModeSetting configSelector = new ModeSetting(
            "Active Config",
            "Choose a configuration to load",
            Managers.CONFIG.getCurrentConfig(),
            getConfigNames()
    );

    public void init() {
        updateConfigList();
    }

    private String[] getConfigNames() {
        File folder = new File(Managers.CONFIG.getConfigs());
        if (!folder.exists() || !folder.isDirectory()) {
            return new String[]{"default"};
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            return new String[]{"default"};
        }

        List<String> names = new ArrayList<>();
        for (File file : files) {
            String name = file.getName().replace(".json", "");
            names.add(name);
        }
        return names.toArray(new String[0]);
    }

    public void updateConfigList() {
        String current = configSelector.getValue();
        String[] configs = getConfigNames();
        configSelector.setModes(configs);
        if (!Arrays.asList(configs).contains(current) && configs.length > 0) {
            configSelector.setValue(configs[0]);
        }
    }

    @Override
    public void onEnable() {
        String selected = configSelector.getValue();
        if (!selected.equals(Managers.CONFIG.getCurrentConfig())) {
            Managers.CONFIG.loadConfig(selected);
            Services.CHAT.sendRaw("§aLoaded config: §7" + selected);
        }
        setEnabled(false);
    }

    @Override
    public void onDisable() {
    }

    public static void refreshConfigList() {
        Feature feature = Managers.FEATURE.getFeatureFromClass(ConfigFeature.class);
        if (feature instanceof ConfigFeature cf) {
            cf.updateConfigList();
        }
    }
}