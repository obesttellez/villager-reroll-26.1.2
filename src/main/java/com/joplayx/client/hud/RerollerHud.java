package com.joplayx.client.hud;

import com.joplayx.client.VillagerRerollClient;
import com.joplayx.client.config.RerollerConfig;
import com.joplayx.client.state.RerollController;
import com.joplayx.client.state.RerollState;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.CommonColors;

/**
 * HUD overlay for the Villager Trade Reroller.
 *
 * Implements HudElement by providing extractRenderState(GuiGraphicsExtractor, DeltaTracker).
 * Method name and signature confirmed from HudElement.java and HudTests.java in references.
 *
 * Uses graphics.text() and graphics.fill() — confirmed from HudTests.java reference.
 */
public class RerollerHud {

	private static final int PADDING = 4;
	private static final int LINE_HEIGHT = 10;
	private static final int X = 4;
	private static final int Y_START = 4;

	// Confirmed from HudTests.java: graphics.text(font, text, x, y, color)
	// Uses CommonColors or raw ARGB int — confirmed from HudTests.java reference
	private static final int COLOR_BG     = 0xAA000000; // semi-transparent black
	private static final int COLOR_GOLD   = 0xFFFFAA00;
	private static final int COLOR_WHITE  = CommonColors.WHITE;
	private static final int COLOR_GRAY   = 0xFFAAAAAA;
	private static final int COLOR_GREEN  = 0xFF55FF55;
	private static final int COLOR_RED    = 0xFFFF5555;
	private static final int COLOR_YELLOW = 0xFFFFFF55;

	/**
	 * Called every frame by HudElementRegistry.
	 * Method name MUST be extractRenderState — confirmed from HudElement.java interface in references.
	 */
	public static void extractRenderState(GuiGraphicsExtractor graphics, DeltaTracker delta) {
		RerollerConfig.Config cfg = RerollerConfig.get();
		if (!cfg.hudEnabled) return;

		RerollController ctrl = VillagerRerollClient.CONTROLLER;
		RerollState state = ctrl.getState();
		if (state == RerollState.IDLE) return;

		Minecraft mc = Minecraft.getInstance();
		if (mc.options.hideGui) return;

		int statusColor = switch (state) {
			case FOUND -> COLOR_GREEN;
			case ERROR -> COLOR_RED;
			case WAIT_FOR_PROFESSION, WAIT_FOR_SCREEN,
				 WAIT_AFTER_CLOSE, WAIT_BEFORE_RETRY,
				 WAIT_BREAK_COMPLETE -> COLOR_YELLOW;
			default -> COLOR_WHITE;
		};

		String target = cfg.targetEnchantment.isEmpty() ? "not set" : cfg.targetEnchantment;
		String lecternPos = cfg.hasLecternPos() ? cfg.lecternPosString() : "NOT SET — press L in-game";

		String[] lines = {
			"[Villager Reroller]",
			"Status: " + ctrl.getStatusMessage(),
			"Target: " + target,
			"Lectern: " + lecternPos,
			"Max cost: " + cfg.maxEmeraldCost + " emeralds",
			"Attempts: " + ctrl.getAttempts(),
			"Last: " + ctrl.getLastTradeDescription(),
			"[J] Start/Stop  [K] Stop  [L] Set Pos"
		};

		int[] colors = {
			COLOR_GOLD,
			statusColor,
			COLOR_WHITE,
			cfg.hasLecternPos() ? COLOR_GRAY : COLOR_RED,
			COLOR_GRAY,
			COLOR_WHITE,
			COLOR_GRAY,
			COLOR_GRAY
		};

		// Measure widest line for background
		int maxWidth = 0;
		for (String line : lines) {
			int w = mc.font.width(line);
			if (w > maxWidth) maxWidth = w;
		}

		int bgX1 = X - PADDING;
		int bgY1 = Y_START - PADDING;
		int bgX2 = X + maxWidth + PADDING;
		int bgY2 = Y_START + lines.length * LINE_HEIGHT + PADDING;

		// graphics.fill() — confirmed from HudTests.java reference
		graphics.fill(bgX1, bgY1, bgX2, bgY2, COLOR_BG);

		// graphics.text() — confirmed from HudTests.java reference
		for (int i = 0; i < lines.length; i++) {
			graphics.text(mc.font, lines[i], X, Y_START + i * LINE_HEIGHT, colors[i]);
		}
	}
}
