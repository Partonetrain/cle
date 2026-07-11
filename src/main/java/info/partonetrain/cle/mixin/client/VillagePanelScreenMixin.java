package info.partonetrain.cle.mixin.client;

import info.partonetrain.cle.CleConfig;
import info.partonetrain.cle.ParsedConfigs;
import net.minecraft.network.chat.Component;
import org.millenaire.client.gui.VillagePanelScreen;
import org.millenaire.network.PanelContentPayload;
import org.millenaire.village.panel.PanelContent;
import org.millenaire.village.panel.PanelLine;
import org.millenaire.village.panel.PanelType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(VillagePanelScreen.class)
public class VillagePanelScreenMixin {

    @Mutable
    @Final
    @Shadow
    private final PanelContent content;

    public VillagePanelScreenMixin(PanelContent content) {
        this.content = content;
    }

    @Inject(method = "<init>(Lorg/millenaire/village/panel/PanelContent;Lorg/millenaire/network/PanelContentPayload;)V", at=@At("RETURN"))
    private void cle$init(CallbackInfo ci){
        if(content.type() == PanelType.CONSTRUCTIONS && !CleConfig.SORT_CONSTRUCTIONS_PANEL.get().isBlank()){

            if(ParsedConfigs.lowPriorityBuildings.isEmpty()){
                ParsedConfigs.parseLowPriorityBuildings();
            }

            List<PanelLine> linesToRemove = new ArrayList<>();
            for(PanelLine pl : content.lines()){
                if(pl.navTarget() != null){
                    String key = pl.navTarget().itemKey();
                    for(String low : ParsedConfigs.lowPriorityBuildings){
                        if(key.contains(low)){
                            linesToRemove.add(pl);
                        }
                    }
                }
            }

            for(PanelLine line : linesToRemove){
                content.lines().remove(line);
            }

            content.lines().addAll(linesToRemove);
        }
    }
}
