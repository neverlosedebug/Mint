package net.mint.script.impl;

import lombok.Getter;
import net.mint.Manager;
import net.mint.Managers;
import net.mint.Mint;
import net.mint.script.LuaApiRegistry;
import net.mint.script.LuaFunctionProvider;
import net.mint.script.annotations.RegisterLua;
import net.mint.utils.miscellaneous.FileUtils;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Getter
public class ScriptManager extends Manager {

    private final Map<String, ScriptModule> loadedScripts = new HashMap<>();
    private final List<ScriptModule> scripts = new ArrayList<>();
    private final File scriptFolder = new File(Mint.NAME + "/scripts");

    public ScriptManager() {
        super("Scripts", "Manages Lua scripting");
    }

    @Override
    public void onInit() {
        try {
            FileUtils.createDirectory(Mint.NAME);
            FileUtils.createDirectory(scriptFolder.getAbsolutePath());
        } catch (IOException e) {
            Mint.getLogger().error("Failed to create script directories", e);
        }

        registerApis();
        refreshScripts();
    }

    public void refreshScripts() {
        for (ScriptModule script : scripts) {
            Managers.FEATURE.getFeatures().remove(script);
        }

        scripts.clear();

        File[] files = scriptFolder.listFiles((dir, name) -> name.endsWith(".lua"));
        if (files == null) return;

        for (File file : files) {
            ScriptModule module = new ScriptModule(file);
            scripts.add(module);
            Managers.FEATURE.getFeatures().add(module);
        }
    }

    private void registerApis() {
        try {
            Reflections reflections = new Reflections("net.mint.script.functions");
            Set<Class<? extends LuaFunctionProvider>> classes = reflections.getSubTypesOf(LuaFunctionProvider.class);

            for (Class<? extends LuaFunctionProvider> clazz : classes) {
                if (clazz.getAnnotation(RegisterLua.class) == null) continue;
                LuaApiRegistry.register(clazz.getDeclaredConstructor().newInstance());
            }
        } catch (Exception e) {
            Mint.getLogger().error("Failed to register Lua APIs", e);
        }
    }
}