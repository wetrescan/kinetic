package net.wecan.kinetic;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wecan.kinetic.config.GravityConfig;
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
			dispatcher.register(CommandManager.literal("gravity")
					.then(CommandManager.literal("value")
							.executes(context -> {
								double gravity = GravityConfig.GRAVITY;
								context.getSource().sendFeedback(() -> Text.literal("Current gravity value: " + gravity), false);
								return 1;
							})
							.then(CommandManager.argument("new_value", DoubleArgumentType.doubleArg())
									.executes(context -> {
										double newGravity = DoubleArgumentType.getDouble(context, "new_value");
										GravityConfig.GRAVITY = newGravity;
										context.getSource().sendFeedback(() -> Text.literal("New gravity value set to: " + newGravity), false);
										return 1;
									})
							)
							.then(CommandManager.literal("default").executes(context -> {
								GravityConfig.GRAVITY = Double.valueOf("9.807");
								context.getSource().sendFeedback(() -> Text.literal("Smash multiplier set to: default (9.807)"), true);
								return 1;
							}))
					)
					.then(CommandManager.literal("smash")
							.executes(context -> {
								double smashMultiplier = GravityConfig.SMASH_MULTIPLIER;
								context.getSource().sendFeedback(() -> Text.literal("Current smash multiplier: " + smashMultiplier), false);
								return 1;
							})
							.then(CommandManager.argument("new_value", DoubleArgumentType.doubleArg())
									.executes(context -> {
										double newSmashMultiplier = DoubleArgumentType.getDouble(context, "new_value");
										GravityConfig.SMASH_MULTIPLIER = newSmashMultiplier;
										context.getSource().sendFeedback(() -> Text.literal("New smash value set to: " + newSmashMultiplier), false);
										return 1;
									})
							)
							.then(CommandManager.literal("1.7").executes(context -> {
								GravityConfig.SMASH_MULTIPLIER = Double.valueOf("1.7");
								context.getSource().sendFeedback(() -> Text.literal("Smash multiplier set to: default (1.7)"), true);
								return 1;
							}))
					)
					.then(CommandManager.literal("direction")
							.executes(context -> {
								String direction = GravityConfig.DIRECTION;
								context.getSource().sendFeedback(() -> Text.literal("Current direction: " + direction), false);
								return 1;
							})
							.then(CommandManager.literal("south").executes(context -> {
								GravityConfig.DIRECTION = "south";
								context.getSource().sendFeedback(() -> Text.literal("Direction set to: south"), true);
								return 1;
							}))
							.then(CommandManager.literal("east").executes(context -> {
								GravityConfig.DIRECTION = "east";
								context.getSource().sendFeedback(() -> Text.literal("Direction set to: east"), true);
								return 1;
							}))
							.then(CommandManager.literal("west").executes(context -> {
								GravityConfig.DIRECTION = "west";
								context.getSource().sendFeedback(() -> Text.literal("Direction set to: west"), true);
								return 1;
							}))
							.then(CommandManager.literal("north").executes(context -> {
								GravityConfig.DIRECTION = "north";
								context.getSource().sendFeedback(() -> Text.literal("Direction set to: north"), true);
								return 1;
							}))
							.then(CommandManager.literal("up").executes(context -> {
								GravityConfig.DIRECTION = "up";
								context.getSource().sendFeedback(() -> Text.literal("Direction set to: up"), true);
								return 1;
							}))
							.then(CommandManager.literal("down").executes(context -> {
								GravityConfig.DIRECTION = "down";
								context.getSource().sendFeedback(() -> Text.literal("Direction set to: down"), true);
								return 1;
							}))
					)
			);
		});
	}
}