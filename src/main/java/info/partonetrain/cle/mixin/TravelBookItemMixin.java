package info.partonetrain.cle.mixin;

import info.partonetrain.cle.CleClient;
import info.partonetrain.cle.CleConfig;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.millenaire.item.ModItems;
import org.millenaire.item.TravelBookItem;
import org.millenaire.network.InfoPanelRequestPayload;
import org.millenaire.network.ModPayloads;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TravelBookItem.class)
public class TravelBookItemMixin {

    @Inject(method = "use", at=@At("HEAD"), cancellable = true)
    public void cle$use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir){
        if(CleConfig.TRAVEL_BOOK_INFO_PANEL.getAsBoolean()) {
            if (level.isClientSide()) {
                PacketDistributor.sendToServer(new InfoPanelRequestPayload(), new CustomPacketPayload[0]);
            }
            cir.setReturnValue(InteractionResultHolder.success(player.getItemInHand(hand)));
        }
    }
}
