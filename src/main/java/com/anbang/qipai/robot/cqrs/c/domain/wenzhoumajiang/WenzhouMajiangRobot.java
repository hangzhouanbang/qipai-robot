package com.anbang.qipai.robot.cqrs.c.domain.wenzhoumajiang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import com.anbang.qipai.robot.config.RobotConfig;
import com.anbang.qipai.robot.cqrs.c.domain.CommonMO;
import com.anbang.qipai.robot.cqrs.c.domain.JoinGameException;
import com.anbang.qipai.robot.cqrs.c.domain.Robot;
import com.anbang.qipai.robot.cqrs.c.domain.RobotLoginException;
import com.anbang.qipai.robot.cqrs.c.domain.game.Game;
import com.anbang.qipai.robot.cqrs.c.domain.game.GameFinishVoteMO;
import com.anbang.qipai.robot.cqrs.c.domain.majiang.MajiangPlayerAction;
import com.anbang.qipai.robot.cqrs.q.dbo.RobotDbo;

public class WenzhouMajiangRobot extends Robot {

	private final String readyUrl = "/game/ready";

	private final String gameInfoUrl = "/game/info";

	private final String panForMeUrl = "/mj/pan_action_frame_for_me";

	private final String actionUrl = "/mj/action";

	private final String readyToNextPanUrl = "/mj/ready_to_next_pan";

	private final String voteInfoUrl = "/game/finish_vote_info";

	private final String voteToFinishUrl = "/game/vote_to_finish";

	private final String maidiInfoUrl = "/game/maidi_info";

	private final String maidiUrl = "/mj/maidi";

	private String gameState;

	private String state;

	private String onlineState;

	public WenzhouMajiangRobot(Game game, String gameId, RobotDbo robotDbo, String unionid, String openid)
			throws ClientProtocolException, IOException, RobotLoginException, JoinGameException {
		super(game, gameId, robotDbo, unionid, openid);
	}

	public WenzhouMajiangRobot(Game game, String gameId, String playerId, String nickname, String headimgurl,
			String gender, String token) {
		super(game, gameId, playerId, nickname, headimgurl, gender, token);
		this.httpUrl = RobotConfig.WENZHOU_HTTP_URL;
		this.wsUrl = RobotConfig.WENZHOU_WS_URL;
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
		} else if (scope.equals("maidiState")) {
			queryMaidiInfo();
		}
	}

	private void queryGameInfo() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("gameId", gameId);
		HttpResponse response = doPost(httpUrl + gameInfoUrl, null, querys);
		String entity = EntityUtils.toString(response.getEntity());
		CommonMO mo = gson.fromJson(entity, CommonMO.class);
		if (!mo.isSuccess()) {
			return;
		}
		Map data = (Map) ((Map) mo.getData()).get("game");
		gameState = (String) data.get("state");
		((List) data.get("playerList")).forEach((player) -> {
			Map p = (Map) player;
			if (getPlayerId().equals(p.get("playerId"))) {
				state = (String) p.get("state");
				onlineState = (String) p.get("onlineState");
			}
		});
		if (gameState != null && gameState.equals("playing")) {
			queryPanForMe();
		} else if (gameState != null && (gameState.equals("canceled") || gameState.equals("finished")
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
		} else if (gameState != null && gameState.equals("maidi")) {
			queryMaidiInfo();
		}
	}

	private void queryPanForMe() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		querys.put("gameId", gameId);
		HttpResponse response = doPost(httpUrl + panForMeUrl, null, querys);
		String entity = EntityUtils.toString(response.getEntity());
		CommonMO mo = gson.fromJson(entity, CommonMO.class);
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
		HttpResponse response = doPost(httpUrl + voteInfoUrl, null, querys);
		String entity = EntityUtils.toString(response.getEntity());
		CommonMO mo = gson.fromJson(entity, CommonMO.class);
		if (!mo.isSuccess()) {
			return;
		}
		Map data = (Map) ((Map) mo.getData()).get("vote");
		GameFinishVoteMO voteMO = new GameFinishVoteMO(data);
		if (voteMO.getResult() == null && !voteMO.getVotePlayerIds().contains(getPlayerId())) {
			doVoteToFinish();
		} else if (voteMO.getResult() != null && voteMO.getResult().equals("yes")) {
			client.doClose();
		}
	}

	private void queryMaidiInfo() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("gameId", gameId);
		HttpResponse response = doPost(httpUrl + maidiInfoUrl, null, querys);
		String entity = EntityUtils.toString(response.getEntity());
		CommonMO mo = gson.fromJson(entity, CommonMO.class);
		if (!mo.isSuccess()) {
			return;
		}
		doMaidi(mo);
	}

	private void doReady() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		HttpResponse response = doPost(httpUrl + readyUrl, null, querys);
		String entity = EntityUtils.toString(response.getEntity());
		CommonMO rmo = gson.fromJson(entity, CommonMO.class);
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
		HttpResponse response = doPost(httpUrl + readyToNextPanUrl, null, querys);
		String entity = EntityUtils.toString(response.getEntity());
		CommonMO rmo = gson.fromJson(entity, CommonMO.class);
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
		HttpResponse response = doPost(httpUrl + voteToFinishUrl, null, querys);
		String entity = EntityUtils.toString(response.getEntity());
		CommonMO rmo = gson.fromJson(entity, CommonMO.class);
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

	private void doMaidi(CommonMO mo) throws Exception {
		Map data = (Map) mo.getData();
		Map maidiInfoMap = (Map) data.get("maidiState");
		String maidiState = (String) maidiInfoMap.get(getPlayerId());
		if (maidiState != null && (maidiState.equals("startMaidi") || maidiState.equals("startDingdi"))) {
			Map<String, String> querys = new HashMap<>();
			querys.put("token", token);
			querys.put("yes", "false");
			HttpResponse response = doPost(httpUrl + maidiUrl, null, querys);
			String entity = EntityUtils.toString(response.getEntity());
			CommonMO rmo = gson.fromJson(entity, CommonMO.class);
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

	private void doAction(CommonMO mo) throws Exception {
		Map data = (Map) ((Map) mo.getData()).get("panActionFrame");
		int actionNo = ((Double) data.get("no")).intValue();
		Map panAfterAction = (Map) data.get("panAfterAction");
		List<MajiangPlayerAction> actionCandidates = new ArrayList<>();
		List<Map> playerList = (List) panAfterAction.get("playerList");
		for (Map majiangPlayer : playerList) {
			if (majiangPlayer.get("id").equals(getPlayerId())) {
				if (majiangPlayer.get("actionCandidates") != null) {
					List<Map> actionList = (List) majiangPlayer.get("actionCandidates");
					for (Map playerAction : actionList) {
						actionCandidates.add(new MajiangPlayerAction(playerAction));
					}
				}
			}
		}
		if (actionCandidates.isEmpty()) {
			return;
		}
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		querys.put("id", String.valueOf(actionCandidates.get(0).getId()));
		querys.put("actionNo", new Integer(actionNo + 1).toString());
		HttpResponse response = doPost(httpUrl + actionUrl, null, querys);
		String entity = EntityUtils.toString(response.getEntity());
		CommonMO rmo = gson.fromJson(entity, CommonMO.class);
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
