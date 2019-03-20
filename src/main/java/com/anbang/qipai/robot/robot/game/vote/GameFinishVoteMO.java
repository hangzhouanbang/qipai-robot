package com.anbang.qipai.robot.robot.game.vote;

import java.util.Map;
import java.util.Set;

public class GameFinishVoteMO {

	private String sponsorId;

	private Set<String> votePlayerIds;

	private Map<String, String> playerIdVoteOptionMap;

	private String result;

	private long startTime;

	private long remainTime;

	public GameFinishVoteMO(Map voteInfoMap) {
		if (voteInfoMap.get("sponsorId") != null) {
			sponsorId = (String) voteInfoMap.get("sponsorId");
		}
		if (voteInfoMap.get("votePlayerIds") != null) {
			votePlayerIds = (Set) voteInfoMap.get("votePlayerIds");
		}
		if (voteInfoMap.get("playerIdVoteOptionMap") != null) {
			playerIdVoteOptionMap = (Map) voteInfoMap.get("playerIdVoteOptionMap");
		}
		if (voteInfoMap.get("result") != null) {
			result = (String) voteInfoMap.get("result");
		}
		if (voteInfoMap.get("startTime") != null) {
			startTime = ((Double) voteInfoMap.get("startTime")).longValue();
		}
		if (voteInfoMap.get("remainTime") != null) {
			remainTime = ((Double) voteInfoMap.get("remainTime")).longValue();
		}
	}

	public String getSponsorId() {
		return sponsorId;
	}

	public void setSponsorId(String sponsorId) {
		this.sponsorId = sponsorId;
	}

	public Set<String> getVotePlayerIds() {
		return votePlayerIds;
	}

	public void setVotePlayerIds(Set<String> votePlayerIds) {
		this.votePlayerIds = votePlayerIds;
	}

	public Map<String, String> getPlayerIdVoteOptionMap() {
		return playerIdVoteOptionMap;
	}

	public void setPlayerIdVoteOptionMap(Map<String, String> playerIdVoteOptionMap) {
		this.playerIdVoteOptionMap = playerIdVoteOptionMap;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getRemainTime() {
		return remainTime;
	}

	public void setRemainTime(long remainTime) {
		this.remainTime = remainTime;
	}

}
