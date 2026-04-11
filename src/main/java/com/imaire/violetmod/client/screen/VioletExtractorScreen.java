package com.imaire.violetmod.client.screen;

import com.imaire.violetmod.common.menu.VioletExtractorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class VioletExtractorScreen extends AbstractContainerScreen<VioletExtractorMenu> {

    // GUI dimensions
    private static final int GUI_W = 176;
    private static final int GUI_H = 166;

    // Energy bar (left side)
    private static final int ENERGY_X = 8;
    private static final int ENERGY_Y = 18;
    private static final int ENERGY_W = 8;
    private static final int ENERGY_H = 50;

    // Progress bar (center arrow)
    private static final int PROGRESS_X = 79;
    private static final int PROGRESS_Y = 32;
    private static final int PROGRESS_W = 24;
    private static final int PROGRESS_H = 17;

    // Colours
    private static final int COL_BG_OUTER  = 0xFF3C3C3C;
    private static final int COL_BG_INNER  = 0xFFC6C6C6;
    private static final int COL_ENERGY_BG = 0xFF111111;
    private static final int COL_ENERGY_FG = 0xFF22CC22;
    private static final int COL_PROGRESS_BG = 0xFF888888;
    private static final int COL_PROGRESS_FG = 0xFF8800FF;
    private static final int COL_LABEL     = 0xFF404040;
    private static final int COL_DIVIDER   = 0xFF999999;

    public VioletExtractorScreen(VioletExtractorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth  = GUI_W;
        this.imageHeight = GUI_H;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;

        // ── Outer border ───────────────────────────────────────────────────
        graphics.fill(x, y, x + GUI_W, y + GUI_H, COL_BG_OUTER);
        graphics.fill(x + 1, y + 1, x + GUI_W - 1, y + GUI_H - 1, COL_BG_INNER);

        // ── Divider between machine area and player inventory ──────────────
        graphics.fill(x + 7, y + 76, x + GUI_W - 7, y + 77, COL_DIVIDER);

        // ── Energy bar ────────────────────────────────────────────────────
        // Border
        graphics.fill(x + ENERGY_X - 1, y + ENERGY_Y - 1,
                      x + ENERGY_X + ENERGY_W + 1, y + ENERGY_Y + ENERGY_H + 1,
                      COL_BG_OUTER);
        // Background
        graphics.fill(x + ENERGY_X, y + ENERGY_Y,
                      x + ENERGY_X + ENERGY_W, y + ENERGY_Y + ENERGY_H,
                      COL_ENERGY_BG);
        // Fill (grows upward)
        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int fillH = (int) ((float) menu.getEnergy() / maxE * ENERGY_H);
            if (fillH > 0) {
                graphics.fill(x + ENERGY_X,
                              y + ENERGY_Y + ENERGY_H - fillH,
                              x + ENERGY_X + ENERGY_W,
                              y + ENERGY_Y + ENERGY_H,
                              COL_ENERGY_FG);
            }
        }

        // ── Progress bar (horizontal arrow) ──────────────────────────────
        // Border
        graphics.fill(x + PROGRESS_X - 1, y + PROGRESS_Y - 1,
                      x + PROGRESS_X + PROGRESS_W + 1, y + PROGRESS_Y + PROGRESS_H + 1,
                      COL_BG_OUTER);
        // Background
        graphics.fill(x + PROGRESS_X, y + PROGRESS_Y,
                      x + PROGRESS_X + PROGRESS_W, y + PROGRESS_Y + PROGRESS_H,
                      COL_PROGRESS_BG);
        // Fill (grows right)
        int maxP = menu.getMaxProgress();
        if (maxP > 0) {
            int fillW = (int) ((float) menu.getProgress() / maxP * PROGRESS_W);
            if (fillW > 0) {
                graphics.fill(x + PROGRESS_X,
                              y + PROGRESS_Y,
                              x + PROGRESS_X + fillW,
                              y + PROGRESS_Y + PROGRESS_H,
                              COL_PROGRESS_FG);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Machine title (centered)
        int titleW = this.font.width(this.title);
        graphics.drawString(this.font, this.title,
                (GUI_W - titleW) / 2, this.titleLabelY, COL_LABEL, false);
        // Player inventory label
        graphics.drawString(this.font, this.playerInventoryTitle,
                8, this.imageHeight - 94, COL_LABEL, false);
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        // Tooltip on energy bar
        int ex = leftPos + ENERGY_X;
        int ey = topPos + ENERGY_Y;
        if (mouseX >= ex && mouseX < ex + ENERGY_W && mouseY >= ey && mouseY < ey + ENERGY_H) {
            graphics.renderTooltip(this.font,
                    List.of(Component.literal(menu.getEnergy() + " / " + menu.getMaxEnergy() + " FE")),
                    java.util.Optional.empty(),
                    mouseX, mouseY);
        }
    }

}
