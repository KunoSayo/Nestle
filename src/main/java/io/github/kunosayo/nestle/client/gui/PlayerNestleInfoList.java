package io.github.kunosayo.nestle.client.gui;

import com.mojang.authlib.GameProfile;
import io.github.kunosayo.nestle.data.NestleValue;
import io.github.kunosayo.nestle.entity.data.NestleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.*;

public final class PlayerNestleInfoList {
    public static final HashMap<UUID, PlayerNestleInfo> infoMap = new HashMap<>();
    public static List<PlayerNestleInfo> profileList = new ArrayList<>();


    private static int filteredCount = 0;
    private static String filter = "";

    private static boolean dirty = false;


    private PlayerNestleInfoList() {
    }

    public static void updatePlayer(UUID playerUUID, NestleValue nestleValue) {
        var playerNestleInfo = infoMap.computeIfAbsent(playerUUID, uuid -> {

            var info = new PlayerNestleInfo(new GameProfile(playerUUID, playerUUID.toString()), nestleValue);
            profileList.add(info);

            Optional.ofNullable(Minecraft.getInstance().player)
                    .flatMap(localPlayer -> localPlayer.connection.getOnlinePlayers().stream()
                            .filter(playerInfo -> playerInfo.getProfile().getId().equals(playerUUID))
                            .findAny()
                    )
                    .map(PlayerInfo::getProfile)
                    .ifPresentOrElse(info::setGameProfile,
                            () -> SkullBlockEntity.fetchGameProfile(playerUUID)
                                    .thenAcceptAsync(gameProfile -> gameProfile.ifPresent(info::setGameProfile)));


            return info;
        });

        playerNestleInfo.nestleValue = nestleValue;
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
                    lastFilteredIndex = i;
                }
            }

        }
    }



    public static int getFilteredCount() {
        return filteredCount;
    }

    public static void clear() {
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

        var nestleValue = player.getData(NestleData.ATTACHMENT_TYPE);


        nestleValue.values.forEach(PlayerNestleInfoList::updatePlayer);

        if (Minecraft.getInstance().isSingleplayer()) {
            updatePlayer(player.getUUID(), new NestleValue());
        }

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
            onlines.add(playerInfo.getProfile().getId());
        });


        profileList.subList(0, profileList.size() - filteredCount).sort((a, b) -> {


            final boolean aSame = level.getPlayerByUUID(a.gameProfile.getId()) != null;
            final boolean bSame = level.getPlayerByUUID(b.gameProfile.getId()) != null;

            if (aSame && !bSame) {
                return -1;
            }
            if (bSame && !aSame) {
                return 1;
            }

            final boolean aOnline = onlines.contains(a.gameProfile.getId());
            final boolean bOnline = onlines.contains(b.gameProfile.getId());
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


    public static class PlayerNestleInfo {
        public GameProfile gameProfile;
        public NestleValue nestleValue;
        public boolean filtered = false;

        public PlayerNestleInfo(GameProfile gameProfile, NestleValue nestleValue) {
            this.gameProfile = gameProfile;
            this.nestleValue = nestleValue;
        }


        public void setGameProfile(GameProfile gameProfile) {
            this.gameProfile = gameProfile;
            checkFilter();
        }

        public boolean checkFilter() {
            boolean newFilter = !filter.isEmpty() && !gameProfile.getName().toLowerCase().contains(filter);
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
    }
}
