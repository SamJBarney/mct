package mask_of_loki.coretweaks;

import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import mask_of_loki.coretweaks.command.MCTCommand;
import mask_of_loki.coretweaks.config.MainConfig;
import mask_of_loki.coretweaks.config.TradeConfig;
import net.fabricmc.api.ModInitializer;

public class Mod implements ModInitializer {

	public Mod() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onInitialize() {
		this.loadConfigs();
		MCTCommand.register();
	}
	
	private void loadConfigs() {
		try {
			MainConfig.load();
			TradeConfig.load();
		} catch (JsonIOException | JsonSyntaxException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CTTradeOffers.rebuild();
	}

}
