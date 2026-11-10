package info.partonetrain.cle;

import info.partonetrain.cle.client.InuitTridentRenderer;
import info.partonetrain.cle.client.QuestHighlighterManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.millenaire.village.VillageId;

@Mod(value = Cle.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Cle.MODID, value = Dist.CLIENT)
public class CleClient {
    public CleClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
//        Cle.LOGGER.info("HELLO FROM CLIENT SETUP");
//        Cle.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Cle.THROWN_INUIT_TRIDENT_ENTITY_TYPE.get(), InuitTridentRenderer::new);
    }

    @SubscribeEvent
    public static void modelRegistry(ModelEvent.RegisterAdditional event) {
        event.register(ModelResourceLocation.standalone(Cle.ALTERNATIVE_INUIT_TRIDENT.getId().withPrefix("item/")));
        event.register(ModelResourceLocation.standalone(Cle.ALTERNATIVE_INUIT_TRIDENT.getId().withPrefix("item/").withSuffix("_in_hand")));
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        if (stack.is(Items.HOPPER)) {
            if (stack.get(Cle.CONTROLLED_HOPPER_DATA_COMPONENT_TYPE.get()) != null){
                ControlledHopper ch = stack.get(Cle.CONTROLLED_HOPPER_DATA_COMPONENT_TYPE);

                if(event.getFlags().isAdvanced()){
                    event.getToolTip().add(Component.literal(ch.villageUUID().toString()));
                }
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        QuestHighlighterManager.tickCooldown();
    }
}
