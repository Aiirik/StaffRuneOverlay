/*
 * Copyright (c) 2026, Aiirik
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.aiirik.staffruneoverlay;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

public class StaffRuneOverlayOverlay extends WidgetItemOverlay
{
	private static final int[] EMPTY_RUNES = new int[0];
	private static final int FIRE_RUNE_ID = 554;
	private static final int WATER_RUNE_ID = 555;
	private static final int AIR_RUNE_ID = 556;
	private static final int EARTH_RUNE_ID = 557;

	private final Client client;
	private final StaffRuneOverlayConfig config;
	private final ItemManager itemManager;
	private final TooltipManager tooltipManager;
	private final Map<Integer, int[]> itemCache = new HashMap<>();

	@Inject
	public StaffRuneOverlayOverlay(Client client, StaffRuneOverlayConfig config, ItemManager itemManager, TooltipManager tooltipManager)
	{
		this.client = client;
		this.config = config;
		this.itemManager = itemManager;
		this.tooltipManager = tooltipManager;
		showOnInventory();
		showOnEquipment();
		showOnBank();
		showOnInterfaces(
			InterfaceID.BANKMAIN,
			InterfaceID.BANKSIDE,
			InterfaceID.BANK_DEPOSITBOX,
			InterfaceID.GE_OFFERS,
			InterfaceID.GE_OFFERS_SIDE,
			InterfaceID.GE_PRICECHECKER,
			InterfaceID.GE_PRICECHECKER_SIDE,
			InterfaceID.SEED_VAULT,
			InterfaceID.SEED_VAULT_DEPOSIT,
			InterfaceID.SHOPMAIN,
			InterfaceID.SHOPSIDE,
			InterfaceID.SMITHING
		);
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		if (!config.showRunes())
		{
			return;
		}

		Widget widget = itemWidget.getWidget();
		boolean inContainer = isContainerWidget(widget);
		boolean inEquipment = isEquipmentWidget(widget);
		if (!shouldRenderInLocation(inContainer, inEquipment))
		{
			return;
		}

		int[] runeIds = itemCache.computeIfAbsent(itemId, this::getRuneIdsForItem);
		if (runeIds.length == 0)
		{
			return;
		}

		maybeAddTooltip(itemWidget, runeIds);

		Rectangle bounds = itemWidget.getCanvasBounds();
		if (config.staffMode() == StaffRuneOverlayConfig.StaffMode.ICON)
		{
			renderStaffIcons(graphics, bounds, runeIds, inContainer, inEquipment);
			return;
		}

		renderStaffText(graphics, bounds, runeIds, inContainer, inEquipment);
	}

	private void maybeAddTooltip(WidgetItem itemWidget, int[] runeIds)
	{
		if (!config.showTooltip())
		{
			return;
		}

		net.runelite.api.Point mouse = client.getMouseCanvasPosition();
		Rectangle bounds = itemWidget.getCanvasBounds();
		if (mouse == null || bounds == null || !bounds.contains(mouse.getX(), mouse.getY()))
		{
			return;
		}

		tooltipManager.add(new Tooltip(colorTooltipText(buildTooltipText(runeIds))));
	}

	private boolean shouldRenderInLocation(boolean inContainer, boolean inEquipment)
	{
		if (inEquipment)
		{
			return config.showRunesOnEquipment();
		}

		return !inContainer || config.showRunesInContainers();
	}

	private boolean isContainerWidget(Widget widget)
	{
		return isContainerWidgetId(widget) || isContainerWidgetId(widget != null ? widget.getParent() : null);
	}

	private boolean isEquipmentWidget(Widget widget)
	{
		return isEquipmentWidgetId(widget) || isEquipmentWidgetId(widget != null ? widget.getParent() : null);
	}

	private boolean isContainerWidgetId(Widget widget)
	{
		if (widget == null)
		{
			return false;
		}

		int groupId = widget.getId() >>> 16;
		return groupId == InterfaceID.BANKMAIN
			|| groupId == InterfaceID.BANKSIDE
			|| groupId == InterfaceID.BANK_DEPOSITBOX
			|| groupId == InterfaceID.GE_OFFERS
			|| groupId == InterfaceID.GE_OFFERS_SIDE
			|| groupId == InterfaceID.GE_PRICECHECKER
			|| groupId == InterfaceID.GE_PRICECHECKER_SIDE
			|| groupId == InterfaceID.SEED_VAULT
			|| groupId == InterfaceID.SEED_VAULT_DEPOSIT
			|| groupId == InterfaceID.SHOPMAIN
			|| groupId == InterfaceID.SHOPSIDE
			|| groupId == InterfaceID.SMITHING;
	}

	private boolean isEquipmentWidgetId(Widget widget)
	{
		if (widget == null)
		{
			return false;
		}

		int groupId = widget.getId() >>> 16;
		return groupId == InterfaceID.WORNITEMS
			|| groupId == InterfaceID.EQUIPMENT_SIDE;
	}

	private void renderStaffIcons(Graphics2D graphics, Rectangle bounds, int[] runeIds, boolean inContainer, boolean inEquipment)
	{
		int size = config.iconSize();
		StaffRuneOverlayConfig.ItemPosition[] positions = getStaffPositions(inContainer, inEquipment);

		for (int i = 0; i < runeIds.length && i < positions.length; i++)
		{
			BufferedImage icon = itemManager.getImage(runeIds[i], 1, false);
			if (icon != null)
			{
				Point point = getIconPoint(bounds, positions[i], size, config.staffOffset());
				graphics.drawImage(icon, point.x, point.y, size, size, null);
			}
		}
	}

	private void renderStaffText(Graphics2D graphics, Rectangle bounds, int[] runeIds, boolean inContainer, boolean inEquipment)
	{
		StaffRuneOverlayConfig.ItemPosition[] positions = getStaffPositions(inContainer, inEquipment);

		for (int i = 0; i < runeIds.length && i < positions.length; i++)
		{
			renderText(graphics, bounds, getLetter(runeIds[i]), positions[i], config.staffOffset(), inContainer);
		}
	}

	private void renderText(
		Graphics2D graphics,
		Rectangle bounds,
		String text,
		StaffRuneOverlayConfig.ItemPosition position,
		int offset,
		boolean inContainer
	)
	{
		Object oldTextAntialias = graphics.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		graphics.setFont(getOverlayFont(config.staffTextSize(), inContainer));

		FontMetrics fontMetrics = graphics.getFontMetrics();
		int textWidth = fontMetrics.stringWidth(text);
		int textHeight = fontMetrics.getAscent();

		int drawX;
		int drawY;

		switch (position)
		{
			case TOP_RIGHT:
				drawX = bounds.x + bounds.width - textWidth - offset;
				drawY = bounds.y + textHeight + offset;
				break;
			case BOTTOM_LEFT:
				drawX = bounds.x + offset;
				drawY = bounds.y + bounds.height - offset;
				break;
			case BOTTOM_RIGHT:
				drawX = bounds.x + bounds.width - textWidth - offset;
				drawY = bounds.y + bounds.height - offset;
				break;
			default:
				drawX = bounds.x + offset;
				drawY = bounds.y + textHeight + offset;
				break;
		}

		graphics.setColor(Color.BLACK);
		graphics.drawString(text, drawX + 1, drawY);
		graphics.drawString(text, drawX - 1, drawY);
		graphics.drawString(text, drawX, drawY + 1);
		graphics.drawString(text, drawX, drawY - 1);

		graphics.setColor(config.staffColor());
		graphics.drawString(text, drawX, drawY);
		graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, oldTextAntialias);
	}

	private Font getOverlayFont(int size, boolean inContainer)
	{
		Font baseFont;
		if (inContainer || size <= 10)
		{
			baseFont = FontManager.getRunescapeSmallFont();
		}
		else if (size >= 12)
		{
			baseFont = FontManager.getRunescapeBoldFont();
		}
		else
		{
			baseFont = FontManager.getRunescapeFont();
		}

		return baseFont.deriveFont(Font.PLAIN, (float) size);
	}

	private StaffRuneOverlayConfig.ItemPosition[] getStaffPositions(boolean inContainer, boolean inEquipment)
	{
		StaffRuneOverlayConfig.StaffLayout layout = inEquipment
			? config.staffEquipmentLayout()
			: (inContainer ? config.staffContainerLayout() : config.staffLayout());

		switch (layout)
		{
			case REVERSE_DIAGONAL:
				return new StaffRuneOverlayConfig.ItemPosition[]
				{
					StaffRuneOverlayConfig.ItemPosition.TOP_RIGHT,
					StaffRuneOverlayConfig.ItemPosition.BOTTOM_LEFT
				};
			case LEFT:
				return new StaffRuneOverlayConfig.ItemPosition[]
				{
					StaffRuneOverlayConfig.ItemPosition.TOP_LEFT,
					StaffRuneOverlayConfig.ItemPosition.BOTTOM_LEFT
				};
			case RIGHT:
				return new StaffRuneOverlayConfig.ItemPosition[]
				{
					StaffRuneOverlayConfig.ItemPosition.TOP_RIGHT,
					StaffRuneOverlayConfig.ItemPosition.BOTTOM_RIGHT
				};
			case TOP:
				return new StaffRuneOverlayConfig.ItemPosition[]
				{
					StaffRuneOverlayConfig.ItemPosition.TOP_LEFT,
					StaffRuneOverlayConfig.ItemPosition.TOP_RIGHT
				};
			case BOTTOM:
				return new StaffRuneOverlayConfig.ItemPosition[]
				{
					StaffRuneOverlayConfig.ItemPosition.BOTTOM_LEFT,
					StaffRuneOverlayConfig.ItemPosition.BOTTOM_RIGHT
				};
			default:
				return new StaffRuneOverlayConfig.ItemPosition[]
				{
					StaffRuneOverlayConfig.ItemPosition.TOP_LEFT,
					StaffRuneOverlayConfig.ItemPosition.BOTTOM_RIGHT
				};
		}
	}

	private Point getIconPoint(Rectangle bounds, StaffRuneOverlayConfig.ItemPosition position, int size, int offset)
	{
		int leftX = bounds.x - 1 + offset;
		int rightX = bounds.x + bounds.width - size + 1 - offset;
		int topY = bounds.y - 1 + offset;
		int bottomY = bounds.y + bounds.height - size + 1 - offset;

		switch (position)
		{
			case TOP_RIGHT:
				return new Point(rightX, topY);
			case BOTTOM_LEFT:
				return new Point(leftX, bottomY);
			case BOTTOM_RIGHT:
				return new Point(rightX, bottomY);
			default:
				return new Point(leftX, topY);
		}
	}

	private int[] getRuneIdsForItem(int itemId)
	{
		String lowerName = itemManager.getItemComposition(itemId).getName().toLowerCase();
		if (!isStaff(lowerName))
		{
			return EMPTY_RUNES;
		}

		if (lowerName.contains("lava")) return new int[]{FIRE_RUNE_ID, EARTH_RUNE_ID};
		if (lowerName.contains("mud")) return new int[]{WATER_RUNE_ID, EARTH_RUNE_ID};
		if (lowerName.contains("steam")) return new int[]{FIRE_RUNE_ID, WATER_RUNE_ID};
		if (lowerName.contains("smoke")) return new int[]{FIRE_RUNE_ID, AIR_RUNE_ID};
		if (lowerName.contains("mist")) return new int[]{WATER_RUNE_ID, AIR_RUNE_ID};
		if (lowerName.contains("dust")) return new int[]{EARTH_RUNE_ID, AIR_RUNE_ID};
		if (lowerName.contains("fire")) return new int[]{FIRE_RUNE_ID};
		if (lowerName.contains("water")) return new int[]{WATER_RUNE_ID};
		if (lowerName.contains("air")) return new int[]{AIR_RUNE_ID};
		if (lowerName.contains("earth")) return new int[]{EARTH_RUNE_ID};
		return EMPTY_RUNES;
	}

	private boolean isStaff(String lowerName)
	{
		return lowerName.contains("staff") || lowerName.contains("battlestaff");
	}

	private String getLetter(int runeId)
	{
		if (runeId == FIRE_RUNE_ID) return "F";
		if (runeId == WATER_RUNE_ID) return "W";
		if (runeId == AIR_RUNE_ID) return "A";
		return "E";
	}

	private String buildTooltipText(int[] runeIds)
	{
		StringBuilder builder = new StringBuilder("Provides: ");
		for (int i = 0; i < runeIds.length; i++)
		{
			if (i > 0)
			{
				builder.append(", ");
			}
			builder.append(getRuneName(runeIds[i]));
		}
		return builder.toString();
	}

	private String colorTooltipText(String text)
	{
		return "<col=00ffff>" + text + "</col>";
	}

	private String getRuneName(int runeId)
	{
		if (runeId == FIRE_RUNE_ID) return "Fire";
		if (runeId == WATER_RUNE_ID) return "Water";
		if (runeId == AIR_RUNE_ID) return "Air";
		return "Earth";
	}
}
