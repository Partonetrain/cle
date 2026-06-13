package info.partonetrain.cle.entity;

import info.partonetrain.cle.Cle;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ThrownInuitTridentEntity extends ThrownTrident {

    public ThrownInuitTridentEntity(EntityType<? extends ThrownTrident> entityType, Level level) {
        super(entityType, level);
    }

    public ThrownInuitTridentEntity(Level level, LivingEntity shooter, ItemStack stack) {
        super(level, shooter, stack);
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return Cle.THROWN_INUIT_TRIDENT_ENTITY_TYPE.get();
    }

}
