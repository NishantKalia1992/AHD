package com.all.homedesire.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchProperty {
	@JsonProperty("keywords")
	private String keywords;
	@JsonProperty("type_id")
	private Long typeId;
	@JsonProperty("purpose_id")
	private Long purposeId;
	@JsonProperty("address")
	private String address;
	@JsonProperty("min_price")
	private Double minPrice;
	@JsonProperty("max_price")
	private Double maxPrice;
	@JsonProperty("page_number")
	private int pageNumber;
	@JsonProperty("page_size")
	private int pageSize;
	@JsonProperty("order_by")
	private String orderBy;

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public Long getTypeId() {
		return typeId;
	}

	public void setTypeId(Long typeId) {
		this.typeId = typeId;
	}

	public Long getPurposeId() {
		return purposeId;
	}

	public void setPurposeId(Long purposeId) {
		this.purposeId = purposeId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Double getMinPrice() {
		return minPrice;
	}

	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}

	public Double getMaxPrice() {
		return maxPrice;
	}

	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
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
