package info.partonetrain.cle.compat;

import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import info.partonetrain.cle.CleUtils;
import info.partonetrain.cle.ParsedConfigs;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.millenaire.entity.MillVillager;
import org.millenaire.village.Village;

import java.util.*;

public class ArsNouveauCompat {

    public static int parsed = 0;

    public static final ResourceLocation AMPLIFY = ResourceLocation.fromNamespaceAndPath("ars_nouveau","glyph_amplify");

    public record FoundSpellEffect(ResourceLocation registryName, int location){}

    @SubscribeEvent
    public void OnServerStart(ServerStartedEvent event){
        parsed = ParsedConfigs.parseSpells();
    }

    @SubscribeEvent
    public void onEffectResolve(EffectResolveEvent.Post event){
        if(event.shooter instanceof ServerPlayer sp){
            if(event.rayTraceResult instanceof EntityHitResult ehr && ehr.getEntity() instanceof MillVillager mv){
                int finalRepChange = 0;
                if(mv.getVillageId() == null){
                    return;
                }

                Spell spell = event.spell;

                //find glpyhs that match the config.
                List<FoundSpellEffect> foundSpellEffects = new ArrayList<>();

                for (int i = 0; i < spell.size(); i++) { //would use foreach loop on spell.recipe(); but we need an index for augs
                    AbstractSpellPart part = spell.get(i);
                    for(Map.Entry<ResourceLocation, Integer> entry: ParsedConfigs.spellEffectReputations.entrySet()){
                        if(part.getRegistryName().equals(entry.getKey())){
                            foundSpellEffects.add(new FoundSpellEffect(part.getRegistryName(), i));
                        }
                    }
                }

                for(FoundSpellEffect fse : foundSpellEffects){
                    int amplifies = 1;
                    List<AbstractAugment> augs = event.spell.getAugments(fse.location, event.shooter);
                    for(AbstractAugment aug : augs){
                        if(aug.getRegistryName().equals(AMPLIFY)){
                            amplifies++;
                        }
                    }

                    int repChangeForThisEffect = ParsedConfigs.spellEffectReputations.get(fse.registryName);
                    finalRepChange = repChangeForThisEffect * amplifies;
                }
                repChangeFromSpell(sp.getUUID(), mv, finalRepChange);
            }

        }
    }

    public static void repChangeFromSpell(UUID playerUUID, MillVillager millVillager, int change){
        ServerLevel sl = (ServerLevel) millVillager.level();
        Village village = Village.resolve(sl, millVillager.getVillageId());
        if(village != null) {
            int oldRep = village.getReputation().get(playerUUID);
            int newRep = village.adjustReputation(sl, playerUUID, change);
            CleUtils.printAdjustedReputation(oldRep, change, newRep, "repChangeFromSpell");
        }

        if(change > 0){
            CleUtils.addParticlesAroundEntity(ParticleTypes.HAPPY_VILLAGER, millVillager, (ServerLevel) millVillager.level());
        }
        else{
            CleUtils.addParticlesAroundEntity(ParticleTypes.ANGRY_VILLAGER, millVillager, (ServerLevel) millVillager.level());
        }
    }
}
