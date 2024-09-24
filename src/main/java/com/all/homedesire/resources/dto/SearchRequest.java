package com.all.homedesire.resources.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchRequest {

	@JsonProperty("search_type")
	private String searchType;

	@JsonProperty("purpose_id")
	private long purposeId;

	@JsonProperty("type_id")
	private long typeId;

	@JsonProperty("state_name")
	private String stateName;

	@JsonProperty("city_name")
	private String cityName;

	@JsonProperty("area_name")
	private String areaName;

	@JsonProperty("budget")
	private double budget;

	@JsonProperty("page_number")
	private int pageNumber;

	@JsonProperty("page_size")
	private int pageSize;

	@JsonProperty("order_by")
	private String orderBy;

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public long getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(long purposeId) {
		this.purposeId = purposeId;
	}

	public long getTypeId() {
		return typeId;
	}

	public void setTypeId(long typeId) {
		this.typeId = typeId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public double getBudget() {
		return budget;
	}

	public void setBudget(double budget) {
		this.budget = budget;
	}

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

}
