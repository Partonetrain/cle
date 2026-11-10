package info.partonetrain.cle.mixin.client;

import info.partonetrain.cle.CleConfig;
import net.minecraft.ChatFormatting;
import org.millenaire.client.gui.LockedChestScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LockedChestScreen.class)
public class LockedChestScreenMixin {

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"), index = 4)
    public int cle$render(int color) {
        if(CleConfig.READABLE_SCREENS.get()){
            if(color == -43691){ //ARGB #FFFF5555
                int dred = ChatFormatting.DARK_RED.getColor();
                dred = (dred & 0x00FFFFFF) | 0xFF000000; //add alpha
                return dred;
            }
            else if(color == -11141291){ //ARGB #FF55FF55
                int dgreen = ChatFormatting.DARK_GREEN.getColor();
                dgreen = (dgreen & 0x00FFFFFF) | 0xFF000000; //add alpha
                return dgreen;
            }
        }
        return color;
    }
}
