package mask_of_loki.coretweaks.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import mask_of_loki.coretweaks.CTFactory;
import mask_of_loki.coretweaks.CTTradeOffers;
import mask_of_loki.coretweaks.config.MainConfig;
import mask_of_loki.coretweaks.config.TradeConfig;
import mask_of_loki.coretweaks.config.types.Trade;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradeOffers.Factory;
import net.minecraft.village.VillagerProfession;

public class TradeOffersBuilder {
	Map<String, List<Trade>> trades = new HashMap<String, List<Trade>>();
	
	public void add(TradeConfig config) {
		List<Trade> currentTrades = trades.get(config.profession);
		if (currentTrades == null) {
			currentTrades = new ArrayList<Trade>();
			trades.put(config.profession, currentTrades);
		}
		List<Trade> newTrades = config.trades;
		newTrades.forEach(trade -> {
			if (trade.level < 1) {
				trade.level = config.level;
			}
		});
		currentTrades.addAll(newTrades);
	}
	
	public void create() {
		
		// Cleanup before calling
		Identifier mct = new Identifier("mct:trades");
		Identifier minecraft = new Identifier("minecraft:trades");
		CTTradeOffers.removeVillagerTrades(mct);
		CTTradeOffers.removeVillagerTrades(minecraft);
		CTTradeOffers.removeWandererTrades(mct);
		CTTradeOffers.removeWandererTrades(minecraft);
		
		// Load the config file trades
		Map<VillagerProfession, Map<Integer, TradeOffers.Factory[]>> hashMap = new HashMap<VillagerProfession, Map<Integer, TradeOffers.Factory[]>>();
		List<Map<Integer, TradeOffers.Factory[]>> wanderer_trades = new ArrayList<Map<Integer, TradeOffers.Factory[]>>();
		trades.forEach((id, theTrades) -> {
			if (!id.equals("minecraft:wandering_trader")) {
				VillagerProfession profession = Registry.VILLAGER_PROFESSION.get(new Identifier(id));
				if (profession != null) {
					Map<Integer, TradeOffers.Factory[]> sorted = getSorted(theTrades);
					hashMap.merge(profession, sorted, CTTradeOffers::mergeTrades);
				} else {
					System.out.println(String.format("Ignoring villager trade list for '%s' because profession does not exist", id));
				}
			} else {
				wanderer_trades.add(getSorted(theTrades));
			}
		});
		
		// Add the config file trades
		CTTradeOffers.addVillagerTrades(mct, hashMap);
		wanderer_trades.forEach(wTrades -> CTTradeOffers.addWandererTrades(mct, wTrades));
		
		// Add vanilla trades if requested
		if (MainConfig.appendBuiltinTrades()) {
			Map<VillagerProfession, Map<Integer, TradeOffers.Factory[]>> builtinTrades = new HashMap<VillagerProfession, Map<Integer, TradeOffers.Factory[]>>();
			TradeOffers.PROFESSION_TO_LEVELED_TRADE.forEach((profession, original) -> {
				Map<Integer, TradeOffers.Factory[]> trades = new HashMap<Integer, TradeOffers.Factory[]>();
				original.forEach((key, value) -> trades.put(key, value));
				builtinTrades.put(profession, trades);
			});
			CTTradeOffers.addVillagerTrades(minecraft, builtinTrades);
			
			Map<Integer, TradeOffers.Factory[]> wTrades = new HashMap<Integer, TradeOffers.Factory[]>();
			TradeOffers.WANDERING_TRADER_TRADES.forEach((key, value) -> wTrades.put(key, value));
			
			CTTradeOffers.addWandererTrades(minecraft, wTrades);
		}
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
