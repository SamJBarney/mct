package mask_of_loki.coretweaks.command;

import java.io.IOException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import mask_of_loki.coretweaks.config.MainConfig;
import mask_of_loki.coretweaks.config.TradeConfig;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.network.chat.TextComponent;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

public class MCTCommand {
	public static void register() {
		CommandRegistry.INSTANCE.register(false, dispatcher -> {
			dispatcher.register(command());
		});
	}
	
	private static LiteralArgumentBuilder<ServerCommandSource> command() {
		return CommandManager.literal("mct")
				.requires((source) -> {
					return source.hasPermissionLevel(2);
				})
				.then(subcommandConfig())
				.then(subcommandReload());
	}
	
	private static LiteralArgumentBuilder<ServerCommandSource> subcommandConfig() {
		return CommandManager.literal("config")
				.then(
						CommandManager.argument("field", StringArgumentType.word())
						.suggests(MainConfig.CONFIG_SUGGESTION_PROVIDER)
						.then(
								CommandManager.argument("value", BoolArgumentType.bool())
								.executes(context -> {
									String field = StringArgumentType.getString(context, "field");
									switch(field) {
										case "tweakTrades":
											MainConfig.setTweakTrades(BoolArgumentType.getBool(context, "value"));
											return 1;
										case "appendBuiltinTrades":
											MainConfig.setAppendBuiltinTrades(BoolArgumentType.getBool(context, "value"));
											return 1;
									}
									return 0; 
								})
						)
						.executes(context -> {
							String field = StringArgumentType.getString(context, "field");
							switch(field) {
								case "tweakTrades":
									context.getSource().sendFeedback(new TextComponent(String.format("tweakTrades is set to %s",Boolean.toString(MainConfig.tweakTrades()))), false);
									return Boolean.compare(MainConfig.tweakTrades(), false);
								case "appendBuiltinTrades":
									context.getSource().sendFeedback(new TextComponent(String.format("appendBuiltinTrades is set to %s",Boolean.toString(MainConfig.appendBuiltinTrades()))), false);
									return Boolean.compare(MainConfig.appendBuiltinTrades(), false);
							}
							return 0; 
						})
				);
	}
	
	private static LiteralArgumentBuilder<ServerCommandSource> subcommandReload() {
		return CommandManager.literal("reload")
				.executes(context -> {
					try {
						MainConfig.load();
						TradeConfig.load();
					} catch (JsonIOException | JsonSyntaxException | IOException e) {
						e.printStackTrace();
						context.getSource().sendFeedback(new TextComponent("Configurations reloaded"), false);
						return 0;
					}
					
					context.getSource().sendFeedback(new TextComponent("Unable to reload configurations"), false);
					return 1;
				});
	}
}
