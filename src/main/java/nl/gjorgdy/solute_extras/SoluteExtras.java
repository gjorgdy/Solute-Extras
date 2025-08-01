package nl.gjorgdy.solute_extras;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import nl.gjorgdy.solute_extras.listeners.PlayerBlockBreakListener;
import nl.gjorgdy.solute_extras.utils.EnchantmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoluteExtras implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Solute Extras");

    @Override
    public void onInitialize() {

        LOGGER.info("Overheating the furnace to add more solute to the base");

        PlayerBlockBreakEvents.BEFORE.register(new PlayerBlockBreakListener());

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            // Lumber
            var lumber = EnchantmentUtils.getEnchantmentFromString(server, "solute:lumber");
            lumber.ifPresent(enchantmentReference -> ENCHANTMENTS.LUMBER = enchantmentReference);
            // Excavation
            var excavation = EnchantmentUtils.getEnchantmentFromString(server, "solute:excavation");
            excavation.ifPresent(enchantmentReference -> ENCHANTMENTS.EXCAVATION = enchantmentReference);
            // Drilling
            var drilling = EnchantmentUtils.getEnchantmentFromString(server, "solute:drilling");
            drilling.ifPresent(enchantmentReference -> ENCHANTMENTS.DRILLING = enchantmentReference);
            // Crushing
            var crushing = EnchantmentUtils.getEnchantmentFromString(server, "solute:crushing");
            crushing.ifPresent(enchantmentReference -> ENCHANTMENTS.CRUSHING = enchantmentReference);
        });
    }

    public static class ENCHANTMENTS {
        public static RegistryEntry.Reference<Enchantment> LUMBER;
        public static RegistryEntry.Reference<Enchantment> EXCAVATION;
        public static RegistryEntry.Reference<Enchantment> DRILLING;
        public static RegistryEntry.Reference<Enchantment> CRUSHING;
    }
}
