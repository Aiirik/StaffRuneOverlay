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

	@ConfigSection(name = "Display", description = "Settings for staff rune overlays", position = 0)
	String displaySection = "displaySection";

	@ConfigItem(keyName = "showRunes", name = "Show Runes", description = "Show rune overlays on staves", position = 1, section = displaySection)
	default boolean showRunes()
	{
		return true;
	}

	@ConfigItem(keyName = "staffMode", name = "Display Mode", description = "Show rune icons or letters", position = 2, section = displaySection)
	default StaffMode staffMode()
	{
		return StaffMode.ICON;
	}

	@ConfigItem(keyName = "showTooltip", name = "Show Tooltip", description = "Add the staff rune info to the item tooltip on hover", position = 3, section = displaySection)
	default boolean showTooltip()
	{
		return false;
	}

	@ConfigItem(keyName = "staffLayout", name = "Inventory Layout", description = "Where to place staff runes in your inventory", position = 4, section = displaySection)
	default StaffLayout staffLayout()
	{
		return StaffLayout.DIAGONAL;
	}

	@ConfigItem(keyName = "staffColor", name = "Text Color", description = "Color for rune letters in text mode", position = 5, section = displaySection)
	default Color staffColor()
	{
		return Color.CYAN;
	}

	@Range(min = 10, max = 25)
	@ConfigItem(keyName = "iconSize", name = "Icon Size", description = "Size of the rune icons", position = 6, section = displaySection)
	default int iconSize()
	{
		return 16;
	}

	@Range(min = -5, max = 10)
	@ConfigItem(keyName = "staffOffset", name = "Corner Offset", description = "Nudge rune overlays within the item slot", position = 7, section = displaySection)
	default int staffOffset()
	{
		return 0;
	}

	@ConfigItem(keyName = "showRunesInContainers", name = "Show In Banks", description = "Also show staff runes in bank and other inventory-style containers", position = 8, section = displaySection)
	default boolean showRunesInContainers()
	{
		return true;
	}

	@ConfigItem(keyName = "staffContainerLayout", name = "Bank Layout", description = "Where to place staff runes in banks and shops", position = 9, section = displaySection)
	default StaffLayout staffContainerLayout()
	{
		return StaffLayout.BOTTOM;
	}

	@ConfigItem(keyName = "showRunesOnEquipment", name = "Show On Equipment", description = "Also show staff runes on the equipment screen", position = 10, section = displaySection)
	default boolean showRunesOnEquipment()
	{
		return true;
	}

	@ConfigItem(keyName = "staffEquipmentLayout", name = "Equipment Layout", description = "Where to place staff runes on the equipment screen", position = 11, section = displaySection)
	default StaffLayout staffEquipmentLayout()
	{
		return StaffLayout.DIAGONAL;
	}

	@Range(min = 9, max = 20)
	@ConfigItem(keyName = "staffTextSize", name = "Text Size", description = "Size of the rune letters in text mode", position = 12, section = displaySection)
	default int staffTextSize()
	{
		return 15;
	}
}
