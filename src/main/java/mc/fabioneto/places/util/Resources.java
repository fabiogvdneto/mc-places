package mc.fabioneto.places.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public final class Resources {

    public static FileConfiguration saveAndLoad(Plugin plugin, String name) {
        File usr = new File(plugin.getDataFolder(), name);

        plugin.saveResource(name, false);

        InputStreamReader src = new InputStreamReader(plugin.getResource(name));

        FileConfiguration srcConfig = YamlConfiguration.loadConfiguration(src);
        FileConfiguration usrConfig = YamlConfiguration.loadConfiguration(usr);

        usrConfig.setDefaults(srcConfig);
        usrConfig.options().copyDefaults(true);

        try {
            usrConfig.save(usr);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return usrConfig;
    }

}
