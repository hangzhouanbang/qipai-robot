package com.anbang.qipai.robot.cqrs.c.domain.majiang;

import java.util.Map;

public class MajiangPlayerAction {
	private int id;

	private String type;

	private String actionPlayerId;

	private boolean disabledByHigherPriorityAction;

	public MajiangPlayerAction(Map action) {
		id = ((Double) action.get("id")).intValue();
		type = (String) action.get("type");
		actionPlayerId = (String) action.get("actionPlayerId");
		disabledByHigherPriorityAction = (Boolean) action.get("disabledByHigherPriorityAction");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
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
