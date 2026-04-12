package com.imaire.violetmod.client.screen;

import com.imaire.violetmod.VioletMod;
import com.imaire.violetmod.common.menu.VioletExtractorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;

public class VioletExtractorScreen extends AbstractContainerScreen<VioletExtractorMenu> {

    // ── Texture ──────────────────────────────────────────────────────────────
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(VioletMod.MOD_ID, "textures/gui/violet_extractor.png");
    private static final int TEX = 256; // atlas size

    // ── GUI dimensions ───────────────────────────────────────────────────────
    private static final int GUI_W = 176;
    private static final int GUI_H = 166;

    // ── Energy bar ───────────────────────────────────────────────────────────
    private static final int ENERGY_X = 7;
    private static final int ENERGY_Y = 22;
    private static final int ENERGY_W = 15;
    private static final int ENERGY_H = 44;

    // ── Progress bar ─────────────────────────────────────────────────────────
    private static final int PROGRESS_X = 79;
    private static final int PROGRESS_Y = 35;
    private static final int PROGRESS_W = 24;
    private static final int PROGRESS_H = 17;

    // ── Dynamic fill colours (NOT in the texture, rendered procedurally) ─────
    private static final int COL_E_HIGH = 0xFF33EE44;  // >= 60%  green
    private static final int COL_E_MID  = 0xFFEEAA11;  // 25–59%  amber
    private static final int COL_E_LOW  = 0xFFEE4444;  // <  25%  red
    private static final int COL_P_FILL = 0xFF8800EE;  // violet progress
    private static final int COL_LABEL  = 0xFFDDDDDD;  // bright label

    // ── Constructor ──────────────────────────────────────────────────────────
    public VioletExtractorScreen(VioletExtractorMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth      = GUI_W;
        this.imageHeight     = GUI_H;
        this.titleLabelY     = 5;
        this.inventoryLabelY = GUI_H - 94; // 72 — standard MC value
    }

    // ── Render ───────────────────────────────────────────────────────────────

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        renderBackground(g, mouseX, mouseY, partialTick);
        super.render(g, mouseX, mouseY, partialTick);
        renderTooltip(g, mouseX, mouseY);
    }

    /**
     * Renders the static GUI texture, then overlays the dynamic fills
     * for the energy bar and progress bar.
     */
    @Override
    protected void renderBg(GuiGraphics g, float partialTick, int mouseX, int mouseY) {
        int x = leftPos;
        int y = topPos;

        // ── Static background (PNG blit) ──────────────────────────────────────
        // This covers: panel, bevel, machine/inventory areas, divider, hotbar separator,
        // all 36 inventory slot backgrounds + 3 machine slot backgrounds,
        // energy bar frame, progress bar frame, and arrow tip.
        g.blit(TEXTURE, x, y, 0, 0, GUI_W, GUI_H, TEX, TEX);

        // ── Energy fill (dynamic, grows upward) ───────────────────────────────
        int maxE = menu.getMaxEnergy();
        if (maxE > 0) {
            int fillH = (int) ((float) menu.getEnergy() / maxE * ENERGY_H);
            if (fillH > 0) {
                int pct = menu.getEnergy() * 100 / maxE;
                int col = pct < 25 ? COL_E_LOW : (pct < 60 ? COL_E_MID : COL_E_HIGH);
                g.fill(x + ENERGY_X,
                        y + ENERGY_Y + ENERGY_H - fillH,
                        x + ENERGY_X + ENERGY_W - 1,
                        y + ENERGY_Y + ENERGY_H - 1,
                        col);
            }
        }

        // ── Progress fill (dynamic, grows right) ─────────────────────────────
        int maxP = menu.getMaxProgress();
        if (maxP > 0) {
            int fillW = (int) ((float) menu.getProgress() / maxP * PROGRESS_W);
            if (fillW > 0) {
                g.fill(x + PROGRESS_X,
                        y + PROGRESS_Y,
                        x + PROGRESS_X + fillW,
                        y + PROGRESS_Y + PROGRESS_H - 1,
                        COL_P_FILL);
            }
        }
    }

    // ── Labels ───────────────────────────────────────────────────────────────

    @Override
    protected void renderLabels(GuiGraphics g, int mouseX, int mouseY) {
        // Machine title — centred
        int tw = this.font.width(this.title);
        g.drawString(this.font, this.title, (GUI_W - tw) / 2, this.titleLabelY, COL_LABEL, false);

        // Player inventory label
        g.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, COL_LABEL, false);
        }

    /** Format large FE numbers compactly: 20000 → "20k", 1500000 → "1.5M". */
    private static String formatFE(int fe) {
        if (fe >= 1_000_000) return String.format("%.1fM", fe / 1_000_000.0);
        if (fe >= 1_000)     return (fe / 1_000) + "k";
        return String.valueOf(fe);
    }

    // ── Tooltips ─────────────────────────────────────────────────────────────

    @Override
    protected void renderTooltip(GuiGraphics g, int mouseX, int mouseY) {
        super.renderTooltip(g, mouseX, mouseY);

        // Energy bar
        int ex = leftPos + ENERGY_X;
        int ey = topPos  + ENERGY_Y;
        if (mouseX >= ex && mouseX < ex + ENERGY_W && mouseY >= ey && mouseY < ey + ENERGY_H) {
            int e    = menu.getEnergy();
            int maxE = menu.getMaxEnergy();
            g.renderTooltip(this.font, List.of(
                    Component.translatable("tooltip.violetmod.energy"),
                    Component.literal(formatFE(e) + " / " + formatFE(maxE) + " FE")
            ), java.util.Optional.empty(), mouseX, mouseY);
        }

        // Progress bar
        int px = leftPos + PROGRESS_X;
        int py = topPos  + PROGRESS_Y;
        if (mouseX >= px && mouseX < px + PROGRESS_W && mouseY >= py && mouseY < py + PROGRESS_H) {
            int prog = menu.getProgress();
            int maxP = menu.getMaxProgress();
            if (maxP > 0) {
                g.renderTooltip(this.font, List.of(
                        Component.translatable("tooltip.violetmod.progress"),
                        Component.literal(+ prog * 100 / maxP + "%")
                ), java.util.Optional.empty(), mouseX, mouseY);
            }
        }
    }
}
