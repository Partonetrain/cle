package info.partonetrain.cle.mixin;

import org.millenaire.building.BlockCostRegistry;
import org.millenaire.tool.ToolCategory;
import org.millenaire.tool.ToolCategoryRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(BlockCostRegistry.class)
public interface BlockCostRegistryAccessor {

    @Accessor("COSTS")
    static Map<String, BlockCostRegistry.BlockCost> cle$getCosts() {
        throw new AssertionError();
    }

}