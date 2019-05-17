package mask_of_loki.coretweaks;

import java.util.Map;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

public class CTTradeOffers {
	private static CTTradeOffers INSTANCE;
	
	public static CTTradeOffers Instance() {
		return INSTANCE;
	}
	
	public static void setup(Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> professionTrades, Int2ObjectMap<TradeOffers.Factory[]> wandererTrades) {
		INSTANCE = new CTTradeOffers(professionTrades, wandererTrades);
	}
	
	private CTTradeOffers(Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> professionTrades, Int2ObjectMap<TradeOffers.Factory[]> wandererTrades) {
		this.professionToQualityTrades = professionTrades;
		this.wandererTrades = wandererTrades;
	}

	public final Map<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> professionToQualityTrades;
	public final Int2ObjectMap<TradeOffers.Factory[]>  wandererTrades;
}
