package com.all.homedesire.resources.dto.property;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CommonLeadRequest {
	@JsonProperty("search_text")
	private String searchText;

	@JsonProperty("address")
	private String address;

	@JsonProperty("type")
	private String type;

	@JsonProperty("status")
	private String status;

	@JsonProperty("price")
	private double price;

	@JsonProperty("page_number")
	private int pageNumber;

	@JsonProperty("page_size")
	private int pageSize;

	@JsonProperty("order_by")
	private String orderBy;

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

}
