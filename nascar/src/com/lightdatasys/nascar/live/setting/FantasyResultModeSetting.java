package com.lightdatasys.nascar.live.setting;

import com.lightdatasys.nascar.live.gui.cell.FantasyResultCell;


public class FantasyResultModeSetting extends Setting<FantasyResultCell.Mode>
{
	public FantasyResultModeSetting(int num)
	{
		super("fantasyResultMode" + num, "Fantasy Result Mode [" + num + "]", FantasyResultCell.Mode.POSITION, true);
		
		for(FantasyResultCell.Mode mode : FantasyResultCell.Mode.values())
		{
			addOption(mode.getStringIndex(), mode);
		}
	}
}
