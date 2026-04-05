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
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

@ConfigGroup("staffruneoverlay")
public interface StaffRuneOverlayConfig extends Config
{
	enum ItemPosition
	{
		TOP_LEFT("Top Left"),
		TOP_RIGHT("Top Right"),
		BOTTOM_LEFT("Bottom Left"),
		BOTTOM_RIGHT("Bottom Right");

		private final String name;

		ItemPosition(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	enum StaffMode
	{
		ICON,
		TEXT
	}

	enum StaffLayout
	{
		DIAGONAL("Diagonal"),
		REVERSE_DIAGONAL("Reverse Diagonal"),
		LEFT("Left"),
		RIGHT("Right"),
		TOP("Top"),
		BOTTOM("Bottom");

		private final String name;

		StaffLayout(String name)
		{
			this.name = name;
		}

		@Override
		public String toString()
		{
			return name;
		}
	}

	@ConfigSection(name = "General", description = "Core staff rune overlay settings", position = 0)
	String generalSection = "generalSection";

	@ConfigSection(name = "Appearance", description = "Visual display settings", position = 1)
	String appearanceSection = "appearanceSection";

	@ConfigSection(name = "Inventory", description = "Inventory-specific placement settings", position = 2)
	String inventorySection = "inventorySection";

	@ConfigSection(name = "Banks And Shops", description = "Container-specific placement settings", position = 3)
	String containerSection = "containerSection";

	@ConfigSection(name = "Equipment", description = "Equipment screen placement settings", position = 4)
	String equipmentSection = "equipmentSection";

	@ConfigItem(keyName = "showRunes", name = "Show Item Overlay", description = "Show rune overlays on supported staves", position = 0, section = generalSection)
	default boolean showRunes()
	{
		return true;
	}

	@ConfigItem(keyName = "showTooltip", name = "Show Hover Tooltip", description = "Add staff rune info to the item tooltip on hover", position = 1, section = generalSection)
	default boolean showTooltip()
	{
		return false;
	}

	@ConfigItem(keyName = "staffMode", name = "Overlay Mode", description = "Display rune overlays as icons or letters", position = 0, section = appearanceSection)
	default StaffMode staffMode()
	{
		return StaffMode.ICON;
	}

	@Range(min = 10, max = 25)
	@ConfigItem(keyName = "iconSize", name = "Icon Size", description = "Size of the rune icons", position = 1, section = appearanceSection)
	default int iconSize()
	{
		return 16;
	}

	@Range(min = 9, max = 20)
	@ConfigItem(keyName = "staffTextSize", name = "Text Size", description = "Size of the rune letters in text mode", position = 2, section = appearanceSection)
	default int staffTextSize()
	{
		return 15;
	}

	@ConfigItem(keyName = "staffColor", name = "Text Color", description = "Color used for rune letters in text mode", position = 3, section = appearanceSection)
	default Color staffColor()
	{
		return Color.CYAN;
	}

	@Range(min = -5, max = 10)
	@ConfigItem(keyName = "staffOffset", name = "Corner Offset", description = "Nudge rune overlays within the item slot", position = 4, section = appearanceSection)
	default int staffOffset()
	{
		return 0;
	}

	@ConfigItem(keyName = "staffLayout", name = "Layout", description = "Where to place staff runes in your inventory", position = 0, section = inventorySection)
	default StaffLayout staffLayout()
	{
		return StaffLayout.DIAGONAL;
	}

	@ConfigItem(keyName = "showRunesInContainers", name = "Show Overlay", description = "Also show staff runes in banks and other inventory-style containers", position = 0, section = containerSection)
	default boolean showRunesInContainers()
	{
		return true;
	}

	@ConfigItem(keyName = "staffContainerLayout", name = "Layout", description = "Where to place staff runes in banks and shops", position = 1, section = containerSection)
	default StaffLayout staffContainerLayout()
	{
		return StaffLayout.BOTTOM;
	}

	@ConfigItem(keyName = "showRunesOnEquipment", name = "Show Overlay", description = "Also show staff runes on the equipment screen", position = 0, section = equipmentSection)
	default boolean showRunesOnEquipment()
	{
		return true;
	}

	@ConfigItem(keyName = "staffEquipmentLayout", name = "Layout", description = "Where to place staff runes on the equipment screen", position = 1, section = equipmentSection)
	default StaffLayout staffEquipmentLayout()
	{
		return StaffLayout.DIAGONAL;
	}
}
