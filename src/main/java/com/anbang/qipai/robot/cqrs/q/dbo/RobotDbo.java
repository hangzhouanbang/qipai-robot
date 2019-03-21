package com.anbang.qipai.robot.cqrs.q.dbo;

/**
 * @Author: 吴硕涵
 * @Date: 2019/1/31 12:18 PM
 * @Version 1.0
 */
public class RobotDbo {
	private String id;// 会员id
	private String nickname;// 会员昵称
	private String gender;// 会员性别:男:male,女:female
	private String headimgurl;// 头像url
	private String phone;// 会员手机
	private String idCard;// 身份证
	private boolean robot;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public boolean isRobot() {
		return robot;
	}

	public void setRobot(boolean robot) {
		this.robot = robot;
	}

}
