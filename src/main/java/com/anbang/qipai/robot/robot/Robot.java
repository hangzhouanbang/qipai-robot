package com.anbang.qipai.robot.robot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.anbang.qipai.robot.dao.dataObject.RobotMemberDbo;
import com.anbang.qipai.robot.robot.game.Game;
import com.anbang.qipai.robot.robot.websocket.RobotWebSocketClient;
import com.google.gson.Gson;

/**
 * 机器人的抽象类，保存游戏的必要信息和http连接
 * 
 * @author lsc
 *
 */
public abstract class Robot {

	private String robotId;// 机器人id
	private String playerId;// 玩家id
	private String nickname;// 玩家昵称
	private String headimgurl;// 玩家头像
	private String gender;// 玩家性别:男:male,女:female
	private String unionid;
	private String openid;
	protected String gameId;// 游戏id
	protected String token;// 游戏token
	private Game game;
	protected String httpUrl;
	protected String wsUrl;
	protected RobotWebSocketClient client;
	private HttpClient httpClient;
	protected Gson gson = new Gson();

	public Robot(Game game, String gameId, RobotMemberDbo robotDbo) throws ClientProtocolException, IOException {
		this.game = game;
		this.gameId = gameId;
		httpClient = HttpClients.createDefault();
		robotId = robotDbo.getId();
		playerId = robotDbo.getId();
		nickname = robotDbo.getNickname();
		headimgurl = robotDbo.getHeadimgurl();
		gender = robotDbo.getGender();
		unionid = robotDbo.getUnionid();
		openid = robotDbo.getOpenid();
		getGameToken();
	}

	private void getGameToken() throws ClientProtocolException, IOException {
		Map<String, String> querys = new HashMap<>();
		querys.put("unionid", unionid);
		querys.put("openid", openid);
		querys.put("nickname", nickname);
		querys.put("headimgurl", headimgurl);
		if (gender.equals("male")) {
			gender = "1";
		} else if (gender.equals("female")) {
			gender = "2";
		} else {
			gender = "0";
		}
		querys.put("sex", gender);
		HttpResponse response = doPost("", querys);
		Map map = gson.fromJson(EntityUtils.toString(response.getEntity()), Map.class);
		joinRoom((String) map.get("token"));
	}

	private void joinRoom(String token) throws ClientProtocolException, IOException {
		Map<String, String> querys = new HashMap<>();
		querys.put("token", token);
		querys.put("gameId", gameId);
		querys.put("game", game.name());

		HttpResponse response = doPost("", querys);
		Map map = gson.fromJson(EntityUtils.toString(response.getEntity()), Map.class);
		this.token = (String) map.get("token");
		wsUrl = (String) map.get("wsUrl");
		httpUrl = (String) map.get("httpUrl");
	}

	protected HttpResponse doPost(String url, Map<String, String> querys) throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(buildUrl(url, querys));
		return httpClient.execute(request);
	}

	private String buildUrl(String url, Map<String, String> querys) throws UnsupportedEncodingException {
		StringBuilder sbUrl = new StringBuilder();
		sbUrl.append(url);
		if (null != querys) {
			StringBuilder sbQuery = new StringBuilder();
			for (Map.Entry<String, String> query : querys.entrySet()) {
				if (0 < sbQuery.length()) {
					sbQuery.append("&");
				}
				if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
					sbQuery.append(query.getValue());
				}
				if (!StringUtils.isBlank(query.getKey())) {
					sbQuery.append(query.getKey());
					if (!StringUtils.isBlank(query.getValue())) {
						sbQuery.append("=");
						sbQuery.append(URLEncoder.encode(query.getValue(), "utf-8"));
					}
				}
			}
			if (0 < sbQuery.length()) {
				sbUrl.append("?").append(sbQuery);
			}
		}

		return sbUrl.toString();
	}

	public abstract void doScope(String scope) throws Exception;

	public String getRobotId() {
		return robotId;
	}

	public void setRobotId(String robotId) {
		this.robotId = robotId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getGameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public String getWsUrl() {
		return wsUrl;
	}

	public void setWsUrl(String wsUrl) {
		this.wsUrl = wsUrl;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getHttpUrl() {
		return httpUrl;
	}

	public void setHttpUrl(String httpUrl) {
		this.httpUrl = httpUrl;
	}

	public Gson getGson() {
		return gson;
	}

	public void setGson(Gson gson) {
		this.gson = gson;
	}

	public RobotWebSocketClient getClient() {
		return client;
	}

	public void setClient(RobotWebSocketClient client) {
		this.client = client;
	}

}
