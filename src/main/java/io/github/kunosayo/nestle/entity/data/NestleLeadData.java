package io.github.kunosayo.nestle.entity.data;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashSet;
import java.util.UUID;

public class NestleLeadData {
    public static final AttachmentType<NestleLeadData> ATTACHMENT_TYPE = AttachmentType.builder(NestleLeadData::new).build();

    public HashSet<UUID> targets = new HashSet<>();


    public static void nestleTwo(Player a, Player b) {
        var da = a.getData(ATTACHMENT_TYPE);
        var db = b.getData(ATTACHMENT_TYPE);
        da.targets.add(b.getUUID());
        db.targets.add(a.getUUID());
    }

    public static void nestleTwo(Player a, LivingEntity b) {
        var da = a.getData(ATTACHMENT_TYPE);
        da.targets.add(b.getUUID());
    }

    public static void removeTwo(Player a, Player b) {
        var da = a.getData(ATTACHMENT_TYPE);
        var db = b.getData(ATTACHMENT_TYPE);
        da.targets.remove(b.getUUID());
        db.targets.remove(a.getUUID());

        if (da.targets.isEmpty()) {
            a.removeData(ATTACHMENT_TYPE);
        }
        if (db.targets.isEmpty()) {
            b.removeData(ATTACHMENT_TYPE);
        }
    }


    public static void removeTwo(Player a, LivingEntity b) {
        var da = a.getData(ATTACHMENT_TYPE);
        da.targets.remove(b.getUUID());

        if (da.targets.isEmpty()) {
            a.removeData(ATTACHMENT_TYPE);
        }
    }

    public static boolean isNestle(Player a, Player b) {
        return a.hasData(ATTACHMENT_TYPE) && b.hasData(ATTACHMENT_TYPE)
                && a.getData(ATTACHMENT_TYPE).targets.contains(b.getUUID())
                && b.getData(ATTACHMENT_TYPE).targets.contains(a.getUUID());
    }

    public static boolean isNestle(Player a, LivingEntity b) {
        return a.hasData(ATTACHMENT_TYPE) && a.getData(ATTACHMENT_TYPE).targets.contains(b.getUUID());
    }
}
