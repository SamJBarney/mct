package mask_of_loki.coretweaks.config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import mask_of_loki.coretweaks.CTTradeOffers;
import net.fabricmc.loader.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;

public class MainConfig {
	private static final String CONFIG_NAME = "mct.json";
	private static MainConfig INSTANCE;
	private static String[] fieldNames;
	
	public static SuggestionProvider<ServerCommandSource> CONFIG_SUGGESTION_PROVIDER = (commandContext_1, suggestionsBuilder_1) -> {
		return net.minecraft.server.command.CommandSource.suggestMatching(fieldNames, suggestionsBuilder_1);
	};
	
	public static void load() throws JsonIOException, JsonSyntaxException, IOException {
		try {
			Path path = FabricLoader.INSTANCE.getConfigDirectory().toPath().resolve(CONFIG_NAME);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			INSTANCE = gson.fromJson(new JsonReader(new FileReader(new File(path.toString()))), MainConfig.class);
		} catch (FileNotFoundException e) {
			INSTANCE = defaults();
			save();
		} catch (Exception e) {
			e.printStackTrace();
			if (INSTANCE == null) {
				INSTANCE = defaults();
			}
		}
	}
	
	public static void save() throws IOException {
		if (INSTANCE != null) {
			Path path = FabricLoader.INSTANCE.getConfigDirectory().toPath().resolve(CONFIG_NAME);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String json = gson.toJson(INSTANCE, MainConfig.class);
			BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString()));
		    writer.write(json);
		    writer.close();
		}
	}
	
	public static boolean tweakTrades() {
		return INSTANCE.tweakTrades;
	}
	
	public static boolean appendBuiltinTrades() {
		return INSTANCE.appendBuiltinTrades;
	}
	
//	public static boolean qualityTrades() {
//		return INSTANCE.qualityTrades;
//	}
	
	public static void setTweakTrades(boolean value) {
		INSTANCE.tweakTrades = value;
		try {
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setAppendBuiltinTrades(boolean value) {
		INSTANCE.appendBuiltinTrades = value;
		try {
			TradeConfig.load();
			CTTradeOffers.rebuild();
			save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
//	public static void setQualityTrades(boolean value) {
//		INSTANCE.qualityTrades = value;
//		try {
//			save();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
	
	private static MainConfig defaults() {
		MainConfig config = new MainConfig();
		config.tweakTrades = true;
		config.appendBuiltinTrades = true;
//		config.qualityTrades = false;
		return new MainConfig();
	}
	
	static {
		fieldNames = Arrays.asList(MainConfig.class.getFields())
		.stream().filter(field -> {
			int modifiers = field.getModifiers();
			return !Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers);
		}).map(field -> {
			return field.getName();
		}).toArray(String[]::new);
	}
	
	
	public boolean tweakTrades;
	public boolean appendBuiltinTrades;
//	public boolean qualityTrades;
}
