package io.github.kunosayo.nestle.client.screen;

import io.github.kunosayo.nestle.client.gui.PlayerListScrollPanel;
import io.github.kunosayo.nestle.client.gui.PlayerNestleInfoList;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class NestleScreen extends Screen {
    public static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation.fromNamespaceAndPath("nestle", "playerlist");
    public static final ResourceLocation ICON_SPRITE = ResourceLocation.fromNamespaceAndPath("nestle", "icons");
    private static final Component EMPTY_TIP = Component.translatable("gui.nestle.empty");

    private int lastCount = -1;
    private static final int SEARCH_BOX_MARGIN_X = 16;
    private static final Component SEARCH_HINT = Component.translatable("gui.socialInteractions.search_hint")
            .withStyle(ChatFormatting.ITALIC)
            .withStyle(ChatFormatting.GRAY);

    private Component titleComponent;
    private StringWidget titleWidget;
    private EditBox searchBox;
    private PlayerListScrollPanel scrollPanel;
    private StringWidget emptyWidget;
    private int startX;
    private int startY;

    public NestleScreen() {
        super(Component.translatable("gui.nestle.title", PlayerNestleInfoList.profileList.size()));
        this.titleComponent = this.getTitle();
    }

    private void checkCount() {
        final int curCount = PlayerNestleInfoList.profileList.size();
        if (curCount != lastCount) {
            updateTitleWidget();
        }
    }

    private void updateTitleWidget() {
        lastCount = PlayerNestleInfoList.profileList.size();
        titleComponent = Component.translatable("gui.nestle.title", lastCount);
        titleWidget.setMessage(titleComponent);
        titleWidget.setWidth(font.width(titleComponent.getVisualOrderText()));
    }

    @Override
    protected void init() {
        super.init();

        startX = (this.width - 250) >> 1;
        startY = (this.height - 250) >> 1;
        final int SEARCH_BOX_MARGIN_Y_TO_START = 24;
        final int SCROLL_MARGIN_Y = 8;
        if (searchBox == null) {
            searchBox = new EditBox(this.font, startX + SEARCH_BOX_MARGIN_X, startY + SEARCH_BOX_MARGIN_Y_TO_START, 250 - (SEARCH_BOX_MARGIN_X << 1), 15, SEARCH_HINT);
            searchBox.setResponder(PlayerNestleInfoList::setFilter);
        } else {
            PlayerNestleInfoList.setFilter(this.searchBox.getValue());
        }
        if (scrollPanel == null) {
            scrollPanel = new PlayerListScrollPanel(Minecraft.getInstance(),
                    250 - 16,
                    250 - searchBox.getHeight() - SCROLL_MARGIN_Y - SEARCH_BOX_MARGIN_Y_TO_START - 6,
                    searchBox.getY() + searchBox.getHeight() + SCROLL_MARGIN_Y, startX + 8, this.font);
        }

        titleWidget = new StringWidget(startX + 8, startY + 6, 0, 9, Component.empty(), this.font);
        updateTitleWidget();
        this.addRenderableWidget(titleWidget);
        this.addRenderableWidget(searchBox);
        this.addRenderableWidget(scrollPanel);

        final int emptyTextWidth = font.width(EMPTY_TIP.getVisualOrderText());
        emptyWidget = new StringWidget(startX + (250 - emptyTextWidth) / 2,
                startY + 125 - font.lineHeight / 2,
                emptyTextWidth, font.lineHeight,
                EMPTY_TIP, font);

        PlayerNestleInfoList.setDirty();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        checkCount();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (PlayerNestleInfoList.profileList.isEmpty()) {
            emptyWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.blitSprite(BACKGROUND_SPRITE, 250, 310, 0, 0,
                startX, startY, 250, 250);
    }
}
