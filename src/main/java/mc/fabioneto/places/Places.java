package mc.fabioneto.places;

import com.google.common.base.Preconditions;

public final class Places {

    private static PlacesPlugin plugin;

    public static PlacesPlugin getPlugin() {
        return plugin;
    }

    static void setPlugin(PlacesPlugin plugin) {
        Preconditions.checkArgument(Places.plugin == null);

        Places.plugin = plugin;
    }
}
