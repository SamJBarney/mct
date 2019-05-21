package mask_of_loki.coretweaks;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

public class CTTradeOffers {
	private static Map<VillagerProfession, Map<Integer, TradeOffers.Factory[]>> villagerTrades;
	private static Map<Integer, TradeOffers.Factory[]>  wandererTrades;

	private static Map<Identifier, Map<VillagerProfession, Map<Integer, TradeOffers.Factory[]>>> namespacedVillagerTrades;
	private static Map<Identifier, Map<Integer, TradeOffers.Factory[]>>  namespacedWandererTrades;
	
	public static TradeOffers.Factory[] villagerTrades(VillagerProfession profession, int lvl) {
		Map<Integer, TradeOffers.Factory[]> trades = villagerTrades.get(profession);
		if (trades != null) {
			return trades.get(lvl);
		}
		return null;
	}

	public static TradeOffers.Factory[] wandererTrades(int lvl) {
		return wandererTrades.get(lvl);
	}
	
	public static void addVillagerTrades(Identifier namespace, Map<VillagerProfession, Map<Integer, TradeOffers.Factory[]>> trades) {
		Map<VillagerProfession, Map<Integer, TradeOffers.Factory[]>> theTrades = namespacedVillagerTrades.getOrDefault(namespace, new HashMap<VillagerProfession, Map<Integer, TradeOffers.Factory[]>>());
		trades.forEach((profession, professionTrades) -> {
			professionTrades = mergeTrades(theTrades.getOrDefault(profession, new HashMap<Integer, TradeOffers.Factory[]>()), professionTrades);
			theTrades.put(profession, professionTrades);
		});
		
		namespacedVillagerTrades.put(namespace, theTrades);
	}
	
	public static void addWandererTrades(Identifier namespace, Map<Integer, TradeOffers.Factory[]> trades) {
		Map<Integer, TradeOffers.Factory[]> theTrades = namespacedWandererTrades.getOrDefault(namespace, new HashMap<Integer, TradeOffers.Factory[]>());
		theTrades = mergeTrades(theTrades, trades);
		namespacedWandererTrades.put(namespace, theTrades);
	}
	
	public static void replaceVillagerTrades(Identifier namespace, Map<VillagerProfession, Map<Integer, TradeOffers.Factory[]>> trades) {
		namespacedVillagerTrades.put(namespace, trades);
	}
	
	public static void replaceWandererTrades(Identifier namespace, Map<Integer, TradeOffers.Factory[]> trades) {
		namespacedWandererTrades.put(namespace, trades);
	}
	
	public static void removeVillagerTrades(Identifier namespace) {
		namespacedVillagerTrades.remove(namespace);
	}
	
	public static void removeWandererTrades(Identifier namespace) {
		namespacedWandererTrades.remove(namespace);
	}
	
	public static void rebuild() {
		rebuildVillagerTrades();
		rebuildWandererTrades();
	}
	
	private static void rebuildVillagerTrades() {
		villagerTrades = new HashMap<VillagerProfession, Map<Integer, TradeOffers.Factory[]>>();
		 namespacedVillagerTrades.forEach((_ignore, namespace) -> {
			 namespace.forEach((profession, trades) -> {
				 villagerTrades.merge(profession, trades, CTTradeOffers::mergeTrades);
			 });
		 });
	}
	
	private static void rebuildWandererTrades() {
		wandererTrades = new HashMap<Integer, TradeOffers.Factory[]>();
		namespacedWandererTrades.forEach((_ignore, trades) -> {
			wandererTrades = mergeTrades(wandererTrades, trades);
		});
	}
	
	public static Map<Integer, TradeOffers.Factory[]> mergeTrades(Map<Integer, TradeOffers.Factory[]> trades1, Map<Integer, TradeOffers.Factory[]> trades2) {
		trades2.forEach((key, value) -> {
			TradeOffers.Factory[] trades = trades1.get(key);
			if (trades != null) {
				trades = ArrayUtils.addAll(trades, value);
			} else {
				trades = value;
			}
			trades1.put(key, trades);
		});
		
		return trades1;
	}
	
	static {
		villagerTrades = new HashMap<VillagerProfession, Map<Integer, TradeOffers.Factory[]>>();
		wandererTrades = new HashMap<Integer, TradeOffers.Factory[]>();
		namespacedVillagerTrades = new HashMap<Identifier, Map<VillagerProfession, Map<Integer, TradeOffers.Factory[]>>>();
		namespacedWandererTrades = new HashMap<Identifier, Map<Integer, TradeOffers.Factory[]>>();
	}
}
