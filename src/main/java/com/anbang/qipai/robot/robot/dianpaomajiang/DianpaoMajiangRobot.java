package com.anbang.qipai.robot.robot.dianpaomajiang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import com.anbang.qipai.robot.plan.bean.RobotMemberDbo;
import com.anbang.qipai.robot.robot.Robot;
import com.anbang.qipai.robot.robot.game.Game;
import com.anbang.qipai.robot.robot.game.vote.GameFinishVoteMO;
import com.anbang.qipai.robot.robot.majiang.action.MajiangPlayerAction;
import com.anbang.qipai.robot.robot.websocket.CommonMO;

public class DianpaoMajiangRobot extends Robot {

	private final String readyUrl = "/game/ready";

	private final String gameInfoUrl = "/game/info";

	private final String panForMeUrl = "/mj/pan_action_frame_for_me";

	private final String actionUrl = "/mj/action";

	private final String readyToNextPanUrl = "/mj/ready_to_next_pan";

	private final String voteInfoUrl = "/game/finish_vote_info";

	private final String voteToFinishUrl = "/game/vote_to_finish";

	private String gameState;

	private String state;

	private String onlineState;

	public DianpaoMajiangRobot(Game game, String gameId, RobotMemberDbo robotDbo)
			throws ClientProtocolException, IOException {
		super(game, gameId, robotDbo);
	}

	@Override
	public void doScope(String scope) throws Exception {
		if (scope.equals("gameInfo")) {
			queryGameInfo();
		} else if (scope.equals("panForMe")) {
			queryPanForMe();
		} else if (scope.equals("panResult")) {
			queryPanResult();
		} else if (scope.equals("juResult")) {
			queryJuResult();
		} else if (scope.equals("gameFinishVote")) {
			queryGameFinishVote();
		}
	}

	private void queryGameInfo() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("gameId", gameId);
		HttpResponse response = doPost(httpUrl + gameInfoUrl, querys);
		CommonMO mo = gson.fromJson(EntityUtils.toString(response.getEntity()), CommonMO.class);
		if (!mo.isSuccess()) {
			return;
		}
		Map data = (Map) mo.getData();
		gameState = (String) data.get("state");
		((List) data.get("playerList")).forEach((player) -> {
			Map p = (Map) player;
			if (getPlayerId().equals(p.get("playerId"))) {
				state = (String) p.get("state");
				onlineState = (String) p.get("onlineState");
			}
		});
		if (gameState != null && (gameState.equals("canceled") || gameState.equals("finished")
				|| gameState.equals("finishedbyvote"))) {
			client.doClose();
		} else if (gameState != null && gameState.equals("waitingStart")) {
			if (state != null && state.equals("joined")) {
				doReady();
			}
		} else if (gameState != null && gameState.equals("waitingNextPan")) {
			if (state != null && (state.equals("panFinished") || state.equals("PlayerPanFinishedAndVoting")
					|| state.equals("PlayerPanFinishedAndVoted"))) {
				doReadyNextPan();
			}
		}
	}

	private void queryPanForMe() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		querys.put("gameId", gameId);
		HttpResponse response = doPost(httpUrl + panForMeUrl, querys);
		CommonMO mo = gson.fromJson(EntityUtils.toString(response.getEntity()), CommonMO.class);
		if (!mo.isSuccess()) {
			return;
		}
		doAction(mo);
	}

	private void queryPanResult() throws Exception {

	}

	private void queryJuResult() throws Exception {
		client.doClose();
	}

	private void queryGameFinishVote() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("gameId", gameId);
		HttpResponse response = doPost(httpUrl + voteInfoUrl, querys);
		CommonMO mo = gson.fromJson(EntityUtils.toString(response.getEntity()), CommonMO.class);
		if (!mo.isSuccess()) {
			return;
		}
		Map data = (Map) mo.getData();
		GameFinishVoteMO voteMO = new GameFinishVoteMO(data);
		if (voteMO.getResult() == null && !voteMO.getVotePlayerIds().contains(getPlayerId())) {
			doVoteToFinish();
		} else if (voteMO.getResult() != null && voteMO.getResult().equals("yes")) {
			client.doClose();
		}
	}

	private void doReady() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		HttpResponse response = doPost(httpUrl + readyUrl, querys);
		CommonMO rmo = gson.fromJson(EntityUtils.toString(response.getEntity()), CommonMO.class);
		if (!rmo.isSuccess()) {
			return;
		}
		Map rdata = (Map) rmo.getData();
		((List) rdata.get("queryScopes")).forEach((queryScope) -> {
			String scope = (String) queryScope;
			try {
				doScope(scope);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void doReadyNextPan() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		HttpResponse response = doPost(httpUrl + readyToNextPanUrl, querys);
		CommonMO rmo = gson.fromJson(EntityUtils.toString(response.getEntity()), CommonMO.class);
		if (!rmo.isSuccess()) {
			return;
		}
		Map rdata = (Map) rmo.getData();
		((List) rdata.get("queryScopes")).forEach((queryScope) -> {
			String scope = (String) queryScope;
			try {
				doScope(scope);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void doVoteToFinish() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		querys.put("yes", "true");
		HttpResponse response = doPost(httpUrl + voteToFinishUrl, querys);
		CommonMO rmo = gson.fromJson(EntityUtils.toString(response.getEntity()), CommonMO.class);
		if (!rmo.isSuccess()) {
			return;
		}
		Map rdata = (Map) rmo.getData();
		String scope = (String) rdata.get("queryScope");
		try {
			doScope(scope);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void doAction(CommonMO mo) throws Exception {
		Map data = (Map) mo.getData();
		int actionNo = ((Double) data.get("no")).intValue();
		Map panAfterAction = (Map) data.get("panAfterAction");
		List<MajiangPlayerAction> actionCandidates = new ArrayList<>();
		((List) panAfterAction.get("playerList")).forEach((player) -> {
			Map majiangPlayer = (Map) player;
			if (majiangPlayer.get("id").equals(getPlayerId())) {
				if (majiangPlayer.get("actionCandidates") != null) {
					((List) majiangPlayer.get("actionCandidates")).forEach((action) -> {
						Map playerAction = (Map) action;
						actionCandidates.add(new MajiangPlayerAction(playerAction));
					});
				}
			}
		});
		if (actionCandidates.isEmpty()) {
			return;
		}
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		querys.put("id", actionCandidates.get(0).getId());
		querys.put("actionNo", new Integer(actionNo + 1).toString());
		HttpResponse response = doPost(httpUrl + actionUrl, querys);
		CommonMO rmo = gson.fromJson(EntityUtils.toString(response.getEntity()), CommonMO.class);
		if (!rmo.isSuccess()) {
			return;
		}
		Map rdata = (Map) rmo.getData();
		((List) rdata.get("queryScopes")).forEach((queryScope) -> {
			String scope = (String) queryScope;
			try {
				doScope(scope);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
