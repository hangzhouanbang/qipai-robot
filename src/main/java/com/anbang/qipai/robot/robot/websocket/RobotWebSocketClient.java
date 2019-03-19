package com.anbang.qipai.robot.robot.websocket;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.anbang.qipai.robot.robot.Robot;
import com.google.gson.Gson;

/**
 * 通用的机器人websocket
 * 
 * @author lsc
 *
 */
public class RobotWebSocketClient extends WebSocketClient {

	private Robot robot;

	private Gson gson = new Gson();

	public RobotWebSocketClient(URI serverUri, Robot robot) {
		super(serverUri);
		this.robot = robot;
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		for (Iterator<String> it = handshakedata.iterateHttpFields(); it.hasNext();) {
			String key = it.next();
			System.out.println(key + ":" + handshakedata.getFieldValue(key));
		}
	}

	@Override
	public void onMessage(String message) {
		CommonMO mo = gson.fromJson(message, CommonMO.class);
		String msg = mo.getMsg();
		if (msg.equals("bindPlayer")) {
			bindPlayer();
		} else if (msg.equals("query")) {
			Map data = (Map) mo.getData();
			String scope = (String) data.get("scope");
			try {
				robot.doScope(scope);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(Exception ex) {
		doClose();
	}

	private void bindPlayer() {
		Map data = new HashMap<>();
		data.put("token", robot.getToken());
		data.put("gameId", robot.getGameId());
		send(gson.toJson(data));
	}

	public void doClose() {
		this.close();
	}
}
