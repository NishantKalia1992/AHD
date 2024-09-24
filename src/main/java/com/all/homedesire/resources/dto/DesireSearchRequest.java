package com.all.homedesire.resources.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class is created to handle common search parameters.
 * 
 * @author Arun Kumar Tiwari
 * @version 1.0
 */
public class DesireSearchRequest {
	@JsonProperty("page_number")
	private int pageNumber;
	@JsonProperty("page_size")
	private int pageSize;
	@JsonProperty("order_by")
	private String orderBy;

	@JsonProperty("user_id")
	private long userId;

	@JsonProperty("user_type")
	private String userType;

	@JsonProperty("is_active")
	private Boolean isActive;

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

}
