package nl.gjorgdy.solute_extras.utils;

import net.minecraft.block.Block;
import net.minecraft.block.RedstoneOreBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import nl.gjorgdy.solute_extras.interfaces.ExperienceDroppingBlockAccessor;

import java.util.Optional;

public class EnchantmentUtils {

    public static Optional<RegistryEntry.Reference<Enchantment>> getEnchantmentFromString(MinecraftServer server, String enchantmentId) {
        Identifier id = Identifier.tryParse(enchantmentId);
        if (id != null) return server.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(id);
        return Optional.empty();
    }

    public static int getExperienceDrops(ServerWorld world, ItemStack tool, Block block) {
        IntProvider intProvider;
        if (block instanceof ExperienceDroppingBlockAccessor experienceDroppingBlock) {
            intProvider = experienceDroppingBlock.solute$getIntProvider();
        } else if (block instanceof RedstoneOreBlock) {
            intProvider = UniformIntProvider.create(1, 5);
        } else {
            return 0;
        }

        return EnchantmentHelper.getBlockExperience(world, tool, intProvider.get(world.getRandom()));
    }

}
