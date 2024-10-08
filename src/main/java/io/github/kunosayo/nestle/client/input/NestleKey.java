package io.github.kunosayo.nestle.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.kunosayo.nestle.Nestle;
import io.github.kunosayo.nestle.client.gui.PlayerNestleInfoList;
import io.github.kunosayo.nestle.config.NestleConfig;
import io.github.kunosayo.nestle.item.NestleItem;
import io.github.kunosayo.nestle.network.NestlePacket;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT, modid = Nestle.MOD_ID)
public class NestleKey {
    public static final KeyMapping NESTLE_KEY = new KeyMapping("key.nestle.desc",
            KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z,
            "key.category.nestle");

    @SubscribeEvent
    public static void onKeyboardInput(InputEvent.Key event) {
        if (isInGame() && event.getAction() == GLFW.GLFW_PRESS && NESTLE_KEY.matches(event.getKey(), event.getScanCode())) {

            var player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }

            Player targetPlayer = NestleItem.getPlayerPointingAt(player, player.level());
            if (targetPlayer == null) {
                return;
            }

            if (PlayerNestleInfoList.clientNestleData.getValue(targetPlayer.getUUID()).getValue()
                    >= NestleConfig.NESTLE_CONFIG.getLeft().nestleFreeRequire.get()) {
                PacketDistributor.sendToServer(new NestlePacket(targetPlayer.getUUID()));
            }
        }
    }

    private static boolean isInGame() {
        Minecraft mc = Minecraft.getInstance();
        // 不能是加载界面
        if (mc.getOverlay() != null) {
            return false;
        }
        // 不能打开任何 GUI
        if (mc.screen != null) {
            return false;
        }
        // 当前窗口捕获鼠标操作
        if (!mc.mouseHandler.isMouseGrabbed()) {
            return false;
        }
        // 选择了当前窗口
        return mc.isWindowActive();
    }
}
