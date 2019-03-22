package com.anbang.qipai.robot.cqrs.c.domain.wenzhoushuangkou;

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
import com.anbang.qipai.robot.cqrs.c.domain.puke.DaPaiDianShuSolution;
import com.anbang.qipai.robot.cqrs.q.dbo.RobotDbo;

public class WenzhouShuangkouRobot extends Robot {

	private final String readyUrl = "/game/ready";

	private final String gameInfoUrl = "/game/info";

	private final String chaodiInfoUrl = "/game/chaodi_info";

	private final String panForMeUrl = "/pk/pan_action_frame_for_me";

	private final String daActionUrl = "/pk/da";

	private final String guoActionUrl = "/pk/guo";

	private final String chaodiUrl = "/pk/chaodi";

	private final String readyToNextPanUrl = "/pk/ready_to_next_pan";

	private final String voteInfoUrl = "/game/finish_vote_info";

	private final String voteToFinishUrl = "/game/vote_to_finish";

	private String gameState;

	private String state;

	private String onlineState;

	private Map<String, String> playerIdPositionMap = new HashMap<>();

	private Map<String, String> positionPositionMap = new HashMap<>();

	public WenzhouShuangkouRobot(Game game, String gameId, RobotDbo robotDbo, String unionid, String openid)
			throws ClientProtocolException, IOException, RobotLoginException, JoinGameException {
		super(game, gameId, robotDbo, unionid, openid);
		positionPositionMap.put("dong", "xi");
		positionPositionMap.put("nan", "bei");
		positionPositionMap.put("xi", "dong");
		positionPositionMap.put("bei", "nan");
	}

	public WenzhouShuangkouRobot(Game game, String gameId, String playerId, String nickname, String headimgurl,
			String gender, String token) {
		super(game, gameId, playerId, nickname, headimgurl, gender, token);
		this.httpUrl = RobotConfig.WZSK_HTTP_URL;
		this.wsUrl = RobotConfig.WZSK_WS_URL;
		positionPositionMap.put("dong", "xi");
		positionPositionMap.put("nan", "bei");
		positionPositionMap.put("xi", "dong");
		positionPositionMap.put("bei", "nan");
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
		} else if (scope.equals("chaodiInfo")) {
			queryChaodiInfo();
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
		} else if (gameState != null && gameState.equals("startchaodi")) {
			queryChaodiInfo();
		}
	}

	private void queryChaodiInfo() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("gameId", gameId);
		HttpResponse response = doPost(httpUrl + chaodiInfoUrl, null, querys);
		String entity = EntityUtils.toString(response.getEntity());
		CommonMO mo = gson.fromJson(entity, CommonMO.class);
		if (!mo.isSuccess()) {
			return;
		}
		doChaodi(mo);
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

	private void doChaodi(CommonMO mo) throws Exception {
		Map data = (Map) mo.getData();
		Map chaodiInfoMap = (Map) data.get("chaodiState");
		String chaodiState = (String) chaodiInfoMap.get(getPlayerId());
		if (chaodiState != null && chaodiState.equals("startChaodi")) {
			Map<String, String> querys = new HashMap<>();
			querys.put("token", token);
			querys.put("yes", "true");
			HttpResponse response = doPost(httpUrl + chaodiUrl, null, querys);
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
		Map data = (Map) mo.getData();
		Map panActionFrame = (Map) data.get("panActionFrame");
		Map panAfterAction = (Map) panActionFrame.get("panAfterAction");
		String actionPosition = (String) panAfterAction.get("actionPosition");
		if (!playerIdPositionMap.isEmpty() && !actionPosition.equals(playerIdPositionMap.get(getPlayerId()))) {// 没轮到自己出牌
			return;
		}
		String latestDapaiPlayerId = null;
		if (panAfterAction.get("latestDapaiPlayerId") != null) {
			latestDapaiPlayerId = (String) panAfterAction.get("latestDapaiPlayerId");
		}
		Map<Integer, String> allShouPai = new HashMap<>();

		int[] shoupaiDianShuAmountArray = new int[15];

		Map<String, List<String>> yaPaiSolutionCandidates = new HashMap<>();

		Map<String, List<String>> yaPaiSolutionsForTips = new HashMap<>();

		List<Map> shuangkouPlayerList = (List) panAfterAction.get("shuangkouPlayerList");
		for (Map shuangkouPlayer : shuangkouPlayerList) {
			String playerId = (String) shuangkouPlayer.get("id");
			String playerPosition = (String) shuangkouPlayer.get("position");
			playerIdPositionMap.put(playerId, playerPosition);
			if (playerId.equals(getPlayerId())) {
				List<Double> dianshuList = (List) shuangkouPlayer.get("shoupaiDianShuAmountArray");
				for (int i = 0; i < dianshuList.size(); i++) {
					shoupaiDianShuAmountArray[i] = dianshuList.get(i).intValue();
				}
				Map allShoupai = (Map) shuangkouPlayer.get("allShoupai");
				List<Map> pukePaiList = (List) allShoupai.get("allShoupai");
				for (Map pukePai : pukePaiList) {
					int paiId = ((Double) pukePai.get("id")).intValue();
					String paiMian = (String) pukePai.get("paiMian");
					allShouPai.put(paiId, paiMian);
				}
				List<Map> yaPaiSolutionList = (List) shuangkouPlayer.get("yaPaiSolutionCandidates");
				for (Map solution : yaPaiSolutionList) {
					String dianshuZuheIdx = (String) solution.get("dianshuZuheIdx");
					List<String> dianshuArrayList = (List) solution.get("dachuDianShuArray");
					yaPaiSolutionCandidates.put(dianshuZuheIdx, dianshuArrayList);
				}
				List<Map> yaPaiSolutionsForTipsList = (List) shuangkouPlayer.get("yaPaiSolutionsForTips");
				for (Map solution : yaPaiSolutionsForTipsList) {
					String dianshuZuheIdx = (String) solution.get("dianshuZuheIdx");
					List<String> dianshuArrayList = (List) solution.get("dachuDianShuArray");
					yaPaiSolutionsForTips.put(dianshuZuheIdx, dianshuArrayList);
				}
			}
		}
		if (!actionPosition.equals(playerIdPositionMap.get(getPlayerId()))) {// 没轮到自己出牌
			return;
		}
		if (latestDapaiPlayerId != null) {
			String postion = playerIdPositionMap.get(latestDapaiPlayerId);
			if (playerIdPositionMap.size() != 2 && postion.equals(positionPositionMap.get(actionPosition))) {// 最后出牌的是对家
				guo();
			}
		}
		if (yaPaiSolutionCandidates == null || yaPaiSolutionCandidates.isEmpty()) {// 不能出牌
			guo();
		}
		List<Integer> paiIds = new ArrayList<>();
		String dianshuZuheIdx = DaPaiDianShuSolution.calculateDianshuZuheIdx(shoupaiDianShuAmountArray);
		if (dianshuZuheIdx != null && yaPaiSolutionCandidates.containsKey(dianshuZuheIdx)) {// 出完所有牌
			for (Integer paiId : allShouPai.keySet()) {
				paiIds.add(paiId);
			}
			da(paiIds, dianshuZuheIdx);
		} else if (!yaPaiSolutionsForTips.isEmpty()) {
			List<String> indexList = new ArrayList<>(yaPaiSolutionsForTips.keySet());
			dianshuZuheIdx = indexList.get(0);
			List<String> dachuDianShuArray = yaPaiSolutionsForTips.get(dianshuZuheIdx);
			for (int i = 0; i < dachuDianShuArray.size(); i++) {
				String dianshu = dachuDianShuArray.get(i);
				for (Integer paiId : allShouPai.keySet()) {
					String paimian = allShouPai.get(paiId);
					if (paimian.contains(dianshu) && !paiIds.contains(paiId)) {
						paiIds.add(paiId);
						allShouPai.remove(paiId);
						break;
					}
				}
			}
			da(paiIds, dianshuZuheIdx);
		} else if (!yaPaiSolutionCandidates.isEmpty()) {
			List<String> indexList = new ArrayList<>(yaPaiSolutionCandidates.keySet());
			dianshuZuheIdx = indexList.get(0);
			List<String> dachuDianShuArray = yaPaiSolutionCandidates.get(dianshuZuheIdx);
			for (int i = 0; i < dachuDianShuArray.size(); i++) {
				String dianshu = dachuDianShuArray.get(i);
				for (Integer paiId : allShouPai.keySet()) {
					String paimian = allShouPai.get(paiId);
					if (paimian.contains(dianshu) && !paiIds.contains(paiId)) {
						paiIds.add(paiId);
						allShouPai.remove(paiId);
						break;
					}
				}
			}
			da(paiIds, dianshuZuheIdx);
		} else {
			guo();
		}
	}

	private void da(List<Integer> paiIds, String dianshuZuheIdx) throws Exception {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json;charset=UTF-8");
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		// querys.put("paiIds", gson.toJson(paiIds));
		querys.put("dianshuZuheIdx", dianshuZuheIdx);
		HttpResponse response = doPost(httpUrl + daActionUrl, headers, querys, gson.toJson(paiIds));
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

	private void guo() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		HttpResponse response = doPost(httpUrl + guoActionUrl, null, querys);
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
