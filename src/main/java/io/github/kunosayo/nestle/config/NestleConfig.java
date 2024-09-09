package io.github.kunosayo.nestle.config;

import io.github.kunosayo.nestle.data.CloseNestleValue;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NestleConfig {
    public static final Pair<NestleConfig, ModConfigSpec> NESTLE_CONFIG = new ModConfigSpec.Builder()
            .configure(NestleConfig::new);
    public final ModConfigSpec.ConfigValue<Integer> farAwayNestleValue;
    public final ModConfigSpec.ConfigValue<Integer> damageApportionRequire;
    public final ModConfigSpec.ConfigValue<Integer> damagePlayerValueReduce;
    public final ModConfigSpec.ConfigValue<Integer> nestleRadius;
    public final ModConfigSpec.ConfigValue<Integer> nestleFreeRequire;
    public final ModConfigSpec.ConfigValue<List<? extends String>> nestleValues;
    /**
     * Distance - Value
     */
    public final ArrayList<CloseNestleValue> closeNestleValues = new ArrayList<>();

    NestleConfig(ModConfigSpec.Builder builder) {
        farAwayNestleValue = builder
                .comment("The nestle value get from different world or far away.")
                .define("far_away_nestle_value", 1);
        damageApportionRequire = builder.comment("The nestle value to apportion the damage")
                .define("damage_apportion_require", 720000);
        nestleFreeRequire = builder.comment("The nestle value to nestle freely")
                .define("nestle_free_require", 720000);
        // about half hour when 5m
        damagePlayerValueReduce = builder.comment("The nestle value to minus if damage player and the value to add if nestle player")
                .define("damage_apportion_require", 360000);
        nestleRadius = builder.comment("The radius to nestle")
                .define("nestle_radius", 5);
        nestleValues = builder.comment("The nestle value get if in the distance\nFormat: distance:nestle_value")
                .defineList("nestle_values", new ArrayList<>() {{
                    add("5:200");
                    add("50:100");
                    add("500:50");
                    add("5000:10");
                }}, o -> {
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
    }
}
