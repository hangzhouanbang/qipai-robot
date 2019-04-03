package com.anbang.qipai.robot.websocket.vo.doudizhu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dml.puke.pai.PukePai;

public class DoudizhuPlayerShoupaiVO {
	private List<PukePai> allShoupai;
	private int totalShoupai;

	public DoudizhuPlayerShoupaiVO() {

	}

	public DoudizhuPlayerShoupaiVO(Map<Integer, PukePai> allShoupai, int totalShoupai, List<Integer> sortIds) {
		this.allShoupai = new ArrayList<>();
		if (sortIds != null) {
			for (int id : sortIds) {
				this.allShoupai.add(allShoupai.get(id));
			}
		}
		this.totalShoupai = totalShoupai;
	}

	public List<PukePai> getAllShoupai() {
		return allShoupai;
	}

	public void setAllShoupai(List<PukePai> allShoupai) {
		this.allShoupai = allShoupai;
	}

	public int getTotalShoupai() {
		return totalShoupai;
	}

	public void setTotalShoupai(int totalShoupai) {
		this.totalShoupai = totalShoupai;
	}

}
