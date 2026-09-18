package info.partonetrain.cle.mixin.client;

import info.partonetrain.cle.CleConfig;
import net.minecraft.ChatFormatting;
import org.millenaire.client.gui.QuestScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(QuestScreen.class)
public class QuestScreenMixin {

    //these should be using text components instead...
    @ModifyArg(method = "buildContentLines", at= @At(value = "INVOKE", target = "Lorg/millenaire/client/gui/QuestScreen;addSplitLines(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V", ordinal = 1), index = 2)
    public String cle$buildContentLines1(String original){
        if(CleConfig.READABLE_SCREENS.getAsBoolean()){
            //"§c" = RED -> "§4" = DARK_RED
            return "§" + ChatFormatting.DARK_RED.getChar();
        }
        return original;
    }

    @ModifyArg(method = "buildContentLines", at= @At(value = "INVOKE", target = "Lorg/millenaire/client/gui/QuestScreen;addSplitLines(Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V", ordinal = 2), index = 2)
    public String cle$buildContentLines2(String original){
        if(CleConfig.READABLE_SCREENS.getAsBoolean()){
            if(original.equals("§a")){
                return "§" + ChatFormatting.DARK_GREEN.getChar(); //"§2" = DARK_GREEN
            }
            else if(original.equals("§c")){
                return "§" + ChatFormatting.DARK_RED.getChar(); //"§4" = DARK_RED
            }
        }
        return original;
    }
}
