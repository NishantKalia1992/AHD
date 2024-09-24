package com.all.homedesire.resources.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangeStateRequest {
	@JsonProperty("id")
	private long id;
	@JsonProperty("active_status")
	private boolean isActive;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
