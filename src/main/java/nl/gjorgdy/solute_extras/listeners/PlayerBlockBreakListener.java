package nl.gjorgdy.solute_extras.listeners;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.gjorgdy.solute_extras.SoluteExtras;
import nl.gjorgdy.solute_extras.modules.Enchantment;
import org.jetbrains.annotations.Nullable;

public class PlayerBlockBreakListener implements PlayerBlockBreakEvents.Before {

    @Override
    public boolean beforeBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity) {
        // Enchantments
        ItemStack playerTool = player.getMainHandStack();
        // Excavation
        int excavation = EnchantmentHelper.getLevel(SoluteExtras.ENCHANTMENTS.EXCAVATION, playerTool);
        if (excavation > 0 && !player.isSneaking()) Enchantment.excavate(world, player, pos, state);
        // Drilling
        int drilling = EnchantmentHelper.getLevel(SoluteExtras.ENCHANTMENTS.DRILLING, playerTool);
        if (drilling > 0 && !player.isSneaking()) {
            int depth = switch (drilling) {
                case 1 -> 2;
                case 2 -> 4;
                case 3 -> 5;
                default -> 0;
            };
            Enchantment.drill(world, player, pos, state, depth);
        }
        return true;
    }

}
