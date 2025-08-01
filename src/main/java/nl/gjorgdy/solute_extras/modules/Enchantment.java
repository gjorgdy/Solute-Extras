package nl.gjorgdy.solute_extras.modules;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import nl.gjorgdy.solute_extras.models.Drops;
import nl.gjorgdy.solute_extras.utils.BlockUtils;
import nl.gjorgdy.solute_extras.utils.ToolHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static nl.gjorgdy.solute_extras.utils.BlockUtils.breakBlockReturnDrop;

public class Enchantment {

    public static Optional<ItemStack> crush(Random random, Block block, int fortune) {
        if (block == Blocks.BLACK_CONCRETE) {
            return Optional.ofNullable(Items.BLACK_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.LIGHT_BLUE_CONCRETE) {
            return Optional.ofNullable(Items.LIGHT_BLUE_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.BLUE_CONCRETE) {
            return Optional.ofNullable(Items.BLUE_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.CYAN_CONCRETE) {
            return Optional.ofNullable(Items.CYAN_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.LIME_CONCRETE) {
            return Optional.ofNullable(Items.LIME_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.GREEN_CONCRETE) {
            return Optional.ofNullable(Items.GREEN_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.YELLOW_CONCRETE) {
            return Optional.ofNullable(Items.YELLOW_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.ORANGE_CONCRETE) {
            return Optional.ofNullable(Items.ORANGE_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.BROWN_CONCRETE) {
            return Optional.ofNullable(Items.BROWN_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.RED_CONCRETE) {
            return Optional.ofNullable(Items.RED_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.PURPLE_CONCRETE) {
            return Optional.ofNullable(Items.PURPLE_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.MAGENTA_CONCRETE) {
            return Optional.ofNullable(Items.MAGENTA_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.PINK_CONCRETE) {
            return Optional.ofNullable(Items.PINK_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.WHITE_CONCRETE) {
            return Optional.ofNullable(Items.WHITE_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.LIGHT_GRAY_CONCRETE) {
            return Optional.ofNullable(Items.LIGHT_GRAY_CONCRETE_POWDER.getDefaultStack());
        }
        if (block == Blocks.GRAY_CONCRETE) {
            return Optional.ofNullable(Items.GRAY_CONCRETE_POWDER.getDefaultStack());
        }
        // convert
        if (block == Blocks.SANDSTONE) {
            var stack = Items.SAND.getDefaultStack();
            stack.setCount(getAmount(fortune, random));
            return Optional.of(stack);
        }
        if (block == Blocks.RED_SANDSTONE) {
            var stack = Items.RED_SAND.getDefaultStack();
            stack.setCount(getAmount(fortune, random));
            return Optional.of(stack);
        }
        if (block == Blocks.COBBLESTONE) {
            var stack = Items.GRAVEL.getDefaultStack();
            stack.setCount(getAmount(fortune, random));
            return Optional.of(stack);
        }
        return Optional.empty();
    }

    private static int getAmount(int fortune, Random random) {
        // calculate amount
        int amount = random.nextInt(1) + 1;
        return Math.min(amount + fortune, 4);
    }

    public static void drill(World world, PlayerEntity player, BlockPos pos, BlockState blockState, int depth) {
        var tool = player.getMainHandStack();
        var toolHandler = new ToolHandler(tool);
        if (!toolHandler.test(blockState)) return;
        HitResult hitResult = player.raycast(player.getBlockInteractionRange(), 0, false);

        List<Drops> dropsInstances = new ArrayList<>();

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) hitResult;
            float hardnessRef = blockState.getBlock().getHardness();
            var targetVec = bhr.getSide().getVector();
            if (isLookingAt(player.getRotationVector(), targetVec, 0.55f, 0.55f)) {
                var dir = targetVec.multiply(-1);
                for (int i = 0; i < depth; i++) {
                    var _pos = pos.add(dir.multiply(i));
                    dropsInstances.add(tryBreakBlock(world, _pos, player, tool, hardnessRef, toolHandler));
                }
            }
        }

        DropDrops(world, player, pos, dropsInstances);
    }

    public static void lumber(World world, PlayerEntity player, BlockPos pos, BlockState blockState) {
        var tool = player.getMainHandStack();
        var toolHandler = new ToolHandler(tool);
        if (!toolHandler.test(blockState)) return;

        if (!blockState.isIn(BlockTags.LOGS) && !blockState.isOf(Blocks.MANGROVE_ROOTS)) return;

        Block log = blockState.getBlock();

        AtomicInteger logs = new AtomicInteger();
        ForAxis(true, false, true, pos, (_pos) -> {
            if (world.getBlockState(_pos).isOf(log)) {
                logs.getAndIncrement();
            }
        });
        if (logs.get() > 1) return;

        List<BlockPos> positions = findLogs(new ArrayList<>(), world, pos, log, 48);

        boolean hasLeaves = positions.stream()
            .anyMatch((_pos) -> world.getBlockState(_pos.up()).isIn(BlockTags.LEAVES));
        if (!hasLeaves) return;

        List<Drops> dropsInstances = positions.stream()
            .map(bp -> breakBlockReturnDrop((ServerWorld) world, bp, player, tool))
            .toList();

        DropDrops(world, player, pos, dropsInstances);
    }

    private static List<BlockPos> findLogs(List<BlockPos> positions, World world, BlockPos pos, Block log, int depth) {
        if (depth <= 0) return positions;
        positions.add(pos);
        Consumer<BlockPos> consumer = (_pos) -> {
            if (positions.contains(_pos)) return;
            if (world.getBlockState(_pos).isOf(log)) {
                findLogs(positions, world, _pos, log, depth - 1);
            }
        };
        ForAxis(-1, 1, 0, 1, -1, 1, pos, consumer);
        return positions;
    }

    public static void excavate(World world, PlayerEntity player, BlockPos pos, BlockState blockState) {
        var tool = player.getMainHandStack();
        var toolHandler = new ToolHandler(tool);
        if (!toolHandler.test(blockState)) return;

        List<Drops> dropsInstances = new ArrayList<>();

        HitResult hitResult = player.raycast(player.getBlockInteractionRange(), 0, false);
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) hitResult;
            float hardnessRef = blockState.getBlock().getHardness();
            ForAxis(player, bhr.getSide(), pos, (_pos) -> dropsInstances.add(tryBreakBlock(world, _pos, player, tool, hardnessRef, toolHandler)));
        }

        DropDrops(world, player, pos, dropsInstances);
    }

    private static void DropDrops(World world, PlayerEntity player, BlockPos pos, List<Drops> dropsInstances) {
        Drops drops = Drops.Merge(dropsInstances);

        BlockEntity _blockEntity = world.getBlockEntity(pos);
        boolean hasEntity = _blockEntity != null;

        if (!player.isCreative() && !hasEntity) {
            // drop items
            drops.items().forEach(drop -> Block.dropStack(world, pos, drop));
            // drop experience
            BlockUtils.dropExperience((ServerWorld) world, pos, drops.experience());
        }
    }

    private static Drops tryBreakBlock(World world, BlockPos pos, PlayerEntity player, ItemStack tool, float hardnessRef, ToolHandler toolHandler) {

        BlockState _blockState = world.getBlockState(pos);
        float hardness = _blockState.getBlock().getHardness();
        if (Math.abs(hardnessRef - hardness) < 0.5f && toolHandler.test(_blockState)) {
            return breakBlockReturnDrop((ServerWorld) world, pos, player, tool);
        }
        return Drops.EMPTY;
    }

    private static void ForAxis(PlayerEntity player, Direction direction, BlockPos center, Consumer<BlockPos> consumer) {
        Vec3i vec = direction.getVector();
        if (isLookingAt(player.getRotationVector(), vec, 0.6f, 0.35f)) {
            // execute for each axis
            ForAxis(vec.getX() == 0, vec.getY() == 0, vec.getZ() == 0, center, consumer);
        }
    }

    private static boolean isLookingAt(Vec3d playerRotationVector, Vec3i targetVector, float verticalTolerance, float horizontalTolerance) {
        Vec3d playerVec = playerRotationVector.multiply(-1);
        // if the player looks too far away, return false
        return (Math.abs(targetVector.getX() - playerVec.getX()) <= horizontalTolerance)
                && (Math.abs(targetVector.getY() - playerVec.getY()) <= verticalTolerance)
                && (Math.abs(targetVector.getZ() - playerVec.getZ()) <= horizontalTolerance);
    }

    private static void ForAxis(boolean xAxis, boolean yAxis, boolean zAxis, BlockPos center, Consumer<BlockPos> consumer) {
        ForAxis((xAxis ? -1 : 0), (xAxis ? 1 : 0), (yAxis ? -1 : 0), (yAxis ? 1 : 0), (zAxis ? -1 : 0), (zAxis ? 1 : 0), center, consumer);
    }

    private static void ForAxis(int xA, int xB, int yA, int yB, int zA, int zB, BlockPos center, Consumer<BlockPos> consumer) {
        for (int x = xA; x <= xB; x++) {
            for (int z = zA; z <= zB; z++) {
                for (int y = yA; y <= yB; y++) {
                    consumer.accept(center.add(x, y, z));
                }
            }
        }
    }

}
