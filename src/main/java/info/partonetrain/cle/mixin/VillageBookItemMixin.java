package info.partonetrain.cle.mixin;

import net.minecraft.world.item.Item;
import org.millenaire.item.VillageBookItem;
import org.spongepowered.asm.mixin.Mixin;

import javax.annotation.Nullable;

@Mixin(VillageBookItem.class)
public class VillageBookItemMixin extends Item {
    public VillageBookItemMixin(Properties properties) {
        super(properties);
    }
}
