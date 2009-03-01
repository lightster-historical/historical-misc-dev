package com.lightdatasys.nascar.live.setting;

import com.lightdatasys.nascar.live.gui.cell.ResultCell;


public class ResultModeSetting extends Setting<ResultCell.Mode>
{
	public ResultModeSetting(int num)
	{
		super("resultMode" + num, "Result Mode [" + num + "]", ResultCell.Mode.POSITION, true);
		
		for(ResultCell.Mode mode : ResultCell.Mode.values())
		{
			addOption(mode.getStringIndex(), mode);
		}
	}
}
