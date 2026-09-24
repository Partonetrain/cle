package info.partonetrain.cle.mixin;

import org.millenaire.net.VersionChecker;
import org.millenaire.tool.ToolCategory;
import org.millenaire.tool.ToolCategoryRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ToolCategoryRegistry.class)
public interface ToolCategoryRegistryAccessor {

    @Accessor("CATEGORIES")
    static Map<String, ToolCategory> cle$getCategories() {
        throw new AssertionError();
    }

}