package info.partonetrain.cle.compat;

import info.partonetrain.cle.Cle;
import mezz.jei.api.IModPlugin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

//disabled for now as I am not smart enough to figure out how to do this for millager gathering (not an actual recipetype)
public class JeiCompat {

    //@JeiPlugin
    public class CleJeiPlugin implements IModPlugin {


        @Override
        public @NotNull ResourceLocation getPluginUid() {
            return ResourceLocation.fromNamespaceAndPath(Cle.MODID, "jei_plugin");
        }
    }

}
