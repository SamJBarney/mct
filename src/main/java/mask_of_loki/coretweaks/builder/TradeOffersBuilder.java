package mask_of_loki.coretweaks.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mask_of_loki.coretweaks.CTFactory;
import mask_of_loki.coretweaks.CTTradeOffers;
import mask_of_loki.coretweaks.config.MainConfig;
import mask_of_loki.coretweaks.config.TradeConfig;
import mask_of_loki.coretweaks.config.types.Trade;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers.Factory;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

public class TradeOffersBuilder {
	Map<String, List<Trade>> trades = new HashMap<String, List<Trade>>();
	Map<Integer, net.minecraft.village.TradeOffers.Factory[]> wanderer_trades;
	
	public void add(TradeConfig config) {
		List<Trade> currentTrades = trades.get(config.profession);
		if (currentTrades == null) {
			currentTrades = new ArrayList<Trade>();
			trades.put(config.profession, currentTrades);
		}
		List<Trade> newTrades = config.trades;
		newTrades.forEach(trade -> {
			System.out.print(config.profession);
			System.out.print(':');
			System.out.println(trade.level);
			if (trade.level < 1) {
				trade.level = config.level;
			}
		});
		currentTrades.addAll(newTrades);
	}
	
	public void create() {
		@SuppressWarnings("unchecked")
		Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> map = (Map)SystemUtil.consume(Maps.newHashMap(), (hashMap) -> {
			trades.forEach((id, theTrades) -> {
				System.out.println(String.format("Profession: %s", id));
				if (!id.equals("minecraft:wandering_trader")) {
					VillagerProfession profession = Registry.VILLAGER_PROFESSION.get(new Identifier(id));
					if (profession != null) {
						Map<Integer, TradeOffers.Factory[]> sorted = getSorted(theTrades);
						if (MainConfig.appendBuiltinTrades()) {
							TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(profession).forEach((key, trades) -> {
								net.minecraft.village.TradeOffers.Factory[] offers = sorted.get(key);
								if (offers == null) {
									offers = trades;
								} else {
									offers = ArrayUtils.addAll(offers, trades);
								}
								sorted.put(key, offers);
							});
						}
						hashMap.put(profession, copyToFastUtilMap(ImmutableMap.copyOf(sorted)));
					} else {
						System.out.println(String.format("Ignoring villager trade list for '%s' because profession does not exist", id));
					}
				} else {
					wanderer_trades = getSorted(theTrades);
					if (MainConfig.appendBuiltinTrades()) {
						System.out.println("Appending trades");
						TradeOffers.WANDERING_TRADER_TRADES.forEach((key, trades) -> {
							net.minecraft.village.TradeOffers.Factory[] offers = wanderer_trades.get(key);
							if (offers == null) {
								offers = trades;
							} else {
								offers = ArrayUtils.addAll(offers, trades);
							}
							wanderer_trades.put(key, offers);
						});
					}
				}
			});
		});
		
		Int2ObjectMap<TradeOffers.Factory[]> wander_trades = null;
		if (wanderer_trades != null) {
			System.out.println("Adding wandering trader values");
			wander_trades = copyToFastUtilMap(ImmutableMap.copyOf(wanderer_trades));
			System.out.println(String.format("%d %d", wanderer_trades.size(), wander_trades.size()));
		}
		CTTradeOffers.setup(map, wander_trades);
	}
	
	public Map<Integer, TradeOffers.Factory[]> getSorted(List<Trade> theTrades) {
		Map<Integer, TradeOffers.Factory[]> result = new HashMap<Integer, TradeOffers.Factory[]>();
		Map<Integer, ArrayList<net.minecraft.village.TradeOffers.Factory>> temp = new HashMap<Integer, ArrayList<TradeOffers.Factory>>();
		theTrades.forEach(trade -> {
			
			ArrayList<TradeOffers.Factory> qualityLevel = temp.getOrDefault(trade.level, new ArrayList<TradeOffers.Factory>());
			qualityLevel.add(new CTFactory(trade));
			temp.put(trade.level, qualityLevel);
		});
		
		
		temp.forEach((quality, trades) -> {
			result.put(quality, trades.toArray(new Factory[trades.size()]));
		});
		
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	private Int2ObjectMap<TradeOffers.Factory[]> copyToFastUtilMap(ImmutableMap<Integer, TradeOffers.Factory[]> immutableMap_1) {
	      return new Int2ObjectOpenHashMap(immutableMap_1);
	   }
}
