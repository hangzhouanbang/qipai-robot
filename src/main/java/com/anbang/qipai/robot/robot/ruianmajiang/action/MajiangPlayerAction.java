package com.anbang.qipai.robot.robot.ruianmajiang.action;

import java.util.Map;

public class MajiangPlayerAction {
	private String id;

	private String type;

	private String actionPlayerId;

	private boolean disabledByHigherPriorityAction;

	public MajiangPlayerAction(Map action) {
		id = (String) action.get("id");
		type = (String) action.get("type");
		actionPlayerId = (String) action.get("actionPlayerId");
		disabledByHigherPriorityAction = Boolean.parseBoolean((String) action.get("disabledByHigherPriorityAction"));
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getActionPlayerId() {
		return actionPlayerId;
	}

	public void setActionPlayerId(String actionPlayerId) {
		this.actionPlayerId = actionPlayerId;
	}

	public boolean isDisabledByHigherPriorityAction() {
		return disabledByHigherPriorityAction;
	}

	public void setDisabledByHigherPriorityAction(boolean disabledByHigherPriorityAction) {
		this.disabledByHigherPriorityAction = disabledByHigherPriorityAction;
	}

}
