package io.github.kunosayo.nestle.client.screen;

import io.github.kunosayo.nestle.client.gui.PlayerListScrollPanel;
import io.github.kunosayo.nestle.client.gui.PlayerNestleInfoList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

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
        final int highSize = 181 - 48 - 1;
        final int barWidth = 12;
        final int barInterval = barWidth + 2;

        int leftX = startX + 25;

        long totalTimes = 0;
        for (int i = 0; i < 18; i++) {

            int highY = bottomY - (int) Math.round(highSize * info.percents[i]) - 1;

            if (info.percents[i] == 0.0) {
                guiGraphics.fill(leftX, highY, leftX + barWidth, bottomY, 0xff333333);
            } else {
                guiGraphics.fill(leftX, highY, leftX + barWidth, bottomY, 0xff9c2c2c);
            }


            if (leftX <= mouseX && mouseX <= leftX + barWidth) {
                if (highY <= mouseY && mouseY <= bottomY + 10) {
                    // show tooltip
                    String percent = String.format("%.2f", info.totalPercents[i] * 100);
                    Component msg;
                    if (i < 16) {
                        msg = Component.translatable("tooltip.nestle.stat.normal", info.getNestleValue().times[i], percent, String.valueOf(1 << i));
                    } else if (i == 16) {
                        msg = Component.translatable("tooltip.nestle.stat.far", info.getNestleValue().times[i], percent, String.valueOf(1 << 15));
                    } else {
                        // i is 17, different world.
                        msg = Component.translatable("tooltip.nestle.stat.different", info.getNestleValue().times[i], percent);
                    }
                    guiGraphics.renderTooltip(this.font, msg, mouseX, mouseY);
                }
            }
            totalTimes += info.getNestleValue().times[i];

            leftX += barInterval;
        }

        // draw nestle value text
        var nestleText = String.valueOf(info.getNestleValue().getValue());
        final int textWidth = font.width(nestleText);
        final int nestleTextX = startX + 300 - 16 - textWidth;
        guiGraphics.drawString(font, nestleText,
                nestleTextX, textY, 0xffffffff);

        // draw seconds text
        var secText = totalTimes + "s";
        final int secTextWidth = font.width(secText);
        guiGraphics.drawString(font, secText, startX + 300 - 8 - secTextWidth - 1, startY + 43, 0xffffffff);


        {
            // draw Y-axis value
            final int highY = bottomY - highSize - 1;
            if (highY <= mouseY && mouseY <= bottomY) {
                // show tooltip
                int delta = mouseY - highY;
                final String percent = String.format("%d%%", Math.round(Math.max(100.0 - delta * 100.0 / highSize, 0.0)));


                guiGraphics.drawString(font, percent, startX + 8, mouseY - font.lineHeight / 2, 0xffffffff);

                guiGraphics.hLine(startX + 23, startX + 300 - 8, mouseY, 0x7f333333);
            }
        }

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
