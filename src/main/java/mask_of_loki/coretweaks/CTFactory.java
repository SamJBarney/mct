package mask_of_loki.coretweaks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mask_of_loki.coretweaks.config.types.Enchant;
import mask_of_loki.coretweaks.config.types.Trade;
import mask_of_loki.coretweaks.config.types.TradeItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.enchantment.EnchantmentHelper;

public class CTFactory implements TradeOffers.Factory {
	private Trade trade;
	
	public CTFactory(Trade trade) {
		this.trade = trade;
	}
	
	@Override
	public TradeOffer create(Entity var1, Random random) {
		TradeOffer offer = null;
		Item itemA = null;
		ItemStack stackA = null;
		if (trade.buyA != null) {
			itemA = trade.buyA.asItem();
			int amount = getAmount(trade.buyA, random);
			if (itemA != null && amount > 0) {
				stackA = new ItemStack(itemA, amount);
				stackA = applyData(trade.buyA, stackA, random);
			}
		}

		Item itemB = null;
		ItemStack stackB = null;
		if (trade.buyB != null) {
			itemB = trade.buyB.asItem();
			int amount = getAmount(trade.buyB, random);
			if (itemB != null && amount > 0) {
				stackB = new ItemStack(itemB, amount);
				stackB = applyData(trade.buyB, stackB, random);
			}
		}

		Item result = null;
		ItemStack stackResult = null;
		if (trade.result != null) {
			result = trade.result.asItem();
			int amount = getAmount(trade.result, random);
			if (result != null && amount > 0) {
				stackResult = new ItemStack(result, amount);
				stackResult = applyData(trade.result, stackResult, random);
			}
		}
		
		if (result != null) {
			int uses = trade.uses;
			
			if (stackA != null) {
				if (stackB != null) {
					offer = new TradeOffer(stackA, stackB, stackResult, uses, uses, 0.5f);
				} else {
					offer = new TradeOffer(stackA, stackResult, uses, uses, 0.5f);
				}
			} else if (stackB != null) {
				offer = new TradeOffer(stackB, stackResult, uses, uses, 0.5f);
			}
		}
		
		return offer;
	}

	private static ItemStack applyData(TradeItem trade, ItemStack stack, Random random) {
		if (trade.potion != null) {
			if (!trade.potion.equals("mct:any")) {
				Potion potion = Registry.POTION.get(new Identifier(trade.potion));
				stack = PotionUtil.setPotion(stack, potion);
			} else {
				Potion potion = Registry.POTION.getRandom(random);
				stack = PotionUtil.setPotion(stack, potion);
			}
		}
		
		if (trade.effects != null) {
			for (int i = 0; i < trade.effects.length; ++i) {
				StatusEffect effect = null;
				if (!trade.effects[i].id.equals("mct:any")) {
					effect = Registry.STATUS_EFFECT.get(new Identifier(trade.effects[i].id));
				} else {
					effect = Registry.STATUS_EFFECT.getRandom(random);
				}
				if (effect != null) {
					SuspiciousStewItem.addEffectToStew(stack, effect, trade.effects[i].duration);
				}
			}
		}
		
		if (trade.enchants != null) {
			Map<Enchantment, Integer> enchants = new HashMap<Enchantment, Integer>();
			for (int i = 0; i < trade.enchants.length; ++i) {
				Enchantment enchant = null;
				if (!trade.enchants[i].id.equals("mct:any")) {
					enchant = Registry.ENCHANTMENT.get(new Identifier(trade.enchants[i].id));
				} else {
					enchant = Registry.ENCHANTMENT.getRandom(random);
				}
				if (enchant != null) {
					int lvl = getEnchantLvl(trade.enchants[i], enchant, random);
					enchants.put(enchant, lvl);
				}
			}
			EnchantmentHelper.set(enchants, stack);
		}
		
		return stack;
	}
	
	private static int getAmount(TradeItem item, Random random) {
		return Math.min(getValue(item.amount, item.min, item.max, random), item.asItem().getMaxAmount());
	}
	
	private static int getEnchantLvl(Enchant enchant, Enchantment enchantment, Random random) {
		int min_lvl = enchantment.getMinimumLevel();
		int max_lvl = enchantment.getMaximumLevel();
		int lvl = (enchant.lvl > 0) ? Math.min(enchant.lvl, max_lvl) : 0;
		int min = (enchant.min > 0)? Math.max(enchant.min, min_lvl) : 0;
		int max = (enchant.max > 0) ? Math.min(enchant.max, max_lvl) : 0;
		return getValue(lvl, min, max, random);
	}
	
	private static int getValue(int value, int min, int max, Random random) {
		if (value < 1) {
			if (min > 0 || max > 0) {
				if (min < max) {
					return random.nextInt(max - min) + min;
				}
				return min;
			}
			
			return 1;
		}
		
		return value;
	}
	
}
