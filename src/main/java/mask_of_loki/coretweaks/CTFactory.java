package mask_of_loki.coretweaks;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
			if (itemA != null && trade.buyA.amount > 0) {
				stackA = new ItemStack(itemA, trade.buyA.amount);
				stackA = applyData(trade.buyA, stackA, random);
			}
		}

		Item itemB = null;
		ItemStack stackB = null;
		if (trade.buyB != null) {
			itemB = trade.buyB.asItem();
			if (itemB != null && trade.buyB.amount > 0) {
				stackB = new ItemStack(itemB, trade.buyB.amount);
				stackB = applyData(trade.buyB, stackB, random);
			}
		}

		Item result = null;
		ItemStack stackResult = null;
		if (trade.result != null) {
			result = trade.result.asItem();
			if (result != null && trade.result.amount > 0) {
				stackResult = new ItemStack(result, trade.result.amount);
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
				System.out.println(trade.potion);
				System.out.println(Registry.POTION.getId(potion).toString());
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
					int lvl = Math.min(enchant.getMaximumLevel(), Math.max(enchant.getMinimumLevel(), trade.enchants[i].lvl));
					enchants.put(enchant, lvl);
				}
			}
			EnchantmentHelper.set(enchants, stack);
		}
		
		return stack;
	}
	
}
