package io.github.kunosayo.nestle.client.screen;

import io.github.kunosayo.nestle.client.gui.PlayerListScrollPanel;
import io.github.kunosayo.nestle.client.gui.PlayerNestleInfoList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class NestleDetailScreen extends Screen {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath("nestle", "detail_chart");
    private static final Component TITLE = Component.translatable("gui.nestle_detailed.title");

    private final PlayerNestleInfoList.PlayerNestleInfo info;
    private final Screen lastScreen;
    private int startX;
    private int startY;

    public NestleDetailScreen(PlayerNestleInfoList.PlayerNestleInfo info) {
        super(TITLE);
        this.info = info;
        lastScreen = Minecraft.getInstance().screen;
    }

    @Override
    protected void init() {
        super.init();
        startX = (this.width - 300) >> 1;
        startY = (this.height - 200) >> 1;
        this.addRenderableWidget(new StringWidget(startX + 8, startY - 12, font.width(TITLE.getVisualOrderText()), 9, TITLE, this.font));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        final int headY = startY + 8;
        PlayerListScrollPanel.renderPlayerAvatar(info.gameProfile, startX + 8, headY, guiGraphics);

        final int textY = startY + 8 + (32 - font.lineHeight) / 2;
        guiGraphics.drawString(this.font, info.gameProfile.getName(),
                startX + 8 + 32 + 4, textY,
                0xffffffff);

        info.checkRenderDirty();

        final int bottomY = startY + 181;
        final int highSize = 181 - 48;
        final int barWidth = 12;
        final int barInterval = barWidth + 2;

        int leftX = startX + 25;

        for (int i = 0; i < 18; i++) {

            int highY = bottomY - (int) Math.round(highSize * info.percents[i]);

            guiGraphics.fill(leftX, highY, leftX + barWidth, bottomY, 0xffff0000);


            if (leftX <= mouseX && mouseX <= leftX + barWidth) {
                if (highY <= mouseY && mouseY <= bottomY + 10) {
                    // show tooltip

                    guiGraphics.renderTooltip(this.font, List.of(
                                    Component.literal(String.format("%d (%.2f%)", info.getNestleValue().times[i], info.percents[i] * 100))
                            ),
                            Optional.empty(), mouseX, mouseY);
                }
            }

            leftX += barInterval;
        }

        var nestleText = String.valueOf(info.getNestleValue().getValue());
        final int textWidth = font.width(nestleText);

        final int nestleTextX = startX + 300 - 16 - textWidth;

        guiGraphics.drawString(font, nestleText,
                nestleTextX, textY, 0xffffffff);

        // render :heart:
        guiGraphics.blitSprite(NestleScreen.ICON_SPRITE, 96, 16, 16, 0,
                nestleTextX - 16 - 2, headY + 8, 16, 16);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blitSprite(BACKGROUND_SPRITE, startX, startY, 300, 200);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(lastScreen);
    }
}
