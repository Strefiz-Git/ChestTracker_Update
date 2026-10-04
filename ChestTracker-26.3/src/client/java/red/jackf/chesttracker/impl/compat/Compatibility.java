package red.jackf.chesttracker.impl.compat;

import net.fabricmc.loader.api.FabricLoader;

public interface Compatibility {
    boolean SEARCHABLES = FabricLoader.getInstance().isModLoaded("searchables");

    /**
     * The WTHIT integration is compiled out when no {@code wthit_version} is set in gradle.properties, as there is
     * currently no build of wthit-api for this Minecraft version.
     */
    boolean WTHIT = FabricLoader.getInstance().isModLoaded("wthit") && WthitIntegration.isPresent();

    class WthitIntegration {
        private static final String PLUGIN = "red.jackf.chesttracker.impl.compat.mods.wthit.ChestTrackerWTHITPlugin";

        static boolean isPresent() {
            try {
                Class.forName(PLUGIN, false, Compatibility.class.getClassLoader());
                return true;
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
    }
}