package io.github.kunosayo.nestle.client.gui;

import com.mojang.authlib.GameProfile;
import io.github.kunosayo.nestle.data.NestleValue;
import io.github.kunosayo.nestle.entity.data.NestleData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.block.entity.SkullBlockEntity;

import java.util.*;

public class PlayerNestleInfoList {
    public static final HashMap<UUID, Integer> indexMap = new HashMap<>();
    public static List<PlayerNestleInfo> profileList = new ArrayList<>();
    public static List<Integer> displayOrder = new ArrayList<>();
    /**
     * The the filtered index not be included in this list.
     */
    public static List<Integer> remainIndex = new ArrayList<>();

    private static int filteredCount = 0;
    private static String filter = "";

    private static boolean dirty = false;


    private PlayerNestleInfoList() {
    }

    public static void updatePlayer(UUID playerUUID, NestleValue nestleValue) {
        int idx = indexMap.computeIfAbsent(playerUUID, uuid -> {

            var info = new PlayerNestleInfo(new GameProfile(playerUUID, playerUUID.toString()), nestleValue);
            profileList.add(info);
            displayOrder.add(profileList.size() - 1);
            remainIndex.add(profileList.size() - 1);

            Optional.ofNullable(Minecraft.getInstance().player)
                    .flatMap(localPlayer -> localPlayer.connection.getOnlinePlayers().stream()
                            .filter(playerInfo -> playerInfo.getProfile().getId().equals(playerUUID))
                            .findAny()
                    )
                    .map(PlayerInfo::getProfile)
                    .ifPresentOrElse(info::setGameProfile,
                            () -> SkullBlockEntity.fetchGameProfile(playerUUID)
                                    .thenAcceptAsync(gameProfile -> gameProfile.ifPresent(info::setGameProfile)));


            return profileList.size() - 1;
        });

        var info = profileList.get(idx);
        info.nestleValue = nestleValue;
        dirty = true;
    }

    public static void setFilter(String filter) {

        if (!filter.equals(PlayerNestleInfoList.filter)) {
            PlayerNestleInfoList.filter = filter;
            for (PlayerNestleInfo playerNestleInfo : profileList) {
                playerNestleInfo.checkFilter();
            }
            remainIndex.clear();
            for (int i = 0; i < profileList.size(); i++) {
                if (!profileList.get(i).filtered) {
                    remainIndex.add(i);
                }
            }
        }
    }

    public static int getFilteredCount() {
        return filteredCount;
    }

    public static void clear() {
        profileList.clear();
        indexMap.clear();
        displayOrder.clear();
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

        displayOrder.sort((o1, o2) -> {
            var a = profileList.get(o1);
            var b = profileList.get(o2);


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

        public void checkFilter() {
            boolean newFilter = !filter.isEmpty() && !gameProfile.getName().toLowerCase().contains(filter);
            if (newFilter != filtered) {
                filtered = newFilter;
                if (filtered) {
                    ++filteredCount;
                } else {
                    --filteredCount;
                }
            }
        }
    }
}
