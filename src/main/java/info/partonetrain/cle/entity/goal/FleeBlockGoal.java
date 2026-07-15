package info.partonetrain.cle.entity.goal;

import info.partonetrain.cle.CleUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import org.millenaire.entity.MillVillager;
import org.millenaire.entity.VillagerNavDriver;
import org.millenaire.goal.NavigationHelperUtils;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

public class FleeBlockGoal<T extends LivingEntity> extends Goal {

    protected TagKey<Block> blocksToAvoid;
    private final Predicate<BlockState> AVOID_PREDICATE;

    private final PathfinderMob owner;
    private final int distanceToCheck;

    protected Path path;
    protected final PathNavigation pathNav;
    protected BlockPos posToAvoid;

    public FleeBlockGoal(PathfinderMob mob, TagKey<Block> blocksToAvoid, int distanceToCheck) {
        this.owner = mob;
        this.blocksToAvoid = blocksToAvoid;
        this.AVOID_PREDICATE = s -> s.is(blocksToAvoid);
        this.pathNav = owner.getNavigation();
        this.distanceToCheck = distanceToCheck;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canContinueToUse() {
        return !pathNav.isDone();
    }

    public void start() {
        pathNav.moveTo(path, 1);
    }

    public void stop() {
        owner.setSprinting(false);
        posToAvoid = null;
        spawnDebugParticle(ParticleTypes.HAPPY_VILLAGER, owner.blockPosition());
    }

    public void tick() {
        if(posToAvoid == null){ //shouldnt be necessary but you never know ig
            return;
        }

        if (owner.distanceToSqr(posToAvoid.getX(), posToAvoid.getY(), posToAvoid.getZ()) < 49.0) {
            owner.setSprinting(true);
        }
        else{
            owner.setSprinting(false);
        }
        spawnDebugParticle(ParticleTypes.CRIT, owner.blockPosition());
        if(path != null){
            spawnDebugParticle(ParticleTypes.SONIC_BOOM, path.getTarget());
        }
    }

    @Override
    public boolean canUse() {
        BlockPos found = findNearestBlock(AVOID_PREDICATE, distanceToCheck);
        if(found == null) {
            return false;
        }
        else {
            posToAvoid = found;

            if(owner instanceof MillVillager mv){
                BlockPos randomSafePos = NavigationHelperUtils.findRandomSafePos(owner.level(), owner.getOnPos(), (b) -> b.closerThan(posToAvoid, 3));
                if(randomSafePos == null){
                        return false;
                }
                VillagerNavDriver nav = mv.getNavManager();
                nav.navigateTo(mv, randomSafePos, 0.65);

                BlockState avoidingState = owner.level().getBlockState(posToAvoid);
                LinkedHashMap<String, String> langKeyWithPlaceholders = new LinkedHashMap<>();
                langKeyWithPlaceholders.put("cle.millager_afraid_of_block.1", mv.getFirstName() + " " + mv.getFamilyName());
                langKeyWithPlaceholders.put(avoidingState.getBlock().getDescriptionId(), null);
                langKeyWithPlaceholders.put("cle.millager_afraid_of_block.2", null);
                CleUtils.changeNearbyPlayerReputation((ServerLevel) owner.level(), mv, -1, langKeyWithPlaceholders);
                return true;
            }
            else {
                Vec3 target = DefaultRandomPos.getPosAway(owner, 16, 7, Vec3.atCenterOf(posToAvoid));
                if (target == null) {
                    posToAvoid = null;
                    return false;
                } else {
                    path = pathNav.createPath(target.x, target.y, target.z, 0);
                    return path != null;
                }
            }
        }
    }

    //derived from vanillacopy of BeePollinateGoal::findNearestBlock
    private BlockPos findNearestBlock(Predicate<BlockState> predicate, int distance) {
        BlockPos blockpos = owner.blockPosition();
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for(int i = 0; i <= distance; i = i > 0 ? -i : 1 - i) {
            for(int j = 0; j < distance; ++j) {
                for(int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                    for(int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                        blockpos$mutableblockpos.setWithOffset(blockpos, k, i - 1, l);
                        if (blockpos.closerThan(blockpos$mutableblockpos, distance) && predicate.test(owner.level().getBlockState(blockpos$mutableblockpos))) {
                            spawnParticle(ParticleTypes.ANGRY_VILLAGER, blockpos$mutableblockpos);
                            CleUtils.addParticlesAroundEntity(ParticleTypes.ANGRY_VILLAGER, owner, (ServerLevel) owner.level());
                            return blockpos$mutableblockpos;
                        }
                    }
                }
            }
        }

        return null;
    }

    private void spawnDebugParticle(SimpleParticleType type, BlockPos pos){
        if(false && owner.level() instanceof ServerLevel sl){
            sl.sendParticles(type, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
        }
    }

    private void spawnParticle(SimpleParticleType type, BlockPos pos){
        if(owner.level() instanceof ServerLevel sl){
            sl.sendParticles(type, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
        }
    }

}
