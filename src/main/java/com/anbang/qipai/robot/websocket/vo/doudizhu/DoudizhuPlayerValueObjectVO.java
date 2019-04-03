package com.anbang.qipai.robot.websocket.vo.doudizhu;

import java.util.List;

import com.dml.doudizhu.player.DoudizhuPlayerValueObject;
import com.dml.doudizhu.player.action.da.solution.DaPaiDianShuSolution;
import com.dml.puke.wanfa.dianshu.paizu.DianShuZuPaiZu;
import com.dml.puke.wanfa.position.Position;

public class DoudizhuPlayerValueObjectVO {
	private String id;
	private Position position;
	private DoudizhuPlayerShoupaiVO allShoupai;
	private int[] shoupaiDianShuAmountArray;
	private List<List<Integer>> shoupaiIdListForSortList;
	private List<DianShuZuPaiZu> lishiDachuPaiZuList;
	private DianShuZuPaiZu publicDachuPaiZu;
	private List<DaPaiDianShuSolution> yaPaiSolutionCandidates;
	private List<DaPaiDianShuSolution> yaPaiSolutionsForTips;
	private boolean guo;
	private boolean watingForMe = false;
	private PlayerQiangdizhuState state;
	private boolean noPaiWarning;// 结束警报
	private int rangPai;

	public DoudizhuPlayerValueObjectVO() {

	}

	public DoudizhuPlayerValueObjectVO(DoudizhuPlayerValueObject doudizhuPlayerValueObject) {
		id = doudizhuPlayerValueObject.getId();
		position = doudizhuPlayerValueObject.getPosition();
		shoupaiIdListForSortList = doudizhuPlayerValueObject.getShoupaiIdListForSortList();
		if (shoupaiIdListForSortList == null || shoupaiIdListForSortList.isEmpty()) {
			allShoupai = new DoudizhuPlayerShoupaiVO(doudizhuPlayerValueObject.getAllShoupai(),
					doudizhuPlayerValueObject.getTotalShoupai(), null);
		} else {
			allShoupai = new DoudizhuPlayerShoupaiVO(doudizhuPlayerValueObject.getAllShoupai(),
					doudizhuPlayerValueObject.getTotalShoupai(), shoupaiIdListForSortList.get(0));
		}
		shoupaiDianShuAmountArray = doudizhuPlayerValueObject.getShoupaiDianShuAmountArray();
		lishiDachuPaiZuList = doudizhuPlayerValueObject.getLishiDachuPaiZuList();
		publicDachuPaiZu = doudizhuPlayerValueObject.getPublicDachuPaiZu();
		yaPaiSolutionCandidates = doudizhuPlayerValueObject.getYaPaiSolutionCandidates();
		if (yaPaiSolutionCandidates != null && !yaPaiSolutionCandidates.isEmpty()) {
			watingForMe = true;
		}
		yaPaiSolutionsForTips = doudizhuPlayerValueObject.getYaPaiSolutionsForTips();
		guo = doudizhuPlayerValueObject.isGuo();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public DoudizhuPlayerShoupaiVO getAllShoupai() {
		return allShoupai;
	}

	public void setAllShoupai(DoudizhuPlayerShoupaiVO allShoupai) {
		this.allShoupai = allShoupai;
	}

	public int[] getShoupaiDianShuAmountArray() {
		return shoupaiDianShuAmountArray;
	}

	public void setShoupaiDianShuAmountArray(int[] shoupaiDianShuAmountArray) {
		this.shoupaiDianShuAmountArray = shoupaiDianShuAmountArray;
	}

	public List<List<Integer>> getShoupaiIdListForSortList() {
		return shoupaiIdListForSortList;
	}

	public void setShoupaiIdListForSortList(List<List<Integer>> shoupaiIdListForSortList) {
		this.shoupaiIdListForSortList = shoupaiIdListForSortList;
	}

	public List<DianShuZuPaiZu> getLishiDachuPaiZuList() {
		return lishiDachuPaiZuList;
	}

	public void setLishiDachuPaiZuList(List<DianShuZuPaiZu> lishiDachuPaiZuList) {
		this.lishiDachuPaiZuList = lishiDachuPaiZuList;
	}

	public DianShuZuPaiZu getPublicDachuPaiZu() {
		return publicDachuPaiZu;
	}

	public void setPublicDachuPaiZu(DianShuZuPaiZu publicDachuPaiZu) {
		this.publicDachuPaiZu = publicDachuPaiZu;
	}

	public List<DaPaiDianShuSolution> getYaPaiSolutionCandidates() {
		return yaPaiSolutionCandidates;
	}

	public void setYaPaiSolutionCandidates(List<DaPaiDianShuSolution> yaPaiSolutionCandidates) {
		this.yaPaiSolutionCandidates = yaPaiSolutionCandidates;
	}

	public List<DaPaiDianShuSolution> getYaPaiSolutionsForTips() {
		return yaPaiSolutionsForTips;
	}

	public void setYaPaiSolutionsForTips(List<DaPaiDianShuSolution> yaPaiSolutionsForTips) {
		this.yaPaiSolutionsForTips = yaPaiSolutionsForTips;
	}

	public boolean isGuo() {
		return guo;
	}

	public void setGuo(boolean guo) {
		this.guo = guo;
	}

	public boolean isWatingForMe() {
		return watingForMe;
	}

	public void setWatingForMe(boolean watingForMe) {
		this.watingForMe = watingForMe;
	}

	public PlayerQiangdizhuState getState() {
		return state;
	}

	public void setState(PlayerQiangdizhuState state) {
		this.state = state;
	}

	public boolean isNoPaiWarning() {
		return noPaiWarning;
	}

	public void setNoPaiWarning(boolean noPaiWarning) {
		this.noPaiWarning = noPaiWarning;
	}

	public int getRangPai() {
		return rangPai;
	}

	public void setRangPai(int rangPai) {
		this.rangPai = rangPai;
	}

}
