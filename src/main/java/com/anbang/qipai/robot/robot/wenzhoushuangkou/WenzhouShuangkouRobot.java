package com.anbang.qipai.robot.robot.wenzhoushuangkou;

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
import com.anbang.qipai.robot.robot.puke.pai.PukePaiMian;
import com.anbang.qipai.robot.robot.puke.solution.DaPaiDianShuSolution;
import com.anbang.qipai.robot.robot.websocket.CommonMO;

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

	private Map<Integer, PukePaiMian> allShouPai = new HashMap<>();

	private int[] shoupaiDianShuAmountArray = new int[15];

	private Map<String, String[]> yaPaiSolutionCandidates = new HashMap<>();

	private Map<String, String[]> yaPaiSolutionsForTips = new HashMap<>();

	public WenzhouShuangkouRobot(Game game, String gameId, RobotMemberDbo robotDbo)
			throws ClientProtocolException, IOException {
		super(game, gameId, robotDbo);
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

	private void queryChaodiInfo() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("gameId", gameId);
		HttpResponse response = doPost(httpUrl + chaodiInfoUrl, querys);
		CommonMO mo = gson.fromJson(EntityUtils.toString(response.getEntity()), CommonMO.class);
		if (!mo.isSuccess()) {
			return;
		}
		doChaodi(mo);
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

	private void doChaodi(CommonMO mo) throws Exception {
		Map data = (Map) mo.getData();
		if (data.get("maidiState") == null) {
			return;
		}
		Map chaodiInfoMap = (Map) data.get("chaodiState");
		String chaodiState = (String) chaodiInfoMap.get(getPlayerId());
		if (chaodiState != null && chaodiState.equals("startChaodi")) {
			Map<String, String> querys = new HashMap<>();
			querys.put("token", token);
			querys.put("yes", "true");
			HttpResponse response = doPost(httpUrl + chaodiUrl, querys);
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

	private void doAction(CommonMO mo) throws Exception {
		Map data = (Map) mo.getData();
		Map panActionFrame = (Map) data.get("panActionFrame");
		Map panAfterAction = (Map) panActionFrame.get("panAfterAction");
		String actionPosition = (String) panAfterAction.get("actionPosition");
		String latestDapaiPlayerId = (String) panAfterAction.get("latestDapaiPlayerId");
		((List) panAfterAction.get("shuangkouPlayerList")).forEach((player) -> {
			Map shuangkouPlayer = (Map) player;
			String playerId = (String) shuangkouPlayer.get("id");
			String playerPosition = (String) shuangkouPlayer.get("position");
			playerIdPositionMap.put(playerId, playerPosition);
			if (playerId.equals(getPlayerId())) {
				shoupaiDianShuAmountArray = (int[]) shuangkouPlayer.get("shoupaiDianShuAmountArray");
				Map allShoupai = (Map) shuangkouPlayer.get("allShoupai");
				((List) allShoupai.get("allShoupai")).forEach((pukePai) -> {
					Map pai = (Map) pukePai;
					int paiId = ((Double) shuangkouPlayer.get("id")).intValue();
					PukePaiMian pukePaiMian = new PukePaiMian();
					Map paiMian = (Map) shuangkouPlayer.get("paiMian");
					pukePaiMian.setHuaSe((String) paiMian.get("huaSe"));
					pukePaiMian.setDianShu((String) paiMian.get("dianShu"));
					allShouPai.put(paiId, pukePaiMian);
				});
				((List) shuangkouPlayer.get("yaPaiSolutionCandidates")).forEach((solution) -> {
					Map daPaiDianShuSolution = (Map) solution;
					String dianshuZuheIdx = (String) daPaiDianShuSolution.get("dianshuZuheIdx");
					yaPaiSolutionCandidates.put(dianshuZuheIdx,
							(String[]) daPaiDianShuSolution.get("dachuDianShuArray"));
				});
				((List) shuangkouPlayer.get("yaPaiSolutionsForTips")).forEach((solution) -> {
					Map daPaiDianShuSolution = (Map) solution;
					String dianshuZuheIdx = (String) daPaiDianShuSolution.get("dianshuZuheIdx");
					yaPaiSolutionsForTips.put(dianshuZuheIdx, (String[]) daPaiDianShuSolution.get("dachuDianShuArray"));
				});
			}
		});
		if (!actionPosition.equals(playerIdPositionMap.get(getPlayerId()))) {// 没轮到自己出牌
			return;
		}
		String postion = playerIdPositionMap.get(latestDapaiPlayerId);
		if (playerIdPositionMap.size() != 2 && postion.equals(positionPositionMap.get(actionPosition))) {// 最后出牌的是对家
			return;
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
			String[] dachuDianShuArray = yaPaiSolutionsForTips.get(dianshuZuheIdx);
			for (int i = 0; i < dachuDianShuArray.length; i++) {
				String dianshu = dachuDianShuArray[i];
				for (Integer paiId : allShouPai.keySet()) {
					PukePaiMian paimian = allShouPai.get(paiId);
					if (dianshu.equals(paimian.getDianShu()) && !paiIds.contains(paiId)) {
						paiIds.add(paiId);
					}
				}
			}
			da(paiIds, dianshuZuheIdx);
		} else if (!yaPaiSolutionCandidates.isEmpty()) {
			List<String> indexList = new ArrayList<>(yaPaiSolutionCandidates.keySet());
			dianshuZuheIdx = indexList.get(0);
			String[] dachuDianShuArray = yaPaiSolutionCandidates.get(dianshuZuheIdx);
			for (int i = 0; i < dachuDianShuArray.length; i++) {
				String dianshu = dachuDianShuArray[i];
				for (Integer paiId : allShouPai.keySet()) {
					PukePaiMian paimian = allShouPai.get(paiId);
					if (dianshu.equals(paimian.getDianShu()) && !paiIds.contains(paiId)) {
						paiIds.add(paiId);
					}
				}
			}
			da(paiIds, dianshuZuheIdx);
		} else {
			guo();
		}
	}

	private void da(List<Integer> paiIds, String dianshuZuheIdx) throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		querys.put("paiIds", gson.toJson(paiIds));
		querys.put("dianshuZuheIdx", dianshuZuheIdx);
		HttpResponse response = doPost(httpUrl + daActionUrl, querys);
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

	private void guo() throws Exception {
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		HttpResponse response = doPost(httpUrl + guoActionUrl, querys);
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
