package info.partonetrain.cle.mixin.client;

import info.partonetrain.cle.client.QuestHighlighterManager;
import org.millenaire.client.ClientQuestCache;
import org.millenaire.network.QuestInstanceSyncPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientQuestCache.class)
public class ClientQuestCacheMixin {

    @Inject(method = "update", at=@At("RETURN"))
    private static void cle$update(QuestInstanceSyncPayload payload, CallbackInfo ci){
        QuestHighlighterManager.reevaluateQuestObjectices();
    }


}
