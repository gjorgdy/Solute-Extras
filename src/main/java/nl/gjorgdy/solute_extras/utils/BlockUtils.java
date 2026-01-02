package nl.gjorgdy.solute_extras.utils;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.rule.GameRules;
import nl.gjorgdy.solute_extras.models.Drops;

public class BlockUtils {

    public static void dropExperience(ServerWorld world, BlockPos pos, int size) {
        if (world.getGameRules().getValue(GameRules.DO_TILE_DROPS)) {
            ExperienceOrbEntity.spawn(world, Vec3d.ofCenter(pos), size);
        }
    }

    public static Drops breakBlockReturnDrop(ServerWorld world, BlockPos pos, PlayerEntity player, ItemStack tool) {
        BlockState _blockState = world.getBlockState(pos);
        Block _block = _blockState.getBlock();
        BlockEntity _blockEntity = world.getBlockEntity(pos);
        boolean hasEntity = _blockEntity != null;
        // get drops
        var items = Block.getDroppedStacks(
                _blockState,
                world,
                pos,
                _blockEntity,
                player,
                tool
        );
        int experience = EnchantmentUtils.getExperienceDrops(world, tool, _block);
        // break the block and handle context
        world.breakBlock(pos, hasEntity, player);
        tool.postMine(world, _blockState, pos, player);
        player.incrementStat(Stats.MINED.getOrCreateStat(_block));
        player.addExhaustion(0.005F);
        PlayerBlockBreakEvents.AFTER.invoker().afterBlockBreak(world, player, pos, _blockState, _blockEntity);
        // return drops
        return hasEntity ? Drops.EMPTY : new Drops(items, experience);
    }

}
