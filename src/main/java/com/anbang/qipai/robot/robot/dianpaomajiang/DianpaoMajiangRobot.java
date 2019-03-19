package com.anbang.qipai.robot.robot.dianpaomajiang;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.anbang.qipai.robot.dao.dataObject.RobotMemberDbo;
import com.anbang.qipai.robot.robot.Robot;
import com.anbang.qipai.robot.robot.game.Game;

public class DianpaoMajiangRobot extends Robot {

	public DianpaoMajiangRobot(Game game, String gameId, RobotMemberDbo robotDbo)
			throws ClientProtocolException, IOException {
		super(game, gameId, robotDbo);
	}

	@Override
	public void doScope(String scope) {
		// TODO Auto-generated method stub

	}

}
