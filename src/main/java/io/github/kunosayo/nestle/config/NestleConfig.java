package io.github.kunosayo.nestle.config;

import io.github.kunosayo.nestle.data.CloseNestleValue;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class NestleConfig {
    public static final Pair<NestleConfig, ModConfigSpec> NESTLE_CONFIG = new ModConfigSpec.Builder()
            .configure(NestleConfig::new);
    public final ModConfigSpec.ConfigValue<Integer> farAwayNestleValue;
    public final ModConfigSpec.ConfigValue<Integer> damageApportionRequire;
    public final ModConfigSpec.ConfigValue<Integer> damagePlayerValueReduce;
    public final ModConfigSpec.ConfigValue<Integer> nestleRadius;
    public final ModConfigSpec.ConfigValue<Integer> nestleFreeRequire;
    public final ModConfigSpec.ConfigValue<Double> boundDamageValueScale;
    public final ModConfigSpec.ConfigValue<List<? extends String>> entitiesNotSpreadDamageByDefault;
    public final ModConfigSpec.ConfigValue<List<? extends String>> nestleLeadAvoidEntities;
    public final ModConfigSpec.ConfigValue<List<? extends String>> nestleValues;
    /**
     * Distance - Value
     */
    public final ArrayList<CloseNestleValue> closeNestleValues = new ArrayList<>();
    public Set<EntityType<?>> entitiesNotSpreadDamageByDefaultSet = new HashSet<>();
    public Set<EntityType<?>> nestleLeadAvoidEntitiesSet = new HashSet<>();

    NestleConfig(ModConfigSpec.Builder builder) {
        farAwayNestleValue = builder
                .comment("The nestle value get from different world or far away.")
                .define("far_away_nestle_value", 1);
        damageApportionRequire = builder.comment("The nestle value to apportion the damage")
                .define("damage_apportion_require", 720000);
        boundDamageValueScale = builder.comment("The nestle value to reduce per spread damage by nestle bound")
                .define("bound_damage_value_scale", 1.0);
        nestleFreeRequire = builder.comment("The nestle value to nestle freely")
                .define("nestle_free_require", 720000);
        // about half hour when 5m
        damagePlayerValueReduce = builder.comment("The nestle value to minus if damage player and the value to add if nestle player")
                .define("damage_player_value_reduce", 360000);
        nestleRadius = builder.comment("The radius to nestle")
                .define("nestle_radius", 5);
        nestleValues = builder.comment("The nestle value get if in the distance\nFormat: distance:nestle_value")
                .defineList("nestle_value_by_distance", new ArrayList<>() {{
                    // 2 hours
                    add("1:100");
                    // 4 hours
                    add("2:50");
                    add("5:45");
                    add("10:40");
                    add("17:35");
                    add("65:30");
                    // 8 hours
                    add("129:25");
                    add("513:20");
                    add("1025:15");
                    add("2049:10");
                    add("4097:5");
                }}, () -> "", o -> {
                    if (o instanceof String s) {
                        String[] args = s.split(":", 2);
                        if (args.length == 2) {
                            try {
                                Integer.parseInt(args[0]);
                                Integer.parseInt(args[1]);
                                return true;
                            } catch (NumberFormatException ignored) {

                            }
                        }
                    }
                    return false;
                });
        entitiesNotSpreadDamageByDefault = builder.comment("The entities not to spread damage if no any buff")
                .defineList("entities_not_spread_damage_by_default",
                        new ArrayList<>(),
                        () -> "",
                        o -> {
                            if (o instanceof String s) {
                                return EntityType.byString(s).isPresent();
                            }
                            return false;
                        });

        nestleLeadAvoidEntities = builder.comment("The entities cannot be led by nestle lead")
                .defineList("nestle_lead_avoid_entities",
                        new ArrayList<>(),
                        () -> "",
                        o -> {
                            if (o instanceof String s) {
                                return EntityType.byString(s).isPresent();
                            }
                            return false;
                        });
    }

    public int getValueFromDistance(long distanceSquared) {
        int left = 0;
        int right = closeNestleValues.size();
        int ret = farAwayNestleValue.get();
        while (left < right) {
            int mid = ((right - left) >> 1) + left;
            var value = closeNestleValues.get(mid);
            if ((long) value.distance() * value.distance() < distanceSquared) {
                // we are not in this cfg range.
                left = mid + 1;
            } else {
                // We are in the range
                right = mid;
                ret = closeNestleValues.get(mid).value();
            }
        }
        return ret;
    }

    public void update() {
        closeNestleValues.clear();
        for (String s : nestleValues.get()) {
            String[] args = s.split(":", 2);
            if (args.length == 2) {
                int a = Integer.parseInt(args[0]);
                int b = Integer.parseInt(args[1]);
                closeNestleValues.add(new CloseNestleValue(a, b));
            }
        }
        closeNestleValues.sort(Comparator.comparingInt(CloseNestleValue::distance));

        this.entitiesNotSpreadDamageByDefaultSet = this.entitiesNotSpreadDamageByDefault.get().stream()
                .map(EntityType::byString)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        this.nestleLeadAvoidEntitiesSet = this.nestleLeadAvoidEntities.get().stream()
                .map(EntityType::byString)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }
}
