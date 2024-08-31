package io.github.kunosayo.nestle.client.gui;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.kunosayo.nestle.client.screen.NestleDetailScreen;
import io.github.kunosayo.nestle.client.screen.NestleScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerFaceRenderer;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

import javax.annotation.Nullable;
import java.util.HashSet;

public final class PlayerListScrollPanel extends ScrollPanel {
    private static final int BG_BUTTON_Y_OFFSET = (48 - 20) / 2 - 2;
    private static final int PLAYER_BACKGROUND_HEIGHT = 48;
    private static final int PLAYER_MARGIN_Y = 5;
    private static final int BUTTON_MARGIN_RIGHT = 8;
    private final Font font;


    public PlayerListScrollPanel(Minecraft client, int width, int height, int top, int left, Font font) {
        super(client, width, height, top, left, 0);
        PlayerNestleInfoList.setFilter("");
        this.font = font;
    }

    @Override
    protected int getContentHeight() {
        int displayCount = PlayerNestleInfoList.profileList.size() - PlayerNestleInfoList.getFilteredCount();
        return Math.max(displayCount * PLAYER_BACKGROUND_HEIGHT + (displayCount - 1) * PLAYER_MARGIN_Y, this.bottom - top);
    }


    public static void renderPlayerAvatar(GameProfile profile, int x, int y, GuiGraphics graphics) {
        ResourceLocation skin;
        skin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile).texture();
        PlayerFaceRenderer.draw(graphics, skin, x, y, 32);
    }

    @Override
    protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {
        PlayerNestleInfoList.checkDirty();

        var onlines = new HashSet<>();

        Minecraft.getInstance().player.connection.getListedOnlinePlayers().forEach(playerInfo -> {
            onlines.add(playerInfo.getProfile().getId());
        });

        var selected = getSelectedButton(mouseX, mouseY);
        for (var info : PlayerNestleInfoList.profileList) {
            if (info.filtered) {
                break;
            }

            var gameProfile = info.gameProfile;

            int color = (255 << 24) | (81 << 16) | (107 << 8) | 140;
            int bgBottom = relativeY + PLAYER_BACKGROUND_HEIGHT;
            int bgTop = relativeY;

            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            BufferBuilder backgroundBuffer = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            backgroundBuffer.addVertex(left, bgBottom, 0.0F).setColor(color);
            backgroundBuffer.addVertex(entryRight, bgBottom, 0.0F).setColor(color);
            backgroundBuffer.addVertex(entryRight, bgTop, 0.0F).setColor(color);
            backgroundBuffer.addVertex(left, bgTop, 0.0F).setColor(color);
            BufferUploader.drawWithShader(backgroundBuffer.buildOrThrow());


            renderPlayerAvatar(gameProfile, this.left + 8, relativeY + 8, guiGraphics);

            final int nameX = this.left + 44;
            guiGraphics.drawString(font, gameProfile.getName(), nameX, relativeY + 8, 0xffffffff);


            if (onlines.contains(gameProfile.getId())) {
                guiGraphics.blitSprite(NestleScreen.ICON_SPRITE,
                        96, 16, 0, 0,
                        nameX, relativeY + 24, 16, 16);
            }

            final int BORDER_WIDTH = 6;

            final int ICON_MARGIN_X = 4;

            if (selected == info) {
                guiGraphics.blitSprite(NestleScreen.BACKGROUND_SPRITE,
                        250, 310, 250 - 24, 250 + 26,
                        right - BORDER_WIDTH - 24 - BUTTON_MARGIN_RIGHT, relativeY + BG_BUTTON_Y_OFFSET, 24, 26);
            } else {
                guiGraphics.blitSprite(NestleScreen.BACKGROUND_SPRITE,
                        250, 310, 250 - 24, 250,
                        right - BORDER_WIDTH - 24 - BUTTON_MARGIN_RIGHT, relativeY + BG_BUTTON_Y_OFFSET, 24, 26);
            }

            // render button icon
            guiGraphics.blitSprite(NestleScreen.ICON_SPRITE,
                    96, 16, 32, 0,
                    right - BORDER_WIDTH - 16 - BUTTON_MARGIN_RIGHT - ICON_MARGIN_X, relativeY + 16, 16, 16);

            var nestleText = String.valueOf(info.getNestleValue().getValue());
            final int textWidth = font.width(nestleText);
            final int textY = (PLAYER_BACKGROUND_HEIGHT - font.lineHeight) / 2 + relativeY;

            final int textX = right - BORDER_WIDTH - 24 - BUTTON_MARGIN_RIGHT - 8 - textWidth;

            guiGraphics.drawString(font, nestleText,
                    textX, textY, 0xffffffff);

            // render :heart:
            guiGraphics.blitSprite(NestleScreen.ICON_SPRITE, 96, 16, 16, 0,
                    textX - 16 - 2, relativeY + 16, 16, 16);


            relativeY += PLAYER_MARGIN_Y + PLAYER_BACKGROUND_HEIGHT;
        }

    }

    @Override
    protected boolean clickPanel(double mouseX, double mouseY, int button) {

        var info = getSelectedButton(mouseX + left, mouseY + this.top - (int) this.scrollDistance + border);

        if (info != null) {
            Minecraft.getInstance().setScreen(new NestleDetailScreen(info));
            return true;
        }
        return false;
    }


    @Nullable
    private PlayerNestleInfoList.PlayerNestleInfo getSelectedButton(double mouseX, double mouseY) {

        if (!isMouseOver(mouseX, mouseY)) {
            return null;
        }

        double firstButtonY = top + border - scrollDistance + BG_BUTTON_Y_OFFSET;

        double offset = mouseY - firstButtonY;

        // button interval is 11
        // button height is 26
        // total range is 48

        final int ButtonInterval = 48 + PLAYER_MARGIN_Y;
        if ((offset % ButtonInterval) >= 25) {
            return null;
        }


        if ((offset % ButtonInterval) < 1) {
            return null;
        }

        int idx = (int) (offset / ButtonInterval);

        // button is 20 * 20 inner
        // 24 * 26 in total
        // margin x is 2

        final int buttonLeft = right - 6 - 23 - BUTTON_MARGIN_RIGHT;
        final int buttonRight = buttonLeft + 22;
        if (mouseX < buttonLeft || mouseX > buttonRight) {
            return null;
        }

        int left = PlayerNestleInfoList.getRemainCount();

        if (left <= idx || idx < 0) {
            return null;
        }

        return PlayerNestleInfoList.profileList.get(idx);

    }

    @Override
    public NarrationPriority narrationPriority() {
        // well..?
        return NarrationPriority.NONE;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {

    }


}
