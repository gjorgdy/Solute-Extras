package nl.gjorgdy.solute_extras.models;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public record Drops(List<ItemStack> items, int experience) {

    public static final Drops EMPTY = new Drops(List.of(), 0);

    public static Drops Merge(List<Drops> drops) {
        var items = new ArrayList<ItemStack>();
        var experience = 0;
        for (var drop : drops) {
            if (drop.equals(Drops.EMPTY)) continue;
            items.addAll(drop.items);
            experience += drop.experience;
        }
        return new Drops(items, experience);
    }

}
