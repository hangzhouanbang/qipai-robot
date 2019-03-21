package com.anbang.qipai.robot.msg.msjobj;

public class CommonMO {

	private String msg;

	private Object data;

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "CommonMO{" +
				"msg='" + msg + '\'' +
				", data=" + data +
				'}';
	}
}
