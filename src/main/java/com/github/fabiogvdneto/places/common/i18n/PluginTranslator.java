package com.github.fabiogvdneto.places.common.i18n;

import com.github.fabiogvdneto.places.common.Plugins;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PluginTranslator extends AbstractTranslator {

    private String code;

    public PluginTranslator() {
        super(new HashMap<>());
        this.code = "null";
    }

    @Override
    public String code() {
        return code;
    }

    public void clearTranslations() {
        translations.clear();
    }

    public void loadTranslations(Plugin plugin, String code) {
        if (this.code.equals(code)) return;

        String path = path(code);

        try {
            Configuration config = Plugins.loadConfiguration(plugin, path);

            for (Map.Entry<String, Object> entry : config.getValues(true).entrySet()) {
                if (entry.getValue().getClass() == String.class) {
                    translations.put(entry.getKey(), (String) entry.getValue());
                }
            }

            plugin.getLogger().info("Loaded message translations (" + path + ").");
            this.code = code;
        } catch (IOException e) {
            plugin.getLogger().warning("Error loading message translations (" + path + ").");
        }
    }

    private String path(String code) {
        return "messages" + File.separatorChar + code + ".yml";
    }
}