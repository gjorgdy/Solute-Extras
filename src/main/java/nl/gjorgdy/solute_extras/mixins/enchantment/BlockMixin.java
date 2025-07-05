package nl.gjorgdy.solute_extras.mixins.enchantment;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import nl.gjorgdy.solute_extras.SoluteExtras;
import nl.gjorgdy.solute_extras.modules.Enchantment;
import nl.gjorgdy.solute_extras.utils.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "getDroppedStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)Ljava/util/List;",
    at=@At("RETURN"), cancellable = true)
    private static void crush(BlockState state, ServerWorld world, BlockPos pos, BlockEntity blockEntity, Entity entity, ItemStack stack, CallbackInfoReturnable<List<ItemStack>> cir) {
        // Crushing
        int crushing = EnchantmentHelper.getLevel(SoluteExtras.ENCHANTMENTS.CRUSHING, stack);
        var fortune = EnchantmentUtils.getEnchantmentFromString(world.getServer(), "minecraft:fortune");
        if (fortune.isEmpty()) return;
        int fortuneLvl = EnchantmentHelper.getLevel(fortune.get(), stack);
        if (crushing > 0) {
            var optDrop = Enchantment.crush(world.getRandom(), state.getBlock(), fortuneLvl);
            optDrop.ifPresent(itemStack -> cir.setReturnValue(
                List.of(itemStack)
            ));
        }
    }

}
