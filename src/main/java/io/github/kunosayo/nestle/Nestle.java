package io.github.kunosayo.nestle;

import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.data.NestleValue;
import io.github.kunosayo.nestle.entity.data.NestleData;
import io.github.kunosayo.nestle.init.*;
import io.github.kunosayo.nestle.network.SyncNestleDataPacket;
import io.github.kunosayo.nestle.network.UpdateNestleValuePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

@Mod(Nestle.MOD_ID)
public class Nestle {
    public static final String MOD_ID = "nestle";
    @Nullable
    public static BlockPos clientNearestEntityVec;

    private long lastCalcTime = System.currentTimeMillis();

    public Nestle(IEventBus modEventBus, ModContainer modContainer) {
        ModItems.ITEMS.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);

        // Effects (and potions)
        ModEffects.MOB_EFFECTS.register(modEventBus);
        ModEffects.POTIONS.register(modEventBus);


        ModBlocks.BLOCKS.register(modEventBus);
        ModCreativeTab.TABS.register(modEventBus);
        ModData.ATTACHMENT_TYPES.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);

        modContainer.registerConfig(ModConfig.Type.SERVER, NestleConfig.NESTLE_CONFIG.getRight());
    }


    @SubscribeEvent
    public void onRegisterBrewingRecipe(RegisterBrewingRecipesEvent event) {
        var builder = event.getBuilder();

        builder.addMix(Potions.THICK, ModItems.NESTLE.asItem(), ModEffects.NESTLE_POTION);
        builder.addMix(ModEffects.NESTLE_POTION, Items.SLIME_BALL, ModEffects.DESIRE_NESTLE_POTION); // 粘液球
        builder.addMix(ModEffects.NESTLE_POTION, Items.CACTUS, ModEffects.NESTLE_RESISTANCE_POTION); // 仙人掌
        builder.addMix(ModEffects.NESTLE_RESISTANCE_POTION, Items.REDSTONE, ModEffects.LONG_NESTLE_RESISTANCE_POTION); // 红石
    }

    private void calcNestleValue(MinecraftServer server) {
        var players = server.getPlayerList().getPlayers();
        for (int i = 0; i < players.size(); ++i) {
            var a = players.get(i);
            var com = a.getData(NestleData.ATTACHMENT_TYPE);
            var updatePacket = new UpdateNestleValuePacket();
            for (int j = 0; j < players.size(); ++j) {
                if (i == j) {
                    continue;
                }
                var b = players.get(j);

                int delta;
                if (a.level() != b.level()) {
                    delta = NestleConfig.NESTLE_CONFIG.getLeft().farAwayNestleValue.get();
                    com.addDifValue(b.getUUID(), delta);
                    updatePacket.getDifferentWorld().add(new UpdateNestleValuePacket.DifferentWorldUpdate(b.getUUID(), delta));
                } else {
                    double disSqr = a.position().distanceToSqr(b.position());
                    delta = NestleConfig.NESTLE_CONFIG.getLeft().getValueFromDistance((long) Math.ceil(disSqr));
                    int idx = NestleValue.getIndex(disSqr);
                    com.addValue(b.getUUID(), delta, idx);
                    updatePacket.getSameWorld().add(new UpdateNestleValuePacket.SameWorldUpdate(b.getUUID(), delta, idx));
                }
            }
            if (!updatePacket.getSameWorld().isEmpty() || !updatePacket.getDifferentWorld().isEmpty()) {
                PacketDistributor.sendToPlayer(a, updatePacket);
            }
        }

    }


    @SubscribeEvent
    public void onServerPostTick(ServerTickEvent.Post event) {
        long now = System.currentTimeMillis();
        if (lastCalcTime - now >= 5000) {
            lastCalcTime = now;
        }
        if (now - lastCalcTime >= 1000) {
            calcNestleValue(event.getServer());
            lastCalcTime += 1000;
            if (lastCalcTime + 250 < now) {
                lastCalcTime = now;
            }
        }
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            PacketDistributor.sendToPlayer(sp, new SyncNestleDataPacket(sp.getData(NestleData.ATTACHMENT_TYPE)));
        }
    }

}
