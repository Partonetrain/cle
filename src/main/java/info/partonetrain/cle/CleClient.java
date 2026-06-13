package info.partonetrain.cle;

import info.partonetrain.cle.client.InuitTridentRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

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
}
