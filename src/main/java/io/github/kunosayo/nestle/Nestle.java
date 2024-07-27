package io.github.kunosayo.nestle;

import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.github.kunosayo.nestle.init.ModCreativeTab;
import io.github.kunosayo.nestle.init.ModData;
import io.github.kunosayo.nestle.init.ModEffects;
import io.github.kunosayo.nestle.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import javax.annotation.Nullable;

@Mod(Nestle.MOD_ID)
public class Nestle {
    public static final String MOD_ID = "nestle";
    @Nullable
    public static BlockPos clientNearestEntityVec;

    private long lastCalcTime = System.currentTimeMillis();

    public Nestle(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);

        // Effects (and potions)
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModEffects.POTIONS.register(modEventBus);
        ModCreativeTab.TABS.register(modEventBus);
        ModData.ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.SERVER, NestleConfig.NESTLE_CONFIG.getRight());
    }


    @SubscribeEvent
    public void onRegisterBrewingRecipe(RegisterBrewingRecipesEvent event) {
        var builder = event.getBuilder();

        builder.addMix(Potions.THICK, ModItems.NESTLE.asItem(), ModEffects.NESTLE_POTION);
        builder.addMix(ModEffects.NESTLE_POTION, Items.FERMENTED_SPIDER_EYE, ModEffects.DESIRE_NESTLE_POTION);
    }

    private void calcNestleValue(MinecraftServer server) {
        var players = server.getPlayerList().getPlayers();
        for (int i = 0; i < players.size(); ++i) {
            var a = players.get(i);
            var com = a.getData(NestleData.ATTACHMENT_TYPE);
            for (int j = 0; j < players.size(); ++j) {
                if (i == j) {
                    continue;
                }
                var b = players.get(j);

                int delta;
                if (a.level() != b.level()) {
                    delta = NestleConfig.NESTLE_CONFIG.getLeft().farAwayNestleValue.get();
                } else {
                    delta = NestleConfig.NESTLE_CONFIG.getLeft().getValueFromDistance((long) Math.ceil(a.position().distanceToSqr(b.position())));
                }

                com.addValue(b.getUUID(), delta);
            }
        }

    }


    @SubscribeEvent
    public void onServerPostTick(ServerTickEvent.Post event) {
        long now = System.currentTimeMillis();
        if (now - lastCalcTime >= 1000) {
            calcNestleValue(event.getServer());
            lastCalcTime += 1000;
            if (lastCalcTime + 1000 < now) {
                lastCalcTime = now;
            }
        }
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.Post event) {
        var player = Minecraft.getInstance().player;
        if (player != null) {
            // found the nearest entity
            var entity = player.level().getNearestEntity(LivingEntity.class,
                    TargetingConditions.forNonCombat().ignoreLineOfSight(),
                    player, player.getX(), player.getY(), player.getZ(),
                    new AABB(-500.0 + player.getX(), -256.0 + player.getY(), -500.0 + player.getZ(),
                            500.0 + player.getX(), 256.0 + player.getY(), 500.0 + player.getZ()));
            clientNearestEntityVec = entity == null ? null : entity.blockPosition();
        }
    }


}
