package com.anbang.qipai.robot.websocket.vo.doudizhu;

public enum PlayerQiangdizhuState {

	waitForJiaodizhu, // 等待叫地主

	waitForQiangdizhu, // 等待抢地主

	startQiangdizhu, // 开始抢地主

	startJiaodizhu, // 开始叫地主

	jiaodizhu, // 叫地主

	bujiao, // 不叫

	qiang, // 抢

	buqiang, // 不抢

	afterjiaodizhu, // 叫地主之后结束抢地主

	afterbujiao, // 不叫之后结束抢地主

	afterqiang, // 抢地主之后结束抢地主

	afterbuqiang, // 不抢之后结束抢地主

	over,// 结束

}
