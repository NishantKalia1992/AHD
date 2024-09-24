package com.all.homedesire.resources.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PreferredRequest {

	@JsonProperty("type")
	private String type;

	@JsonProperty("purpose")
	private String purpose;

	@JsonProperty("state")
	private String state;

	@JsonProperty("city")
	private String city;

	@JsonProperty("area")
	private String area;

	@JsonProperty("price")
	private double price;
	
	@JsonProperty("page_number")
	private int pageNumber;

	@JsonProperty("page_size")
	private int pageSize;

	@JsonProperty("order_by")
	private String orderBy;
	
	@JsonProperty("keyboard_search")
	private String keyboardSearch;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
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

	public String getKeyboardSearch() {
		return keyboardSearch;
	}

	public void setKeyboardSearch(String keyboardSearch) {
		this.keyboardSearch = keyboardSearch;
	}

}
