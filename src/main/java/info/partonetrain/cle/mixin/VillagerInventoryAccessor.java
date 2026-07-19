package info.partonetrain.cle.mixin;

import net.minecraft.world.item.Item;
import org.millenaire.entity.VillagerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(VillagerInventory.class)
public interface VillagerInventoryAccessor {
    @Accessor("items")
    Map<Item, Integer> cle$items();
}
