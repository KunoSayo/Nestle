package io.github.kunosayo.nestle.init;

import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.effect.DesireNestleEffect;
import io.github.kunosayo.nestle.effect.NestleEffect;
import io.github.kunosayo.nestle.effect.NestleResistanceEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Nestle.MOD_ID);
    public static final DeferredHolder<MobEffect, NestleEffect> NESTLE_EFFECT = MOB_EFFECTS.register("nestle", () -> new NestleEffect(
            //Can be either BENEFICIAL, NEUTRAL or HARMFUL. Used to determine the potion tooltip color of this effect.
            MobEffectCategory.NEUTRAL,
            //The color of the effect particles.
            0xac3232
    ));

    public static final DeferredHolder<MobEffect, DesireNestleEffect> DESIRE_NESTLE_EFFECT = MOB_EFFECTS.register("desire_nestle", () -> new DesireNestleEffect(
            //Can be either BENEFICIAL, NEUTRAL or HARMFUL. Used to determine the potion tooltip color of this effect.
            MobEffectCategory.NEUTRAL,
            //The color of the effect particles.
            0xd95763
    ));

    public static final DeferredHolder<MobEffect, NestleResistanceEffect> NESTLE_RESISTANCE_EFFECT = MOB_EFFECTS.register("nestle_resistance", () -> new NestleResistanceEffect(
            MobEffectCategory.NEUTRAL,
            0x85C1E9
    ));


    public static DeferredRegister<Potion> POTIONS = DeferredRegister.create(Registries.POTION, Nestle.MOD_ID);


    public static final DeferredHolder<Potion, Potion> NESTLE_POTION = POTIONS.register("nestle_potion", () -> new Potion(new MobEffectInstance(NESTLE_EFFECT, 5 * 20)));
    public static final DeferredHolder<Potion, Potion> DESIRE_NESTLE_POTION = POTIONS.register("desire_nestle_potion", () -> new Potion(new MobEffectInstance(DESIRE_NESTLE_EFFECT, 5 * 20)));
    public static final DeferredHolder<Potion, Potion> NESTLE_RESISTANCE_POTION = POTIONS.register("nestle_resistance_potion", () -> new Potion(new MobEffectInstance(NESTLE_RESISTANCE_EFFECT, 3 * 60 * 20)));
    public static final DeferredHolder<Potion, Potion> LONG_NESTLE_RESISTANCE_POTION = POTIONS.register("long_nestle_resistance_potion", () -> new Potion(new MobEffectInstance(NESTLE_RESISTANCE_EFFECT, 8 * 60 * 20)));
}
