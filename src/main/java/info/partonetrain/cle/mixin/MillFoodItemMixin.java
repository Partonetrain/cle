package info.partonetrain.cle.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import info.partonetrain.cle.CleConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.extensions.IItemStackExtension;
import org.millenaire.item.MillFoodItem;
import org.millenaire.item.ModItems;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MillFoodItem.class)
public class MillFoodItemMixin extends Item {
    @Shadow @Final private float saturationModifier;

    public MillFoodItemMixin(Properties properties) {
        super(properties);
    }


    @Inject(method = "finishUsingItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", ordinal = 0))
    public void cle$finishUsingItem_Drink(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir){
        MillFoodItem self = (MillFoodItem) (Object) this;
        //if(self.drink){ //this is private
        if(self.getUseAnimation(stack) == UseAnim.DRINK){
            if(CleConfig.DRINK_GIVES_BOTTLE.getAsBoolean()){
                Player player = (Player)entity;
                player.addItem(Items.GLASS_BOTTLE.getDefaultInstance());
            }
        }
    }

    @Inject(method = "finishUsingItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V", ordinal = 1))
    public void cle$finishUsingItem_Yogurt(ItemStack stack, Level level, LivingEntity entity, CallbackInfoReturnable<ItemStack> cir){
        MillFoodItem self = (MillFoodItem) (Object) this;
        if(self == ModItems.YOGURT.value()){
            if(CleConfig.YOGURT_GIVES_BOWL.getAsBoolean()){
                Player player = (Player)entity;
                player.addItem(Items.BOWL.getDefaultInstance());
            }
        }
    }

    @ModifyArg(method = "<init>(Lnet/minecraft/world/item/Item$Properties;IFIIIIZZLjava/util/List;)V", at= @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
    private static Properties cle$MillFoodItem_New(Properties properties, @Local(argsOnly = true, ordinal = 0) int nutrition, @Local(argsOnly = true, ordinal = 0) float saturationModifier){
        if(CleConfig.USE_FOOD_COMPONENTS.getAsBoolean()){
            FoodProperties fp = (new FoodProperties.Builder()).nutrition(nutrition).saturationModifier(saturationModifier).build();
            properties.food(fp);
        }

        return properties;
    }


}
