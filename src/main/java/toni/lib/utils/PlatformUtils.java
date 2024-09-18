package toni.lib.utils;

#if FABRIC
import net.fabricmc.loader.api.FabricLoader;
#elif FORGE
import net.minecraftforge.fml.ModList;
#else
import net.neoforged.fml.ModList;
#endif

public class PlatformUtils {

    public static boolean isModLoaded(String modid) {
        #if FABRIC
        return FabricLoader.getInstance().isModLoaded(modid);
        #else
        return ModList.get().isLoaded(modid);
        #endif
    }

    public static String getModName(String modId) {
        #if FORGELIKE
        return ModList.get().getModContainerById(modId).map(container -> container.getModInfo().getDisplayName()).orElse(modId);
        #else
        return FabricLoader.getInstance().getModContainer(modId).map(container -> container.getMetadata().getName()).orElse(modId);
        #endif
    }

    public static boolean isFabric() {
        #if FABRIC
        return true;
        #else
        return false;
        #endif
    }

    public static boolean isForge() {
        #if FORGE
        return true;
        #else
        return false;
        #endif
    }

    public static boolean isNeo() {
        #if NEO
        return true;
        #else
        return false;
        #endif
    }

    public static boolean isForgelike() {
        #if FORGELIKE
        return true;
        #else
        return false;
        #endif
    }
}
