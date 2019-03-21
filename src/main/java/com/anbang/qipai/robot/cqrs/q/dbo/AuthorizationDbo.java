package com.anbang.qipai.robot.cqrs.q.dbo;

/**
 * 用户授权数据库对象。合并账号密码类授权和三方授权，方便数据库查询。
 * 
 * @author neo
 *
 */
public class AuthorizationDbo {

	private String id;

	private String memberId;

	/**
	 * 是否三方授权
	 */
	private boolean thirdAuth;

	private String account;

	private String password;

	/**
	 * 三方授权发布者
	 */
	private String publisher;

	/**
	 * 三方授权的uuid
	 */
	private String uuid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public boolean isThirdAuth() {
		return thirdAuth;
	}

	public void setThirdAuth(boolean thirdAuth) {
		this.thirdAuth = thirdAuth;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

}
