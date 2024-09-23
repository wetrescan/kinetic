package net.wecan.kinetic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class Kinetic implements ModInitializer {
	public static final String MOD_ID = "kinetic";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("kkkkinetic");
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(literal("maincommand")
					.then(literal("firstsubcommand")
							.executes(context -> {
								context.getSource().getClass(Text.literal("Executed /maincommand firstsubcommand!"), false);
								return Command.SINGLE_SUCCESS;
							})
							.then(literal("firstsubsubcommand")
									.executes(context -> {
										context.getSource().getClass(Text.literal("Executed /maincommand firstsubcommand firstsubsubcommand!"), false);
										return Command.SINGLE_SUCCESS;
									})
							)
					)
					.then(literal("secondsubcommand")
							.then(argument("number", IntegerArgumentType.integer())
									.executes(context -> {
										int number = IntegerArgumentType.getInteger(context, "number");
										context.getSource().getClass(Text.literal("Executed /maincommand secondsubcommand with number: " + number), false);
										return Command.SINGLE_SUCCESS;
									})
									.then(literal("secondsubsubcommand")
											.executes(context -> {
												int number = IntegerArgumentType.getInteger(context, "number");
												context.getSource().getClass(Text.literal("Executed /maincommand secondsubcommand secondsubsubcommand with number: " + number), false);
												return Command.SINGLE_SUCCESS;
											})
									)
							)
					)
			);
		});
	}



	private int executeDirection(CommandContext<ServerCommandSource> context) {
		context.getSource().sendFeedback((Supplier<Text>) Text.literal("Executed Direction"), false);
		return Command.SINGLE_SUCCESS;
	}

	private int executeSouth(CommandContext<ServerCommandSource> context) {
		context.getSource().sendFeedback((Supplier<Text>) Text.literal("Executed south"), false);
		return Command.SINGLE_SUCCESS;
	}

	private int executeNorth(CommandContext<ServerCommandSource> context) {
		context.getSource().sendFeedback((Supplier<Text>) Text.literal("Executed north"), false);
		return Command.SINGLE_SUCCESS;
	}

	private int executeEast(CommandContext<ServerCommandSource> context) {
		context.getSource().sendFeedback((Supplier<Text>) Text.literal("Executed East"), false);
		return Command.SINGLE_SUCCESS;
	}

	private int executeWest(CommandContext<ServerCommandSource> context) {
		context.getSource().sendFeedback((Supplier<Text>) Text.literal("Executed west"), false);
		return Command.SINGLE_SUCCESS;
	}

	private int executeValue(CommandContext<ServerCommandSource> context) {
		context.getSource().sendFeedback(Text.literal("Executed value"), false);
		return Command.SINGLE_SUCCESS;
	}

	private int executeNumber(CommandContext<ServerCommandSource> context) {
		// Get the number argument
		int number = IntegerArgumentType.getInteger(context, "number");
		context.getSource().sendFeedback(Text.literal("Executed number with number " + number), false);
		return Command.SINGLE_SUCCESS;
	}
}