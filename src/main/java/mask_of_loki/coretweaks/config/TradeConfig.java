package mask_of_loki.coretweaks.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import mask_of_loki.coretweaks.builder.TradeOffersBuilder;
import mask_of_loki.coretweaks.config.types.Trade;
import net.fabricmc.loader.FabricLoader;

public class TradeConfig {
	public String profession;
	public int level;
	
	public List<Trade> trades = new ArrayList<Trade>();
	
	private static String CONFIG_FOLDER = "mct";

	public static void load() throws IOException {
		Path path = FabricLoader.INSTANCE.getConfigDirectory().toPath().resolve(CONFIG_FOLDER);
		if (Files.exists(path)) {
			loadTrades(path);
		} else {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void loadTrades(Path configDir) throws IOException {
		TradeOffersBuilder builder = new TradeOffersBuilder();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Stream<Path> stream = Files.list(configDir).filter(file -> file.toString().endsWith(".json"));
		stream.forEach(path -> {
			try {
				loadTradeConfig(path, gson, builder);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		stream.close();
		
		builder.create();
	}

	private static void loadTradeConfig(Path path, Gson gson, TradeOffersBuilder builder) throws JsonIOException, JsonSyntaxException, FileNotFoundException {
		TradeConfig config = gson.fromJson(new JsonReader(new FileReader(new File(path.toString()))), TradeConfig.class);
		if (config.level < 1) {
			config.level = 1;
		}
		builder.add(config);
	}
}
