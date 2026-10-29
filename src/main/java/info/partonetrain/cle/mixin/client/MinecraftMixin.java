package info.partonetrain.cle.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import info.partonetrain.cle.client.QuestHighlighterManager;
import net.minecraft.client.Minecraft;
import org.millenaire.entity.MillVillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.world.entity.Entity;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @ModifyReturnValue(method = "shouldEntityAppearGlowing(Lnet/minecraft/world/entity/Entity;)Z", at = @At(value = "RETURN"))
    private boolean cle$shouldEntityAppearGlowing(boolean original, Entity entity) {
        if(original == false && entity instanceof MillVillager millVillager){
            return QuestHighlighterManager.isMillagerQuestObjective(millVillager);
        }
        return original;
    }
}
