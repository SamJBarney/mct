package mask_of_loki.coretweaks.config.types;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class TradeItem implements ItemConvertible {
	public String id;
	public int amount;
	public int min;
	public int max;
	public String potion;
	public Enchant[] enchants;
	public Effect[] effects;

	@Override
	public Item asItem() {
		return Registry.ITEM.get(new Identifier(this.id));
	}
}
