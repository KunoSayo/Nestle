package io.github.kunosayo.nestle.client.gui;

import com.mojang.authlib.GameProfile;
import io.github.kunosayo.nestle.client.task.SingleTask;
import io.github.kunosayo.nestle.data.NestleValue;
import io.github.kunosayo.nestle.entity.data.NestleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayerResolver;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.*;
import java.util.concurrent.ExecutionException;

public final class PlayerNestleInfoList {
    public static final HashMap<UUID, PlayerNestleInfo> infoMap = new HashMap<>();
    public static NestleData clientNestleData = new NestleData();
    public static List<PlayerNestleInfo> profileList = new ArrayList<>();
    public static HashMap<UUID, GameProfile> profileCache = new HashMap<>();

    private static int filteredCount = 0;
    private static String filter = "";

    private static boolean dirty = false;


    private PlayerNestleInfoList() {
    }

    public static void updatePlayer(UUID playerUUID, NestleValue nestleValue) {
        var playerNestleInfo = infoMap.computeIfAbsent(playerUUID, uuid -> {

            var info = new PlayerNestleInfo(new GameProfile(playerUUID, playerUUID.toString()), nestleValue);
            profileList.add(info);

            info.checkFetch();
            return info;
        });

        playerNestleInfo.setNestleValue(nestleValue);
        dirty = true;
    }

    public static void setFilter(String filter) {
        filter = filter.toLowerCase();
        if (!filter.equals(PlayerNestleInfoList.filter)) {
            PlayerNestleInfoList.filter = filter;
            int lastFilteredIndex = -1;
            for (int i = 0; i < profileList.size(); i++) {
                var info = profileList.get(i);
                dirty |= info.checkFilter();
                if (info.filtered) {
                    // this is filtered, record
                    if (lastFilteredIndex == -1) {
                        lastFilteredIndex = i;
                    }
                } else if (lastFilteredIndex != -1) {
                    // have filtered info before this
                    // swap the two to here
                    Collections.swap(profileList, lastFilteredIndex, i);
                    // The next must be filtered.
                    ++lastFilteredIndex;
                }
            }

        }
    }


    public static int getFilteredCount() {
        return filteredCount;
    }

    public static void clear() {
        for (PlayerNestleInfo playerNestleInfo : profileList) {
            if (playerNestleInfo.isGameProfileValid()) {
                profileCache.put(playerNestleInfo.gameProfile.id(), playerNestleInfo.gameProfile);
            }
        }
        profileList.clear();
        infoMap.clear();
        filteredCount = 0;
    }

    public static void syncNew() {
        clear();

        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }

        var nestleValue = clientNestleData;


        nestleValue.values.forEach(PlayerNestleInfoList::updatePlayer);

        profileCache.clear();
        checkDirty();
    }


    public static void checkDirty() {
        if (!dirty) {
            return;
        }
        dirty = false;
        var level = Minecraft.getInstance().level;
        var player = Minecraft.getInstance().player;
        if (level == null || player == null) {
            // hard to think.
            return;
        }


        var onlines = new HashSet<>();

        player.connection.getListedOnlinePlayers().forEach(playerInfo -> {
            onlines.add(playerInfo.getProfile().id());
        });


        profileList.subList(0, profileList.size() - filteredCount).sort((a, b) -> {


            final boolean aSame = level.getPlayerByUUID(a.gameProfile.id()) != null;
            final boolean bSame = level.getPlayerByUUID(b.gameProfile.id()) != null;

            if (aSame && !bSame) {
                return -1;
            }
            if (bSame && !aSame) {
                return 1;
            }

            final boolean aOnline = onlines.contains(a.gameProfile.id());
            final boolean bOnline = onlines.contains(b.gameProfile.id());
            if (aOnline && !bOnline) {
                return -1;
            }
            if (bOnline && !aOnline) {
                return 1;
            }

            return Long.compare(b.nestleValue.getValue(), a.nestleValue.getValue());
        });


    }

    public static int getRemainCount() {
        return profileList.size() - filteredCount;
    }

    public static void setDirty() {
        dirty = true;
    }

    public static void removePlayer(UUID uuid) {
        dirty |= profileList.removeIf(playerNestleInfo -> {
            if (playerNestleInfo.gameProfile.id().equals(uuid)) {
                if (playerNestleInfo.filtered) {
                    --filteredCount;
                }
                return true;
            }
            return false;
        });
        dirty |= infoMap.remove(uuid) != null;
    }


    public static class PlayerNestleInfo {
        public GameProfile gameProfile;
        public double[] percents = new double[18];
        public double[] totalPercents = new double[18];
        public boolean filtered = false;
        private NestleValue nestleValue;
        private boolean dirty = true;

        public PlayerNestleInfo(GameProfile gameProfile, NestleValue nestleValue) {
            this.gameProfile = gameProfile;
            this.nestleValue = nestleValue;
        }


        public void setGameProfile(GameProfile gameProfile) {
            Minecraft.getInstance().execute(() -> {
                this.gameProfile = gameProfile;
                checkFilter();
            });

        }

        public boolean isGameProfileValid() {
            return !this.gameProfile.name().equalsIgnoreCase(this.gameProfile.id().toString());
        }

        public boolean checkFilter() {
            boolean newFilter = !filter.isEmpty() && !gameProfile.name().toLowerCase().contains(filter);
            if (newFilter != filtered) {
                filtered = newFilter;
                if (filtered) {
                    ++filteredCount;
                } else {
                    --filteredCount;
                }
                return true;
            }
            return false;
        }

        public void checkRenderDirty() {
            if (!dirty) {
                return;
            }
            dirty = false;

            long max = 1;
            long total = 0;

            for (int i = 0; i < 18; i++) {
                max = Math.max(this.nestleValue.times[i], max);
                total += this.nestleValue.times[i];
            }

            total = Math.max(total, 1);
            for (int i = 0; i < 18; i++) {
                percents[i] = this.nestleValue.times[i] * 1.0 / max;
                totalPercents[i] = this.nestleValue.times[i] * 1.0 / total;
            }

        }

        public NestleValue getNestleValue() {
            return nestleValue;
        }

        public void setNestleValue(NestleValue nestleValue) {
            this.nestleValue = nestleValue;
            this.dirty = true;
        }

        public void checkFetch() {
            if (!isGameProfileValid()) {
                class RetryFetch implements Runnable {
                    int count = 0;

                    @Override
                    public void run() {

                        if (++count > 3 || !PlayerNestleInfo.this.gameProfile.name().equalsIgnoreCase(gameProfile.id().toString())) {
                            return;
                        }
                        Minecraft.getInstance().services().profileResolver().fetchById(gameProfile.id())
                                .filter(playerInfo -> !playerInfo.name().equalsIgnoreCase(PlayerNestleInfo.this.gameProfile.id().toString()))
                                .ifPresentOrElse(PlayerNestleInfo.this::setGameProfile, () -> {
                                    try {
                                        Thread.sleep(50 + (long) (Math.random() * 1000));
                                    } catch (InterruptedException ignored) {

                                    }
                                    this.run();
                                });

                    }
                }

                Optional.ofNullable(Minecraft.getInstance().player)
                        .flatMap(localPlayer -> localPlayer.connection.getOnlinePlayers().stream()
                                .filter(playerInfo -> playerInfo.getProfile().id().equals(this.gameProfile.id()))
                                .findAny()
                        )
                        .map(PlayerInfo::getProfile)
                        .or(() -> Optional.ofNullable(profileCache.get(this.gameProfile.id())))
                        // not uuid
                        .filter(tgp -> !tgp.name().equalsIgnoreCase(gameProfile.id().toString()))
                        .ifPresentOrElse(PlayerNestleInfo.this::setGameProfile, () -> SingleTask.INSTANCE.submitTask(new RetryFetch()));

            }

        }
    }
}
