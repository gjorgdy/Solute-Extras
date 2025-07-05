package nl.gjorgdy.solute_extras.mixins.enchantment;

import net.minecraft.block.ExperienceDroppingBlock;
import net.minecraft.util.math.intprovider.IntProvider;
import nl.gjorgdy.solute_extras.interfaces.ExperienceDroppingBlockAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ExperienceDroppingBlock.class)
public class ExperienceDroppingBlockMixin implements ExperienceDroppingBlockAccessor {

    @Shadow @Final private IntProvider experienceDropped;

    @Override
    public IntProvider solute$getIntProvider() {
        return this.experienceDropped;
    }

}
