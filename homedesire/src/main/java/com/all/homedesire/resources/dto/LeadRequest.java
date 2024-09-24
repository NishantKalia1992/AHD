package com.all.homedesire.resources.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LeadRequest {

	@JsonProperty("lead_id")
	private long leadId;
	
	@JsonProperty("is_publish")
	private boolean isPublish;

	public long getLeadId() {
		return leadId;
	}

	public void setLeadId(long leadId) {
		this.leadId = leadId;
	}

	public boolean isPublish() {
		return isPublish;
	}

	public void setPublish(boolean isPublish) {
		this.isPublish = isPublish;
	}

}
