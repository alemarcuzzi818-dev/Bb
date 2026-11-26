package com.matias.nightfallstalker;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

public class TerrorEventHandler {
    private static final Random RANDOM = new Random();
    private int cooldownTicks = 20 * 25;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        PlayerEntity player = event.player;
        World world = player.level;

        if (world.isClientSide || player.isCreative() || player.isSpectator()) {
            return;
        }

        if (cooldownTicks > 0) {
            cooldownTicks--;
            return;
        }

        BlockPos pos = player.blockPosition();
        int lightLevel = world.getMaxLocalRawBrightness(pos);
        boolean isNightOrDark = !world.isDay() || lightLevel <= 4;

        int chanceBound = isNightOrDark ? 320 : 1400;
        if (RANDOM.nextInt(chanceBound) == 0) {
            triggerTerrorEvent(player, world, pos, isNightOrDark);
            cooldownTicks = 20 * (35 + RANDOM.nextInt(41));
        }
    }

    private void triggerTerrorEvent(PlayerEntity player, World world, BlockPos pos, boolean isNightOrDark) {
        int roll = RANDOM.nextInt(100);

        if (roll < 40) {
            world.playSound(
                null,
                pos.offset(RANDOM.nextInt(7) - 3, 0, RANDOM.nextInt(7) - 3),
                SoundEvents.AMBIENT_CAVE,
                SoundCategory.AMBIENT,
                2.0F,
                0.55F
            );
            player.addEffect(new EffectInstance(Effects.BLINDNESS, 20 * 4, 0, false, false));
            player.addEffect(new EffectInstance(Effects.WEAKNESS, 20 * 6, 0, false, false));
            player.displayClientMessage(
                new StringTextComponent("Escuchaste algo respirar muy cerca...").withStyle(TextFormatting.DARK_RED, TextFormatting.ITALIC),
                true
            );
        } else if (roll < 75) {
            world.playSound(
                null,
                pos,
                SoundEvents.ENTITY_ENDERMAN_STARE,
                SoundCategory.HOSTILE,
                1.8F,
                0.6F
            );
            player.addEffect(new EffectInstance(Effects.CONFUSION, 20 * 5, 0, false, false));
            player.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 20 * 3, 1, false, false));
            player.displayClientMessage(
                new StringTextComponent("Algo te observa desde las sombras.").withStyle(TextFormatting.RED),
                true
            );
        } else if (isNightOrDark) {
            double angle = RANDOM.nextDouble() * Math.PI * 2;
            double dist = 7.0 + RANDOM.nextDouble() * 5.0;
            double spawnX = player.getX() + Math.cos(angle) * dist;
            double spawnZ = player.getZ() + Math.sin(angle) * dist;
            BlockPos spawnPos = new BlockPos(spawnX, player.getY(), spawnZ);

            EndermanEntity stalker = EntityType.ENDERMAN.create(world);
            if (stalker != null) {
                stalker.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, RANDOM.nextFloat() * 360F, 0F);
                stalker.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 20 * 20, 1, false, true));
                world.addFreshEntity(stalker);
                world.playSound(null, spawnPos, SoundEvents.ENTITY_ENDERMAN_SCREAM, SoundCategory.HOSTILE, 1.5F, 0.7F);
                player.addEffect(new EffectInstance(Effects.BLINDNESS, 20 * 2, 0, false, false));
                player.displayClientMessage(
                    new StringTextComponent("NO TE DES LA VUELTA.").withStyle(TextFormatting.DARK_RED, TextFormatting.BOLD),
                    true
                );
            }
        } else {
            world.playSound(null, pos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 0.7F, 0.5F);
            player.displayClientMessage(
                new StringTextComponent("La luz parece apagarse a tu alrededor...").withStyle(TextFormatting.GRAY, TextFormatting.ITALIC),
                true
            );
        }
    }
}
