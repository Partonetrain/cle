package info.partonetrain.cle.mixin;

import info.partonetrain.cle.ParsedConfigs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.millenaire.world.StructureAvoidance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(StructureAvoidance.class)
public class StructureAvoidanceMixin {

    @Shadow
    @Final
    @Mutable
    private static List<ResourceKey<Structure>> AVOIDED_STRUCTURE_KEYS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void cle$clInit(CallbackInfo ci) {
        List<ResourceKey<Structure>> keys = new ArrayList<>(AVOIDED_STRUCTURE_KEYS);
        keys.addAll(ParsedConfigs.structuresToAvoid);
        AVOIDED_STRUCTURE_KEYS = List.copyOf(keys);
    }

}
