package me.fabiogvdneto.places.common.i18n;

import me.fabiogvdneto.places.common.Plugins;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class PluginTranslator extends AbstractTranslator {

    private String code;

    public PluginTranslator() {
        super(new HashMap<>());
    }

    @Override
    public String code() {
        return code;
    }

    public void clearTranslations() {
        translations.clear();
    }

    public void loadTranslations(Plugin plugin, String code, String fallback) {
        if (fallback != null) {
            loadTranslations(plugin, fallback);
        }

        if (!code.equals(fallback)) {
            loadTranslations(plugin, code);
        }
    }

    public void loadTranslations(Plugin plugin, String code) {
        String path = pathToFile(code);
        plugin.getLogger().info("Loading message translations (" + path + ")...");
        Configuration config = Plugins.loadConfiguration(plugin, path);

        for (Map.Entry<String, Object> entry : config.getValues(true).entrySet()) {
            Object value = entry.getValue();

            if (value.getClass() == String.class) {
                translations.put(entry.getKey(), (String) value);
            }
        }

        this.code = code;
    }

    private String pathToFile(String code) {
        return "messages" + File.separatorChar + code + ".yml";
    }
}