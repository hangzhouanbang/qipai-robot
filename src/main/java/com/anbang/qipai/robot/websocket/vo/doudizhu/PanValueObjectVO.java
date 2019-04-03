package com.anbang.qipai.robot.websocket.vo.doudizhu;

import java.util.ArrayList;
import java.util.List;

import com.dml.doudizhu.pan.PanValueObject;
import com.dml.puke.pai.PaiListValueObject;
import com.dml.puke.pai.PukePai;
import com.dml.puke.wanfa.dianshu.paizu.DianShuZuPaiZu;
import com.dml.puke.wanfa.position.Position;

public class PanValueObjectVO {
	private int no;
	private List<DoudizhuPlayerValueObjectVO> doudizhuPlayerList;
	private PaiListValueObject paiListValueObject;
	private List<DianShuZuPaiZu> dachuPaiZuList;
	private String dizhuPlayerId;// 地主id
	private Position actionPosition;
	private String latestDapaiPlayerId;
	private List<PukePai> dipaiList;
	private int rangPai;

	public PanValueObjectVO() {
	}

	public PanValueObjectVO(PanValueObject panValueObject) {
		no = panValueObject.getNo();
		doudizhuPlayerList = new ArrayList<>();
		panValueObject.getDoudizhuPlayerList()
				.forEach((doudizhuPlayer) -> doudizhuPlayerList.add(new DoudizhuPlayerValueObjectVO(doudizhuPlayer)));
		paiListValueObject = panValueObject.getPaiListValueObject();
		dachuPaiZuList = panValueObject.getDachuPaiZuList();
		actionPosition = panValueObject.getActionPosition();
		latestDapaiPlayerId = panValueObject.getLatestDapaiPlayerId();
		dizhuPlayerId = panValueObject.getDizhuPlayerId();
	}



	public String getDizhuPlayerId() {
		return dizhuPlayerId;
	}

	public void setDizhuPlayerId(String dizhuPlayerId) {
		this.dizhuPlayerId = dizhuPlayerId;
	}

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public List<DoudizhuPlayerValueObjectVO> getDoudizhuPlayerList() {
		return doudizhuPlayerList;
	}

	public void setDoudizhuPlayerList(List<DoudizhuPlayerValueObjectVO> doudizhuPlayerList) {
		this.doudizhuPlayerList = doudizhuPlayerList;
	}

	public PaiListValueObject getPaiListValueObject() {
		return paiListValueObject;
	}

	public void setPaiListValueObject(PaiListValueObject paiListValueObject) {
		this.paiListValueObject = paiListValueObject;
	}

	public List<DianShuZuPaiZu> getDachuPaiZuList() {
		return dachuPaiZuList;
	}

	public void setDachuPaiZuList(List<DianShuZuPaiZu> dachuPaiZuList) {
		this.dachuPaiZuList = dachuPaiZuList;
	}

	public Position getActionPosition() {
		return actionPosition;
	}

	public void setActionPosition(Position actionPosition) {
		this.actionPosition = actionPosition;
	}

	public String getLatestDapaiPlayerId() {
		return latestDapaiPlayerId;
	}

	public void setLatestDapaiPlayerId(String latestDapaiPlayerId) {
		this.latestDapaiPlayerId = latestDapaiPlayerId;
	}

	public List<PukePai> getDipaiList() {
		return dipaiList;
	}

	public void setDipaiList(List<PukePai> dipaiList) {
		this.dipaiList = dipaiList;
	}

	public int getRangPai() {
		return rangPai;
	}

	public void setRangPai(int rangPai) {
		this.rangPai = rangPai;
	}

}
